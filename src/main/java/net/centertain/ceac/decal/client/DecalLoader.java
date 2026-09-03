package net.centertain.ceac.decal.client;

import net.centertain.ceac.decal.DecalDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                    (location, resource) -> extracted(location, packs)
            );
        }

        decals.addAll(packs.values());
        for (DecalPack pack : decals)
            pack.sortDecals();

        printDecals();
    }
    private static void extracted(ResourceLocation location, Map<String, DecalPack> packs) {
        if (!location.getPath().endsWith(".png"))
            return;
        String path = location.getPath();
        String prefix = "textures/decal/";
        if (!path.startsWith(prefix))
            return;

        String relativePath = path.substring(prefix.length());
        int separator = relativePath.indexOf('/');

        if (separator < 0) {
            DecalPack miscellaneous = packs.computeIfAbsent("miscellaneous", DecalPack::new);
            miscellaneous.addDecal(new DecalDefinition(location));
            return;
        }

        String packName = relativePath.substring(0, separator);
        DecalPack pack = packs.computeIfAbsent(packName, DecalPack::new);

        pack.addDecal(new DecalDefinition(location));
    }

    public static void printDecals() {
        System.out.println("INSTALLED DECALS INCLUDE...");
        for (DecalPack pack : decals) {
            System.out.println("\n" + pack.getName() + ": \n");
            for (DecalDefinition decal : pack.getDecals())
                System.out.println(decal.getResourceLocation().getPath());
        }
    }
}
