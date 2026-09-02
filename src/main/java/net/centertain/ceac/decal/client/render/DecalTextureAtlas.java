package net.centertain.ceac.decal.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import net.centertain.ceac.decal.Decal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;
import org.lwjgl.opengl.ARBBindlessTexture;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static net.centertain.ceac.CeacMod.MOD_ID;

public final class DecalTextureAtlas {
    private static final int PADDING = 1;
    private static final Map<ResourceLocation, TextureLocation> LOCATIONS = new HashMap<>();
    private static final ResourceLocation MISSING_TEXTURE = MissingTextureAtlasSprite.getLocation();
                                                         // ^ Why is this class name so unreasonably funny?

    private static final List<DynamicTexture> pages = new ArrayList<>();
    private static final List<Long> pageHandles = new ArrayList<>();

    private static Set<ResourceLocation> textures = Set.of();

    private DecalTextureAtlas() {}


    public static void ensure(Collection<Decal> decals) {
        Set<ResourceLocation> required = new HashSet<>();
        for (Decal decal : decals)
            required.add(decal.getTexture());
        if (required.equals((textures)))
            return;
        rebuild(required);
    }

    public static TextureLocation getLocation(ResourceLocation location) {
        return LOCATIONS.get(location);
    }
    public static List<Long> getPageHandles() {
        return pageHandles;
    }

    private static void rebuild(Set<ResourceLocation> required) {
        Minecraft minecraft = Minecraft.getInstance();
        ResourceManager resourceManager = minecraft.getResourceManager();
        List<TextureImage> images = new ArrayList<>();
        Set<ResourceLocation> resolved = new HashSet<>();

        for (ResourceLocation location : required) {
            ResourceLocation actualLocation = resourceManager.getResource(location).isPresent()
                    ? location
                    : MISSING_TEXTURE;
            if (!resolved.add(actualLocation))
                continue;
            try {
                NativeImage image;

                if (actualLocation.equals(MISSING_TEXTURE)) {
                    NativeImage missingImage = MissingTextureAtlasSprite.getTexture().getPixels();
                    assert missingImage != null;

                    image = new NativeImage(missingImage.getWidth(), missingImage.getHeight(), false);

                    for (int y = 0; y < missingImage.getHeight(); ++y)
                        for (int x = 0; x < missingImage.getWidth(); ++x)
                            image.setPixelRGBA(x, y, missingImage.getPixelRGBA(x, y));
                } else {
                    try (InputStream stream = resourceManager.getResource(actualLocation).orElseThrow().open()) {
                        image = NativeImage.read(stream);
                    }
                }
                images.add(new TextureImage(actualLocation, image));
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
        List<PageBuilder> builders = getBuilders(images, maxTextureSize);
        destroy();

        for (int pageIndex = 0; pageIndex < builders.size(); ++pageIndex) {
            PageBuilder builder = builders.get(pageIndex);
            NativeImage atlas = builder.createImage();

            for (PlacedTexture placed : builder.placements) {
                TextureImage image = placed.image();

                for (int py = 0; py < image.height(); ++py)
                    for (int px = 0; px < image.width(); ++px)
                        atlas.setPixelRGBA(placed.x() + px, placed.y() + py, image.image().getPixelRGBA(px, py));

                int right = placed.x() + image.width();
                int bottom = placed.y() + image.height();

                for (int px = 0; px < image.width(); ++px) {
                    atlas.setPixelRGBA(placed.x() + px, placed.y() - PADDING, image.image().getPixelRGBA(px, 0));
                    atlas.setPixelRGBA(placed.x() + px, bottom, image.image().getPixelRGBA(px, image.height() - 1));
                }
                for (int py = 0; py < image.height(); ++py) {
                    atlas.setPixelRGBA(placed.x() - PADDING, placed.y() + py, image.image().getPixelRGBA(0, py));
                    atlas.setPixelRGBA(right, placed.y() + py, image.image().getPixelRGBA(image.width() - 1, py));
                }
                atlas.setPixelRGBA(placed.x() - PADDING, placed.y() - PADDING, image.image().getPixelRGBA(0, 0));
                atlas.setPixelRGBA(right, placed.y() - PADDING, image.image().getPixelRGBA(image.width() - 1, 0));
                atlas.setPixelRGBA(placed.x() - PADDING, bottom, image.image().getPixelRGBA(0, image.height() - 1));
                atlas.setPixelRGBA(right, bottom, image.image().getPixelRGBA(image.width() - 1, image.height() - 1));

                TextureLocation textureLocation = new TextureLocation(
                        pageIndex,
                        new Vector4f(
                                (float) placed.x() / builder.width,
                                (float) placed.y() / builder.height,
                                (float) right / builder.width,
                                (float) bottom / builder.height
                        )
                );
                LOCATIONS.put(image.location(), textureLocation);
                if (image.location().equals(MISSING_TEXTURE)) {
                    for (ResourceLocation requiredTexture : required) {
                        if (resourceManager.getResource(requiredTexture).isEmpty())
                            LOCATIONS.put(requiredTexture, textureLocation);
                    }
                }

                image.image().close();
            }

            DynamicTexture dynamicTexture = new DynamicTexture(atlas);
            dynamicTexture.setFilter(false, false);

            long handle = ARBBindlessTexture.glGetTextureHandleARB(dynamicTexture.getId());

            ARBBindlessTexture.glMakeTextureHandleResidentARB(handle);

            ResourceLocation pageId = ResourceLocation.fromNamespaceAndPath(MOD_ID, "dynamic/decal_atlas_" + pageIndex);

            minecraft.getTextureManager().register(pageId, dynamicTexture);

            pages.add(dynamicTexture);
            pageHandles.add(handle);
        }
        textures = Set.copyOf(required);
    }

    private static @NotNull List<PageBuilder> getBuilders(List<TextureImage> images, int maxTextureSize) {
        for (TextureImage image : images) {
            if (image.width() + PADDING * 2 > maxTextureSize ||
                    image.height() + PADDING * 2 > maxTextureSize)
                throw new IllegalStateException(
                        "Decal texture " + image.location() +
                        " exceeds GL_MAX_TEXTURE_SIZE (" + maxTextureSize + ")."
                );
        }

        int estimatedPageWidth = getEstimatedPageWidth(images, maxTextureSize);
        return createBuilders(images, estimatedPageWidth);
    }

    private static @NotNull List<PageBuilder> createBuilders(List<TextureImage> images, int estimatedPageWidth) {
        List<PageBuilder> builders = new ArrayList<>();
        PageBuilder page = new PageBuilder(estimatedPageWidth);

        for (TextureImage image : images) {
            if (!page.tryAdd(image)) {
                builders.add(page);
                page = new PageBuilder(estimatedPageWidth);
                if (!page.tryAdd(image))
                    throw new IllegalStateException("Failed to place decal texture " + image.location() + ".");
            }
        }

        builders.add(page);
        return builders;
    }

    private static int getEstimatedPageWidth(List<TextureImage> images, int maxTextureSize) {
        int estimatedPageWidth = 0;
        long totalArea = 0;

        for (TextureImage image : images) {
            int width = image.width() + PADDING * 2;
            int height = image.height() + PADDING * 2;
            estimatedPageWidth = Math.max(estimatedPageWidth, width + PADDING);
            totalArea += (long) width * height;
        }

        estimatedPageWidth = Math.max(estimatedPageWidth, (int) Math.ceil(Math.sqrt(totalArea)));
        estimatedPageWidth = Math.min(estimatedPageWidth, maxTextureSize);
        return estimatedPageWidth;
    }

    public static void destroy() {
        LOCATIONS.clear();
        Minecraft minecraft = Minecraft.getInstance();
        for (int i = 0; i < pages.size(); ++i) {
            ResourceLocation pageId = ResourceLocation.fromNamespaceAndPath(MOD_ID, "dynamic/decal_atlas_" + i);
            if (i < pageHandles.size())
                ARBBindlessTexture.glMakeTextureHandleNonResidentARB(pageHandles.get(i));
            minecraft.getTextureManager().release(pageId);
            pages.get(i).close();
        }
        pages.clear();
        pageHandles.clear();
        textures = Set.of();
    }


    private static final class PageBuilder {
        private final int width;
        private int height;

        private int x = PADDING;
        private int y = PADDING;
        private int rowHeight;

        private final List<PlacedTexture> placements = new ArrayList<>();

        private PageBuilder(int width) {
            this.width = width;
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted") // Maaan... SHUT UP!
        private boolean tryAdd(TextureImage image) {
            int placedWidth = image.width() + PADDING * 2;
            int placedHeight = image.height() + PADDING * 2;

            if (x + placedWidth > width && x > PADDING) {
                x = PADDING;
                y += rowHeight;
                rowHeight = 0;
            }

            if (x + placedWidth > width)
                return false;

            placements.add(new PlacedTexture(image, x, y));

            x += placedWidth;
            rowHeight = Math.max(rowHeight, placedHeight);
            height = Math.max(height, y + placedHeight);

            return true;
        }

        private NativeImage createImage() {
            return new NativeImage(width, height, true);
        }
    }

    private record PlacedTexture(
            TextureImage image,
            int x,
            int y
    ) {}

    public record TextureLocation(
            int page,
            Vector4f bounds
    ) {}

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
