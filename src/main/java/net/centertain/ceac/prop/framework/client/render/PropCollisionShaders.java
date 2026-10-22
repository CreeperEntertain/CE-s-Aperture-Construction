package net.centertain.ceac.prop.framework.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(
        modid = MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class PropCollisionShaders {
    private static ShaderInstance capture;
    private static ShaderInstance clipCapture;
    private static ShaderInstance resolve;
    private static ShaderInstance depth;
    private static ShaderInstance volumeCapture;
    private static ShaderInstance volumeAccumulate;

    private PropCollisionShaders() {}


    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "prop_collision_capture"),
                        DefaultVertexFormat.POSITION
                ),
                shader -> capture = shader
        );
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "prop_collision_clip_capture"),
                        DefaultVertexFormat.POSITION
                ),
                shader -> clipCapture = shader
        );
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "prop_collision_resolve"),
                        DefaultVertexFormat.POSITION
                ),
                shader -> resolve = shader
        );
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "prop_collision_depth"),
                        DefaultVertexFormat.POSITION
                ),
                shader -> depth = shader
        );
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "prop_collision_volume_capture"),
                        DefaultVertexFormat.POSITION
                ),
                shader -> volumeCapture = shader
        );
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "prop_collision_volume_accumulate"),
                        DefaultVertexFormat.POSITION
                ),
                shader -> volumeAccumulate = shader
        );
    }

    public static ShaderInstance getCapture() {
        return capture;
    }
    public static ShaderInstance getClipCapture() {
        return clipCapture;
    }
    public static ShaderInstance getResolve() {
        return resolve;
    }
    public static ShaderInstance getDepth() {
        return depth;
    }
    public static ShaderInstance getVolumeCapture() {
        return volumeCapture;
    }
    public static ShaderInstance getVolumeAccumulate() {
        return volumeAccumulate;
    }
}
