package net.centertain.ceac.prop;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
public final class PropDefinition {
    private final ResourceLocation modelId;
    private final SoundType soundType;
    private final float destroyTime;
    private final boolean targetable;
    private final boolean minable;
    private final AABB localHitbox;
    @Nullable private final ToolAction effectiveToolAction;
    private final int requiredToolLevel;
    @Nullable private final Item dropItem;

    public PropDefinition(
            ResourceLocation modelId,
            SoundType soundType,
            float destroyTime,
            boolean targetable,
            boolean minable,
            AABB localHitbox,
            @Nullable ToolAction effectiveToolAction,
            int requiredToolLevel,
            @Nullable Item dropItem
    ) {
        this.modelId = Objects.requireNonNull(modelId);
        this.soundType = Objects.requireNonNull(soundType);
        if (!Float.isFinite(destroyTime))
            throw new IllegalArgumentException("Destroy time must be finite");
        if (requiredToolLevel < -1)
            throw new IllegalArgumentException("Required tool level must be -1 or greater");
        if (requiredToolLevel >= 0 && effectiveToolAction == null)
            throw new IllegalArgumentException("A required tool level requires an effective tool action");
        this.destroyTime = destroyTime;
        this.targetable = targetable;
        this.minable = minable;
        this.localHitbox = Objects.requireNonNull(localHitbox);
        this.effectiveToolAction = effectiveToolAction;
        this.requiredToolLevel = requiredToolLevel;
        this.dropItem = dropItem;
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
    public AABB getLocalHitbox() {
        return localHitbox;
    }
    public @Nullable ToolAction getEffectiveToolAction() {
        return effectiveToolAction;
    }
    public int getRequiredToolLevel() {
        return requiredToolLevel;
    }
    public @Nullable Item getDropItem() {
        return dropItem;
    }
}
