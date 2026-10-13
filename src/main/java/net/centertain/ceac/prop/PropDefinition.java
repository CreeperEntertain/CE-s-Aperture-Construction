package net.centertain.ceac.prop;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;

import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public final class PropDefinition {
    private final ResourceLocation modelId;
    private final SoundType soundType;
    private final float destroyTime;
    private final boolean targetable;
    private final boolean minable;

    public PropDefinition(
            ResourceLocation modelId,
            SoundType soundType,
            float destroyTime,
            boolean targetable,
            boolean minable
    ) {
        this.modelId = Objects.requireNonNull(modelId);
        this.soundType = Objects.requireNonNull(soundType);
        if (!Float.isFinite(destroyTime))
            throw new IllegalArgumentException("Destroy time must be finite");
        this.destroyTime = destroyTime;
        this.targetable = targetable;
        this.minable = minable;
    }

    public ResourceLocation getModelId() {
        return modelId;
    }
    public SoundType getSoundType() {
        return soundType;
    }
    public float getDestroyTime() {
        return destroyTime;
    }
    public boolean isTargetable() {
        return targetable;
    }
    public boolean getMinable() {
        return minable;
    }
}
