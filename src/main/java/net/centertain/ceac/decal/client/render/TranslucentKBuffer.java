package net.centertain.ceac.decal.client.render;

import com.mojang.blaze3d.shaders.Uniform;
import net.centertain.ceac.decal.Decal;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

public final class TranslucentKBuffer {
    public static final int LAYERS = 4;

    private static final int FRAGMENT_BYTES = Integer.BYTES * 3;

    private static final float CELL_SIZE = 1.1f;
    private static final float HALF_VOLUME = 1.1f / 2.0f;

    private static int width;
    private static int height;

    private static int fragmentBuffer;
    private static int lockBuffer;
    private static int decalBuffer;
    private static int decalCapacity;

    private static int cellBuffer;
    private static int cellCapacity;
    private static int cellCount;

    private static int decalIndexBuffer;
    private static int decalIndexCapacity;

    private static volatile boolean spatialIndexDirty = true;

    private static boolean initialized;

    private TranslucentKBuffer() {}

    public static void init(int w, int h) {
        width = w;
        height = h;

        destroy();

        int pixels = width * height;

        fragmentBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, fragmentBuffer);

        long fragmentBytes = (long) pixels * LAYERS * FRAGMENT_BYTES;

        GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, fragmentBytes, GL15.GL_DYNAMIC_DRAW);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, fragmentBuffer);

        lockBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, lockBuffer);

        long lockBytes = (long) pixels * Integer.BYTES;

        GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, lockBytes, GL15.GL_DYNAMIC_DRAW);

        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);

        initialized = true;
        spatialIndexDirty = true;
        clear();
    }

    public static void resize(int w, int h) {
        if (w == width && h == height && initialized)
            return;
        init(w, h);
    }

    public static void uploadDecals(Collection<Decal> decals) {
        List<Decal> decalList = new ArrayList<>(decals);
        int count = decalList.size();
        if (count == 0)
            return;
        if (decalBuffer == 0 || decalCapacity < count) {
            if (decalBuffer != 0)
                GL15.glDeleteBuffers(decalBuffer);

            decalBuffer = GL15.glGenBuffers();
            decalCapacity = Math.max(count, 64);

            GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, decalBuffer);
            GL15.glBufferData(
                    GL43.GL_SHADER_STORAGE_BUFFER,
                    (long) decalCapacity * 12L * Float.BYTES,
                    GL15.GL_DYNAMIC_DRAW
            );
        } else {
            GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, decalBuffer);
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer data = stack.mallocFloat(count * 12);

            for (Decal decal : decalList) {
                Vec3 origin = decal.getOrigin();
                Vec3 normal = decal.getNormal();

                double width = decal.getPixelWidth() / 16.0;
                double height = decal.getPixelHeight() / 16.0;
                double depth = decal.getBlockDepth();

                // Origin
                data.put((float) origin.x);
                data.put((float) origin.y);
                data.put((float) origin.z);
                data.put(0.0f);

                // Normal
                data.put((float) normal.x);
                data.put((float) normal.y);
                data.put((float) normal.z);
                data.put(0.0f);

                // Volume + rotation
                data.put((float) width);
                data.put((float) height);
                data.put((float) depth);
                data.put((float) (decal.getRotation() & 0xFF));
            }
            data.flip();
            GL15.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, 0, data);
        }
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 2, decalBuffer);
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
        if (spatialIndexDirty)
            rebuildSpatialIndex(decalList);
    }

    private static void rebuildSpatialIndex(
            List<Decal> decals
    ) {
        Map<CellKey, List<Integer>> cells = new HashMap<>();

        for (int decalIndex = 0; decalIndex < decals.size(); ++decalIndex) {
            Decal decal = decals.get(decalIndex);
            Vec3 origin = decal.getOrigin();

            int minX = (int) Math.floor((origin.x - HALF_VOLUME) / CELL_SIZE);
            int minY = (int) Math.floor((origin.y - HALF_VOLUME) / CELL_SIZE);
            int minZ = (int) Math.floor((origin.z - HALF_VOLUME) / CELL_SIZE);

            int maxX = (int) Math.floor((origin.x + HALF_VOLUME) / CELL_SIZE);
            int maxY = (int) Math.floor((origin.y + HALF_VOLUME) / CELL_SIZE);
            int maxZ = (int) Math.floor((origin.z + HALF_VOLUME) / CELL_SIZE);

            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    for (int z = minZ; z <= maxZ; ++z) {
                        CellKey key = new CellKey(x, y, z);

                        cells.computeIfAbsent(
                                key,
                                ignored -> new ArrayList<>()
                        ).add(decalIndex);
                    }
                }
            }
        }

        List<CellKey> keys = new ArrayList<>(cells.keySet());

        keys.sort(
                Comparator
                        .comparingInt(CellKey::x)
                        .thenComparingInt(CellKey::y)
                        .thenComparingInt(CellKey::z)
        );

        cellCount = keys.size();

        if (cellBuffer == 0 || cellCapacity < cellCount) {
            if (cellBuffer != 0)
                GL15.glDeleteBuffers(cellBuffer);

            cellBuffer = GL15.glGenBuffers();
            cellCapacity = Math.max(cellCount, 64);

            GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, cellBuffer);
            GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, (long) cellCapacity * 32L, GL15.GL_DYNAMIC_DRAW);
        }

        int totalIndices = 0;

        for (CellKey key : keys)
            totalIndices += cells.get(key).size();

        if (decalIndexBuffer == 0 || decalIndexCapacity < totalIndices) {
            if (decalIndexBuffer != 0)
                GL15.glDeleteBuffers(decalIndexBuffer);

            decalIndexBuffer = GL15.glGenBuffers();
            decalIndexCapacity = Math.max(totalIndices, 64);

            GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, decalIndexBuffer);
            GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, (long) decalIndexCapacity * Integer.BYTES, GL15.GL_DYNAMIC_DRAW);
        }

        IntBuffer cellData = MemoryUtil.memAllocInt(cellCapacity * 8);
        IntBuffer indexData = MemoryUtil.memAllocInt(totalIndices);

        try {
            for (int i = 0; i < keys.size(); ++i) {
                CellKey key = keys.get(i);
                List<Integer> indices = cells.get(key);

                int base = i * 8;
                int offset = indexData.position();

                cellData.put(base, key.x);
                cellData.put(base + 1, key.y);
                cellData.put(base + 2, key.z);
                cellData.put(base + 3, 0);

                cellData.put(base + 4, offset);
                cellData.put(base + 5, indices.size());
                cellData.put(base + 6, 0);
                cellData.put(base + 7, 0);

                for (int decalIndex : indices)
                    indexData.put(decalIndex);
            }

            for (int i = keys.size(); i < cellCapacity; ++i) {
                int base = i * 8;

                cellData.put(base, Integer.MAX_VALUE);
                cellData.put(base + 1, 0);
                cellData.put(base + 2, 0);
                cellData.put(base + 3, 0);

                cellData.put(base + 4, 0);
                cellData.put(base + 5, 0);
                cellData.put(base + 6, 0);
                cellData.put(base + 7, 0);
            }

            cellData.position(0);
            cellData.limit(cellCapacity * 8);

            indexData.flip();

            GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, cellBuffer);
            GL15.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, 0, cellData);
            GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, decalIndexBuffer);
            GL15.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, 0, indexData);
        } finally {
            MemoryUtil.memFree(cellData);
            MemoryUtil.memFree(indexData);
        }
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 3, cellBuffer);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 4, decalIndexBuffer);
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
        spatialIndexDirty = false;
    }

    public static void markSpatialIndexDirty() {
        spatialIndexDirty = true;
    }

    public static void clear() {
        if (!initialized)
            return;
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, lockBuffer);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer zero = stack.callocInt(1);
            GL43.glClearBufferData(
                    GL43.GL_SHADER_STORAGE_BUFFER,
                    GL30.GL_R32UI,
                    GL30.GL_RED_INTEGER,
                    GL11.GL_UNSIGNED_INT,
                    zero
            );
        }
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
    }

    public static void bind() {
        if (!initialized)
            return;
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, fragmentBuffer);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 1, lockBuffer);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 3, cellBuffer);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 4, decalIndexBuffer);
    }

    public static void setShaderUniforms(ShaderInstance shader) {
        Uniform uniform = shader.getUniform("ScreenSize");
        if (uniform != null)
            uniform.set((float) width, (float) height);
        Uniform cellSize = shader.getUniform("CellSize");
        if (cellSize != null)
            cellSize.set(CELL_SIZE);
        Uniform cellCountUniform = shader.getUniform("CellCount");
        if (cellCountUniform != null)
            cellCountUniform.set((float) cellCount);
    }

    public static void barrier() {
        GL43.glMemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);
    }

    public static boolean isInitialized() {
        return initialized;
    }
    public static int getWidth() {
        return width;
    }
    public static int getHeight() {
        return height;
    }

    public static void destroy() {
        if (fragmentBuffer != 0) {
            GL15.glDeleteBuffers(fragmentBuffer);
            fragmentBuffer = 0;
        }
        if (lockBuffer != 0) {
            GL15.glDeleteBuffers(lockBuffer);
            lockBuffer = 0;
        }
        if (decalBuffer != 0) {
            GL15.glDeleteBuffers(decalBuffer);
            decalBuffer = 0;
        }
        if (cellBuffer != 0) {
            GL15.glDeleteBuffers(cellBuffer);
            cellBuffer = 0;
        }
        if (decalIndexBuffer != 0) {
            GL15.glDeleteBuffers(decalIndexBuffer);
            decalIndexBuffer = 0;
        }

        decalCapacity = 0;
        cellCapacity = 0;
        decalIndexCapacity = 0;
        cellCount = 0;

        spatialIndexDirty = true;
        initialized = false;
    }

    private record CellKey(
            int x,
            int y,
            int z
    ) {}
}
