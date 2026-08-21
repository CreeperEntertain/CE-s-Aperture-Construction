package net.centertain.ceac.decal.client;

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
public class DecalShaders {
    private static ShaderInstance decal;
    private static ShaderInstance translucentCapture;

    private DecalShaders() {}

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "decal"),
                        DefaultVertexFormat.POSITION_COLOR_LIGHTMAP
                ),
                shader -> decal = shader
        );
    }

    public static ShaderInstance getInstance() {
        return decal;
    }
    public static ShaderInstance getTranslucentCaptureShader() {
        return translucentCapture;
    }
}
