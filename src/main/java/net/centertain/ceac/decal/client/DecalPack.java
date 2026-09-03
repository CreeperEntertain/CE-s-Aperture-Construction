package net.centertain.ceac.decal.client;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class DecalPack {
    private final String name;
    private final List<ResourceLocation> decals;

    public DecalPack(String name) {
        this.name = name;
        this.decals = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    public List<ResourceLocation> getDecals() {
        return List.copyOf(decals);
    }
    public void addDecal(ResourceLocation decal) {
        decals.add(decal);
    }
    public void sortDecals() {
        decals.sort(Comparator.comparing(ResourceLocation::getPath));
    }
    public void clearDecals() {
        decals.clear();
    }
}
