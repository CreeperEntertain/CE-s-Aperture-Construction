package net.centertain.ceac.prop.framework;

import net.centertain.ceac.prop.framework.common.PropUseContext;
import net.centertain.ceac.prop.framework.physics.PropPhysDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class PropDefinition {
    private final ResourceLocation modelId;
    private final SoundType soundType;
    private final float destroyTime;
    private final boolean targetable;
    private final boolean minable;
    @Nullable private final ToolAction effectiveToolAction;
    private final int requiredToolLevel;
    @Nullable private final Item dropItem;
    private final PropPhysDefinition physDefinition;

    public PropDefinition(
            ResourceLocation modelId,
            SoundType soundType,
            float destroyTime,
            boolean targetable,
            boolean minable,
            @Nullable ToolAction effectiveToolAction,
            int requiredToolLevel,
            @Nullable Item dropItem
    ) {
        this(
                modelId,
                soundType,
                destroyTime,
                targetable,
                minable,
                effectiveToolAction,
                requiredToolLevel,
                dropItem,
                PropPhysDefinition.DEFAULT
        );
    }

    public PropDefinition(
            ResourceLocation modelId,
            SoundType soundType,
            float destroyTime,
            boolean targetable,
            boolean minable,
            @Nullable ToolAction effectiveToolAction,
            int requiredToolLevel,
            @Nullable Item dropItem,
            PropPhysDefinition physDefinition
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
        this.effectiveToolAction = effectiveToolAction;
        this.requiredToolLevel = requiredToolLevel;
        this.dropItem = dropItem;
        this.physDefinition = Objects.requireNonNull(physDefinition);
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
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isTargetable() {
        return targetable;
    }
    public boolean getMinable() {
        return minable;
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
    public PropPhysDefinition getPhysDefinition() {
        return physDefinition;
    }


    public InteractionResult use(PropUseContext context) {
        return InteractionResult.PASS;
    }
}
