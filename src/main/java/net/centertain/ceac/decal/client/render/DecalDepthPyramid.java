package net.centertain.ceac.decal.client.render;

import org.lwjgl.opengl.*;

public final class DecalDepthPyramid {
    private static int texture;
    private static int width;
    private static int height;
    private static int levels;

    private DecalDepthPyramid() {}

    public static void ensure(int width, int height) {
        int newLevels = 32 - Integer.numberOfLeadingZeros(
                Math.max(width, height)
        );

        if (
                texture != 0 &&
                DecalDepthPyramid.width == width &&
                DecalDepthPyramid.height == height &&
                levels == newLevels
        )
            return;

        destroy();

        DecalDepthPyramid.width = width;
        DecalDepthPyramid.height = height;
        levels = newLevels;

        texture = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL42.glTexStorage2D(GL11.GL_TEXTURE_2D, levels, GL30.GL_R32F, width, height);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, levels - 1);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    public static void build(
            int sourceDepthTexture,
            int width,
            int height
    ) {
        ensure(width, height);
        DecalShaders.buildDepthPyramid(sourceDepthTexture, texture, width, height, levels);
    }

    public static int getTexture() {
        return texture;
    }
    public static int getLevels() {
        return levels;
    }

    public static void destroy() {
        if (texture != 0) {
            GL11.glDeleteTextures(texture);
            texture = 0;
        }
        width = 0;
        height = 0;
        levels = 0;
    }
}
