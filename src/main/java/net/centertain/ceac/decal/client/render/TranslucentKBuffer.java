package net.centertain.ceac.decal.client.render;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class TranslucentKBuffer {
    public static final int LAYERS = 4;

    private static final int FRAGMENT_BYTES = Integer.BYTES;

    private static int width;
    private static int height;

    private static int fragmentBuffer;

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
        if (!initialized || fragmentBuffer == 0)
            return;

        ByteBuffer clearValue = ByteBuffer.allocateDirect(Integer.BYTES).order(ByteOrder.nativeOrder());

        clearValue.putInt(0xFFFFFFFF);
        clearValue.flip();

        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, fragmentBuffer);
        GL43.glClearBufferData(
                GL43.GL_SHADER_STORAGE_BUFFER,
                GL30.GL_R32UI,
                GL30.GL_RED_INTEGER,
                GL11.GL_UNSIGNED_INT,
                clearValue
        );
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
    }

    public static void bind() {
        if (!initialized)
            return;
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, fragmentBuffer);
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
        initialized = false;
    }
}
