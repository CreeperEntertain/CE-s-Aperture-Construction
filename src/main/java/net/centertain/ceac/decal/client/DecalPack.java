package net.centertain.ceac.decal.client;

import net.centertain.ceac.decal.DecalDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class DecalPack {
    private final String name;
    private final List<DecalDefinition> decals;

    public DecalPack(String name) {
        this.name = name;
        this.decals = new ArrayList<>();
    }

    public String getName() {
        return name;
    }
    public List<DecalDefinition> getDecals() {
        return List.copyOf(decals);
    }
    public void addDecal(DecalDefinition decalDefinition) {
        decals.add(decalDefinition);
    }
    public void sortDecals() {
        decals.sort(Comparator.comparing(DecalDefinition::getName));
    }
    public void clearDecals() {
        decals.clear();
    }
}
