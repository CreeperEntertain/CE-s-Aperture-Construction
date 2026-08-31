package net.centertain.ceac.decal.client.render.compute;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

public final class DecalOcclusionShader {
    private int program;

    private int viewProjectionLocation;
    private int decalCountLocation;
    private int pyramidLevelsLocation;
    private int screenSizeLocation;
    private int cameraPositionLocation;

    private int visibilityBuffer;
    private int visibilityCapacity;

    public DecalOcclusionShader() {}


    public void compile(ResourceProvider provider, ResourceLocation location) throws IOException {
        String source;
        try (InputStream stream = provider.getResource(location)
                .orElseThrow(() -> new IOException("Missing compute shader: " + location))
                .open()) {
            source = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }

        int shader = GL20.glCreateShader(GL43.GL_COMPUTE_SHADER);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);

        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shader);
            GL20.glDeleteShader(shader);
            throw new IOException("Failed to compile " + location + ":\n" + log);
        }

        int newProgram = GL20.glCreateProgram();
        GL20.glAttachShader(newProgram, shader);
        GL20.glLinkProgram(newProgram);
        GL20.glDeleteShader(shader);

        if (GL20.glGetProgrami(newProgram, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            String log = GL20.glGetProgramInfoLog(newProgram);
            GL20.glDeleteProgram(newProgram);
            throw new IOException("Failed to link " + location + ":\n" + log);
        }
        if (program != 0)
            GL20.glDeleteProgram(program);

        program = newProgram;

        viewProjectionLocation = GL20.glGetUniformLocation(program, "ViewProjection");
        decalCountLocation = GL20.glGetUniformLocation(program, "DecalCount");
        pyramidLevelsLocation = GL20.glGetUniformLocation(program, "PyramidLevels");
        screenSizeLocation = GL20.glGetUniformLocation(program, "ScreenSize");
        cameraPositionLocation = GL20.glGetUniformLocation(program, "CameraPosition");
    }

    public void dispatch(
            int decalCount,
            Matrix4f viewProjection,
            int decalBuffer,
            int depthPyramidTexture,
            int pyramidLevels,
            int width,
            int height,
            Vec3 cameraPosition
    ) {
        if (program == 0 || decalCount == 0)
            return;
        ensureVisibilityBuffer(decalCount);

        GL20.glUseProgram(program);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer matrix = stack.mallocFloat(16);
            viewProjection.get(matrix);
            GL20.glUniformMatrix4fv(viewProjectionLocation, false, matrix);
        }

        GL20.glUniform1i(decalCountLocation, decalCount);
        GL20.glUniform1i(pyramidLevelsLocation, pyramidLevels);
        GL20.glUniform2f(screenSizeLocation, (float) width, (float) height);
        GL20.glUniform3f(
                cameraPositionLocation,
                (float) cameraPosition.x,
                (float) cameraPosition.y,
                (float) cameraPosition.z
        );
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthPyramidTexture);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 0, decalBuffer);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 1, visibilityBuffer);
        GL43.glDispatchCompute((decalCount + 63) / 64, 1, 1);
        GL43.glMemoryBarrier(GL43.GL_SHADER_STORAGE_BARRIER_BIT);
        GL20.glUseProgram(0);
    }

    public boolean[] readVisibility(int decalCount) {
        boolean[] result = new boolean[decalCount];
        if (visibilityBuffer == 0 || decalCount == 0)
            return result;

        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, visibilityBuffer);
        ByteBuffer data = GL30.glMapBufferRange(
                GL43.GL_SHADER_STORAGE_BUFFER,
                0,
                (long) decalCount * Integer.BYTES,
                GL30.GL_MAP_READ_BIT
        );

        if (data != null) {
            for (int i = 0; i < decalCount; ++i)
                result[i] = data.getInt(i * Integer.BYTES) != 0;
            GL15.glUnmapBuffer(GL43.GL_SHADER_STORAGE_BUFFER);
        }

        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);

        return result;
    }

    private void ensureVisibilityBuffer(int decalCount) {
        if (visibilityBuffer != 0 && visibilityCapacity >= decalCount)
            return;
        if (visibilityBuffer != 0)
            GL15.glDeleteBuffers(visibilityBuffer);

        visibilityBuffer = GL15.glGenBuffers();
        visibilityCapacity = Math.max(decalCount, 64);

        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, visibilityBuffer);
        GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, (long) visibilityCapacity * Integer.BYTES, GL15.GL_DYNAMIC_READ);
        GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
    }

    public void destroy() {
        if (program != 0) {
            GL20.glDeleteProgram(program);
            program = 0;
        }
        if (visibilityBuffer != 0) {
            GL15.glDeleteBuffers(visibilityBuffer);
            visibilityBuffer = 0;
        }
        visibilityCapacity = 0;
    }
}
