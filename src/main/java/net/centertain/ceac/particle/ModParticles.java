package net.centertain.ceac.particle;

import com.mojang.serialization.Codec;
import net.centertain.ceac.material.MaterialParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import static net.centertain.ceac.CeacMod.MOD_ID;

public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MOD_ID);

    public static final RegistryObject<ParticleType<MaterialParticleOptions>> MATERIAL =
            PARTICLES.register("material", () ->
                    new ParticleType<MaterialParticleOptions>(
                            false,
                            MaterialParticleOptions.DESERIALIZER
                    ) {
                        @Override
                        public @NotNull Codec<MaterialParticleOptions> codec() {
                            return MaterialParticleOptions.CODEC;
                        }
                    }
            );

    private ModParticles() {}

    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }
}