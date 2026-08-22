package net.centertain.ceac.decal.client.render;

import net.minecraft.client.renderer.ShaderInstance;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL43;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class TranslucentKBuffer {
    public static final int LAYERS = 4;

    private static final int FRAGMENT_BYTES = 4;

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
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 1, lockBuffer);

        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);

        clear();
        initialized = true;
    }

    public static void resize(int w, int h) {
        if (w == width && h == height && initialized)
            return;
        init(w, h);
    }

    public static void clear() {
        if (!initialized && fragmentBuffer == 0)
            return;

        int pixels = width * height;

        // Depth = 0xffffffff means empty.
        // Color = 0.
        ByteBuffer fragmentData = ByteBuffer.allocateDirect(
                pixels * LAYERS * FRAGMENT_BYTES
        ).order(ByteOrder.nativeOrder());

        for (int i = 0; i < pixels * LAYERS; i++) {
            fragmentData.putInt(0xFFFFFFFF);
        }
        fragmentData.flip();

        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, fragmentBuffer);
        GL15.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, 0, fragmentData);

        ByteBuffer lockData = ByteBuffer.allocateDirect(
                pixels * Integer.BYTES
        ).order(ByteOrder.nativeOrder());

        for (int i = 0; i < pixels; i++)
            lockData.putInt(0);
        lockData.flip();

        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, lockBuffer);
        GL15.glBufferSubData(GL43.GL_SHADER_STORAGE_BUFFER, 0, lockData);
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
    }

    public static void bind() {
        if (!initialized)
            return;
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, fragmentBuffer);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 1, lockBuffer);
    }

    public static void setShaderUniforms(ShaderInstance shader) {
        var uniform = shader.getUniform("ScreenSize");
        if (uniform != null)
            uniform.set((float) width, (float) height);
    }

    public static void barrier() {
        GL43.glMemoryBarrier(
                GL43.GL_SHADER_STORAGE_BARRIER_BIT |
                GL43.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT |
                GL43.GL_FRAMEBUFFER_BARRIER_BIT
        );
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
