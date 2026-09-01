package net.centertain.ceac.decal.client.render.compute;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.lwjgl.opengl.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class DecalDepthPyramidShader {
    private int program;

    private int sourceLevelLocation;
    private int useDepthSourceLocation;

    public DecalDepthPyramidShader() {}

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

        sourceLevelLocation = GL20.glGetUniformLocation(program, "SourceLevel");
        useDepthSourceLocation = GL20.glGetUniformLocation(program, "UseDepthSource");
    }

    public void dispatch(
            int sourceTexture,
            int pyramidTexture,
            int destinationLevel,
            int sourceLevel,
            int width,
            int height,
            boolean useDepthSource
    ) {
        if (program == 0)
            return;

        int destinationWidth = Math.max(1, width >> destinationLevel);
        int destinationHeight = Math.max(1, height >> destinationLevel);

        GL20.glUseProgram(program);
        GL20.glUniform1i(sourceLevelLocation, sourceLevel);
        GL20.glUniform1i(useDepthSourceLocation, useDepthSource ? 1 : 0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, sourceTexture);
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, pyramidTexture);
        GL42.glBindImageTexture(0, pyramidTexture, destinationLevel, false, 0, GL15.GL_WRITE_ONLY, GL30.GL_R32F);
        GL43.glDispatchCompute((destinationWidth + 7) / 8, (destinationHeight + 7) / 8, 1);
        GL42.glMemoryBarrier(GL42.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT | GL42.GL_TEXTURE_FETCH_BARRIER_BIT);
        GL20.glUseProgram(0);
    }

    public void destroy() {
        if (program != 0) {
            GL20.glDeleteProgram(program);
            program = 0;
        }
    }
}