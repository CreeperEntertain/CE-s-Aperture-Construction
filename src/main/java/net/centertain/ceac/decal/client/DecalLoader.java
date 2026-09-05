package net.centertain.ceac.decal.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.centertain.ceac.decal.DecalDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public final class DecalLoader {
    private static final List<DecalPack> decals = new ArrayList<>();

    private DecalLoader() {}


    public static List<DecalPack> getPacks() {
        return List.copyOf(decals);
    }

    public static void gatherResourceLocations() {
        decals.clear();

        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        Map<String, DecalPack> packs = new HashMap<>();

        for (PackResources resourcePack : resourceManager.listPacks().toList()) {
            resourcePack.listResources(
                    PackType.CLIENT_RESOURCES,
                    "ceac",
                    "textures/decal",
                    (location, resource) -> extracted(resourcePack, location, packs)
            );
        }

        decals.addAll(packs.values());
        for (DecalPack pack : decals)
            pack.sortDecals();

        printDecals();
    }
    private static void extracted(
            PackResources resourcePack,
            ResourceLocation location,
            Map<String, DecalPack> packs
    ) {
        if (!location.getPath().endsWith(".png"))
            return;

        String path = location.getPath();
        String prefix = "textures/decal/";

        if (!path.startsWith(prefix))
            return;

        DecalDefinition definition = getDefinition(resourcePack, location);

        String relativePath = path.substring(prefix.length());
        int separator = relativePath.indexOf('/');

        if (separator < 0) {
            DecalPack miscellaneous = packs.computeIfAbsent("Miscellaneous", DecalPack::new);
            miscellaneous.addDecal(definition);
            return;
        }

        String packName = formatPackName(relativePath.substring(0, separator));
        DecalPack pack = packs.computeIfAbsent(packName, DecalPack::new);

        pack.addDecal(definition);
    }

    private static String formatPackName(String name) {
        return Arrays
                .stream(name.replace('_', ' ').split(" "))
                .map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    private static DecalDefinition getDefinition(
            PackResources resourcePack,
            ResourceLocation location
    ) {
        String path = location.getPath();
        String jsonPath = path.substring(0, path.length() - 4) + ".json";

        ResourceLocation jsonLocation = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), jsonPath);
        IoSupplier<InputStream> json = resourcePack.getResource(PackType.CLIENT_RESOURCES, jsonLocation);

        if (json == null)
            return new DecalDefinition(location);

        String name = path.substring(path.lastIndexOf('/') + 1, path.length() - 4);

        int width = 16;
        int height = 16;

        try (InputStream stream = json.get()) {
            JsonObject object = JsonParser
                    .parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
                    .getAsJsonObject();

            if (object.has("name"))
                name = object.get("name").getAsString();
            if (object.has("width"))
                width = object.get("width").getAsInt();
            if (object.has("height"))
                height = object.get("height").getAsInt();

            return new DecalDefinition(name, width, height, location);
        } catch (IOException | JsonParseException | ClassCastException exception) {
            throw new RuntimeException("Failed to load decal definition " + jsonLocation, exception);
        }
    }

    public static void printDecals() {
        System.out.println("INSTALLED DECALS INCLUDE...");
        for (DecalPack pack : decals) {
            System.out.println("\n" + pack.getName() + ": \n");
            for (DecalDefinition decal : pack.getDecals())
                System.out.println(
                        decal.getResourceLocation().getPath() + ": \n" +
                        "    name: " + decal.getName() + "\n" +
                        "    width: " + decal.getWidth() + "\n" +
                        "    height: " + decal.getHeight()
                );
        }
    }
}
