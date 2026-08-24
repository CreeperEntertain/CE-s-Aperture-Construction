package net.centertain.ceac.decal.client.render;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL43;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class KBufferSortShader {
    private int program;

    private int screenSizeLocation;

    public KBufferSortShader() {}

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
        screenSizeLocation = GL20.glGetUniformLocation(program, "ScreenSize");
    }

    public void dispatch(int width, int height) {
        if (program == 0)
            return;
        GL20.glUseProgram(program);
        GL20.glUniform4f(
                screenSizeLocation,
                (float) width,
                (float) height,
                0.0F,
                0.0F
        );
        GL43.glDispatchCompute(
                (width + 7) / 8,
                (height + 7) / 8,
                1
        );
        GL20.glUseProgram(0);
    }

    public void destroy() {
        if (program != 0) {
            GL20.glDeleteProgram(program);
            program = 0;
        }
    }
}
