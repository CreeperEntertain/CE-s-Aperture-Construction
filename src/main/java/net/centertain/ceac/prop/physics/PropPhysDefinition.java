package net.centertain.ceac.prop.physics;

import javax.annotation.Nullable;

public final class PropPhysDefinition {
    public static final PropPhysDefinition DEFAULT = new Builder().build();

    private final float density;
    @Nullable private final Float massOverride;

    private final float friction;
    private final float restitution;

    private final float linearDamping;
    private final float angularDamping;
    private final float gravityScale;
    private final float sleepThreshold;

    private final boolean enableSleep;
    private final boolean bullet;

    private PropPhysDefinition(Builder builder) {
        this.density = requireNonNegativeFinite("Density", builder.density);
        this.massOverride = builder.massOverride != null
                ? requirePositiveFinite("Mass override", builder.massOverride)
                : null;
        this.friction = requireNonNegativeFinite("Friction", builder.friction);
        if (!Float.isFinite(builder.restitution) || builder.restitution < 0.0f || builder.restitution > 1.0f)
            throw new IllegalArgumentException("Restitution must be between 0 and 1");
        this.restitution = builder.restitution;
        this.linearDamping = requireNonNegativeFinite("Linear damping", builder.linearDamping);
        this.angularDamping = requireNonNegativeFinite("Angular damping", builder.angularDamping);
        if (!Float.isFinite(builder.gravityScale))
            throw new IllegalArgumentException("Gravity scale must be finite");
        this.gravityScale = builder.gravityScale;
        this.sleepThreshold = requireNonNegativeFinite("Sleep threshold", builder.sleepThreshold);
        this.enableSleep = builder.enableSleep;
        this.bullet = builder.bullet;
    }

    private static float requireNonNegativeFinite(
            String name,
            float value
    ) {
        if (!Float.isFinite(value) || value < 0.0f)
            throw new IllegalArgumentException(name + " must be finite and non-negative");
        return value;
    }

    @SuppressWarnings("SameParameterValue")
    private static float requirePositiveFinite(
            String name,
            float value
    ) {
        if (!Float.isFinite(value) || value <= 0.0f)
            throw new IllegalArgumentException(name + " must be finite and greater than zero");
        return value;
    }

    public static Builder builder() {
        return new Builder();
    }

    public float getDensity() {
        return density;
    }
    public @Nullable Float getMassOverride() {
        return massOverride;
    }
    public float getFriction() {
        return friction;
    }
    public float getRestitution() {
        return restitution;
    }
    public float getLinearDamping() {
        return linearDamping;
    }
    public float getAngularDamping() {
        return angularDamping;
    }
    public float getGravityScale() {
        return gravityScale;
    }
    public float getSleepThreshold() {
        return sleepThreshold;
    }
    public boolean isSleepEnabled() {
        return enableSleep;
    }
    public boolean isBullet() {
        return bullet;
    }


    public static final class Builder {
        private float density = 1.0f;
        @Nullable private Float massOverride;

        private float friction = 0.6f;
        private float restitution = 0.0f;

        private float linearDamping = 0.0f;
        private float angularDamping = 0.0f;
        private float gravityScale = 1.0f;
        private float sleepThreshold = 0.05f;

        private boolean enableSleep = true;
        private boolean bullet = false;

        private Builder() {}


        public Builder density(float density) {
            this.density = density;
            return this;
        }

        public Builder massOverride(float mass) {
            this.massOverride = mass;
            return this;
        }

        public Builder useAutomaticMass() {
            this.massOverride = null;
            return this;
        }

        public Builder friction(float friction) {
            this.friction = friction;
            return this;
        }

        public Builder restitution(float restitution) {
            this.restitution = restitution;
            return this;
        }

        public Builder linearDamping(float linearDamping) {
            this.linearDamping = linearDamping;
            return this;
        }

        public Builder angularDamping(float angularDamping) {
            this.angularDamping = angularDamping;
            return this;
        }

        public Builder gravityScale(float gravityScale) {
            this.gravityScale = gravityScale;
            return this;
        }

        public Builder sleepThreshold(float sleepThreshold) {
            this.sleepThreshold = sleepThreshold;
            return this;
        }

        public Builder enableSleep(boolean enableSleep) {
            this.enableSleep = enableSleep;
            return this;
        }

        public Builder bullet(boolean bullet) {
            this.bullet = bullet;
            return this;
        }

        public PropPhysDefinition build() {
            return new PropPhysDefinition(this);
        }
    }
}
