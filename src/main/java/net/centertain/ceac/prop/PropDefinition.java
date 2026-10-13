package net.centertain.ceac.prop;

import net.minecraft.world.level.block.SoundType;

public final class PropDefinition {
    private Prop prop;
    private SoundType soundType;
    private float destroyTime;
    private boolean minable;

    public PropDefinition(
            Prop prop,
            SoundType soundType,
            float destroyTime,
            boolean minable
    ) {
        this.prop = prop;
        this.soundType = soundType;
        this.destroyTime = destroyTime;
        this.minable = minable;
    }

    public Prop getProp() {
        return prop;
    }
    public SoundType getSoundType() {
        return soundType;
    }
    public float getDestroyTime() {
        return destroyTime;
    }
    public boolean getMinable() {
        return minable;
    }

    public void setProp(Prop prop) {
        this.prop = prop;
    }
    public void setSoundType(SoundType soundType) {
        this.soundType = soundType;
    }
    public void setDestroyTime(float destroyTime) {
        this.destroyTime = destroyTime;
    }
    public void setMinable(boolean minable) {
        this.minable = minable;
    }
}
