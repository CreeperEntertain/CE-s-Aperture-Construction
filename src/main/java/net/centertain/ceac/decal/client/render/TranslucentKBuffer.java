package net.centertain.ceac.decal.client.render;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

public final class TranslucentKBuffer {
    public static final int LAYERS = 4;

    private static final int FRAGMENT_BYTES = Integer.BYTES * 2;

    private static int width;
    private static int height;

    private static int fragmentBuffer;
    private static int lockBuffer;

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
        clear();
    }

    public static void resize(int w, int h) {
        if (w == width && h == height && initialized)
            return;
        init(w, h);
    }

    public static void clear() {
        if (!initialized)
            return;

        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, fragmentBuffer);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer fragmentClear = stack.mallocInt(1);
            fragmentClear.put(0, -1);

            GL43.glClearBufferData(
                    GL43.GL_SHADER_STORAGE_BUFFER,
                    GL30.GL_R32UI,
                    GL30.GL_RED_INTEGER,
                    GL11.GL_UNSIGNED_INT,
                    fragmentClear
            );
        }

        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, lockBuffer);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer lockClear = stack.callocInt(1);

            GL43.glClearBufferData(
                    GL43.GL_SHADER_STORAGE_BUFFER,
                    GL30.GL_R32UI,
                    GL30.GL_RED_INTEGER,
                    GL11.GL_UNSIGNED_INT,
                    lockClear
            );
        }

        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
    }

    public static void bind() {
        if (!initialized)
            return;
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, fragmentBuffer);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 1, lockBuffer);
    }

    public static void setShaderUniforms(ShaderInstance shader) {
        Uniform uniform = shader.getUniform("ScreenSize");
        if (uniform != null)
            uniform.set((float) width, (float) height);
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
        initialized = false;
    }
}
