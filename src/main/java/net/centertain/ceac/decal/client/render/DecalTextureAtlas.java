package net.centertain.ceac.decal.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import net.centertain.ceac.decal.Decal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static net.centertain.ceac.CeacMod.MOD_ID;

public final class DecalTextureAtlas {
    private static final ResourceLocation ATLAS_ID =
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "dynamic/decal_atlas");
    private static final int PADDING = 1;
    private static final Map<ResourceLocation, Vector4f> UVS = new HashMap<>();

    private static DynamicTexture texture;
    private static Set<ResourceLocation> textures = Set.of();
    private static int width;
    private static int height;

    private DecalTextureAtlas() {}


    public static void ensure(Collection<Decal> decals) {
        Set<ResourceLocation> required = new HashSet<>();
        for (Decal decal : decals)
            required.add(decal.getTexture());
        if (required.equals((textures)))
            return;
        rebuild(required);
    }

    public static int getWidth() {
        return width;
    }
    public static int getHeight() {
        return height;
    }
    public static Vector4f getUVs(ResourceLocation location) {
        return UVS.getOrDefault(location, new Vector4f(0.0f, 0.0f, 1.0f, 1.0f));
    }
    public static ResourceLocation getTexture() {
        return ATLAS_ID;
    }

    private static void rebuild(Set<ResourceLocation> required) {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceManager resourceManager = minecraft.getResourceManager();
        List<TextureImage> images = new ArrayList<>();

        for (ResourceLocation location : required) {
            Optional<Resource> resource = resourceManager.getResource(location);
            if (resource.isEmpty())
                continue;
            try (InputStream stream = resource.get().open()) {
                NativeImage image = NativeImage.read(stream);
                images.add(new TextureImage(location, image));
            } catch (IOException exception) {
                throw new RuntimeException("Failed to load decal texture " + location, exception);
            }
        }
        if (images.isEmpty()) {
            destroy();
            textures = Set.copyOf(required);
            return;
        }

        int maxTextureSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);
        int atlasWidth = 0;
        int atlasHeight = 0;

        for (TextureImage image : images)
            atlasWidth = Math.max(atlasWidth, image.width() + PADDING * 2);

        int x = PADDING;
        int y = PADDING;
        int rowHeight = 0;
        int estimatedWidth = Math.max(
                atlasWidth,
                (int) Math.ceil(Math.sqrt(images.stream().mapToLong(TextureImage::area).sum()))
        );
        estimatedWidth = Math.min(estimatedWidth, maxTextureSize);

        for (TextureImage image : images) {
            int width = image.width() + PADDING * 2;
            int height = image.height() + PADDING * 2;
            if (x + width > estimatedWidth) {
                x = PADDING;
                y += rowHeight;
                rowHeight = 0;
            }
            if (y + height > atlasHeight)
                atlasHeight = y + height;
            x += width;
            rowHeight = Math.max(rowHeight, height);
        }

        atlasWidth = Math.max(atlasWidth, x + PADDING);
        atlasHeight = Math.max(atlasHeight, y + rowHeight);

        if (atlasWidth > maxTextureSize || atlasHeight > maxTextureSize)
            throw new IllegalStateException("Decal texture atlas exceeds GL_MAX_TEXTURE_SIZE (" + maxTextureSize + ").");

        NativeImage atlas = new NativeImage(atlasWidth, atlasHeight, true); // STFU, compiler! It's already accounted for!
        Map<ResourceLocation, Vector4f> newUVs = new HashMap<>();
        x = PADDING;
        y = PADDING;
        rowHeight = 0;

        for (TextureImage image : images) {
            int imageWidth = image.width();
            int imageHeight = image.height();
            if (x + imageWidth + PADDING > atlasWidth) {
                x = PADDING;
                y += rowHeight;
                rowHeight = 0;
            }
            for (int py = 0; py < imageHeight; ++py)
                for (int px = 0; px < imageWidth; ++px)
                    atlas.setPixelRGBA(x + px, y + py, image.image().getPixelRGBA(px, py));
            int right = x + imageWidth;
            int bottom = y + imageHeight;
            for (int px = 0; px < imageWidth; ++px) {
                atlas.setPixelRGBA(x + px, y - PADDING, image.image().getPixelRGBA(px, 0));
                atlas.setPixelRGBA(x + px, bottom, image.image().getPixelRGBA(px, imageHeight - 1));
            }
            for (int py = 0; py < imageHeight; ++py) {
                atlas.setPixelRGBA(x - PADDING, y + py, image.image().getPixelRGBA(0, py));
                atlas.setPixelRGBA(right, y + py, image.image().getPixelRGBA(imageWidth - 1, py));
            }
            atlas.setPixelRGBA(x - PADDING, y - PADDING, image.image().getPixelRGBA(0, 0));
            atlas.setPixelRGBA(right, y - PADDING, image.image().getPixelRGBA(imageWidth - 1, 0));
            atlas.setPixelRGBA(x - PADDING, bottom, image.image().getPixelRGBA(0, imageHeight - 1));
            atlas.setPixelRGBA(right, bottom, image.image().getPixelRGBA(imageWidth - 1, imageHeight - 1));

            newUVs.put(
                    image.location(),
                    new Vector4f(
                            (float) x / atlasWidth,
                            (float) y / atlasHeight,
                            (float) right / atlasWidth,
                            (float) bottom / atlasHeight
                    )
            );
            x += imageWidth + PADDING * 2;
            rowHeight = Math.max(rowHeight, imageHeight + PADDING * 2);
            image.image().close();
        }

        destroy();
        texture = new DynamicTexture(atlas);
        texture.setFilter(false, false);
        minecraft.getTextureManager().register(ATLAS_ID, texture);
        UVS.clear();
        UVS.putAll(newUVs);
        textures = Set.copyOf(required);
        width = atlasWidth;
        height = atlasHeight;
    }

    public static void destroy() {
        UVS.clear();
        if (texture != null) {
            Minecraft.getInstance().getTextureManager().release(ATLAS_ID);
            texture.close();
            texture = null;
        }
        textures = Set.of();
        width = 0;
        height = 0;
    }

    private record TextureImage(
            ResourceLocation location,
            NativeImage image
    ) {
        int width() {
            return image.getWidth();
        }
        int height() {
            return image.getHeight();
        }
        long area() {
            return (long) width() * height();
        }
    }
}
