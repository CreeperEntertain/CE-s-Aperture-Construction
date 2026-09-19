package net.centertain.ceac.material;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.Map;

public abstract class Material {
    private final String name;
    private final Map<Vector2i, ResourceLocation> textures;

    protected Material(
            String name,
            Map<Vector2i, ResourceLocation> textures
    ) {
        this.name = name;
        this.textures = Map.copyOf(textures);
    }

    public String getName() {
        return name;
    }
    public Map<Vector2i, ResourceLocation> getTextures() {
        return textures;
    }
    public @Nullable ResourceLocation getTexture(Vector2i coordinate) {
        return textures.get(coordinate);
    }

    public boolean containsCoordinate(Vector2i coordinate) {
        return textures.containsKey(coordinate);
    }
}
