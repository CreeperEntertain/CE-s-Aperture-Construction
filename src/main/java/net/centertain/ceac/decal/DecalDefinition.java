package net.centertain.ceac.decal;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class DecalDefinition {
    private final String name;
    private final int width;
    private final int height;
    private final ResourceLocation resourceLocation;

    public DecalDefinition(
            String name,
            int width,
            int height,
            ResourceLocation resourceLocation
    ) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.resourceLocation = resourceLocation;
    }
    public DecalDefinition(
            ResourceLocation resourceLocation
    ) {
        this.resourceLocation = resourceLocation;

        String path = resourceLocation.getPath();
        int slash = path.lastIndexOf('/');
        int dot = path.lastIndexOf('.');

        String name = path.substring(slash + 1, dot >= slash ? dot : path.length());
        try {
            name = Arrays.stream(name.replace('_', ' ').split(" "))
                    .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                    .collect(Collectors.joining(" "));
        } catch (StringIndexOutOfBoundsException e) {
            System.out.println("Ya decal don't have a name, man!");
        }
        this.name = name;

        try {
            Resource resource = Minecraft.getInstance()
                    .getResourceManager()
                    .getResource(resourceLocation)
                    .orElseThrow(() -> new IllegalArgumentException("Missing decal texture " + resourceLocation));
            try (InputStream stream = resource.open()) {
                NativeImage image = NativeImage.read(stream);
                this.width = image.getWidth();
                this.height = image.getHeight();
                image.close();
            }
        } catch (IOException e) {
            throw  new RuntimeException("Failed to load decal texture " + resourceLocation, e);
        }
    }

    public String getName() {
        return name;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }
}
