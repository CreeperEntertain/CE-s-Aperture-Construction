package net.centertain.ceac.material;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.Map;

public abstract class Material {
    private final String name;
    private final SoundType soundType;
    private final Map<Vector2i, ResourceLocation> textures;

    protected Material(
            String name,
            SoundType soundType,
            Map<Vector2i, ResourceLocation> textures
    ) {
        this.name = name;
        this.soundType = soundType;
        this.textures = Map.copyOf(textures);
    }
    protected Material(
            String name,
            Map<Vector2i, ResourceLocation> textures
    ) {
        this.name = name;
        this.soundType = SoundType.STONE;
        this.textures = Map.copyOf(textures);
    }

    public String getName() {
        return name;
    }
    public SoundType getSoundType() {
        return soundType;
    }
    public Map<Vector2i, ResourceLocation> getTextures() {
        return textures;
    }
    public @Nullable ResourceLocation getTexture(Vector2i coordinate) {
        return textures.get(coordinate);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted") // ?????
    public boolean containsCoordinate(Vector2i coordinate) {
        return textures.containsKey(coordinate);
    }
}
