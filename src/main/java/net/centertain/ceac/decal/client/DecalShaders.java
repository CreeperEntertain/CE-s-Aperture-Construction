package net.centertain.ceac.decal.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.centertain.ceac.decal.client.render.KBufferSortShader;
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
    private static ShaderInstance translucentCapture;
    private static ShaderInstance decalKBufferCapture;
    private static ShaderInstance decalCoverage;
    private static ShaderInstance decalOpaqueResolve;

    private static KBufferSortShader kBufferSort;

    private DecalShaders() {}

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        // Draw/composite shaders
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "decal_translucent_capture"),
                        DefaultVertexFormat.BLOCK
                ),
                shader -> translucentCapture = shader
        );
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "decal_kbuffer_capture"),
                        DefaultVertexFormat.POSITION_COLOR_LIGHTMAP
                ),
                shader -> decalKBufferCapture = shader
        );
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "decal_coverage"),
                        DefaultVertexFormat.POSITION_COLOR_LIGHTMAP
                ),
                shader -> decalCoverage = shader
        );
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "decal_opaque_resolve"),
                        DefaultVertexFormat.POSITION
                ),
                shader -> decalOpaqueResolve = shader
        );

        // Compute shaders
        if (kBufferSort != null)
            kBufferSort.destroy();
        kBufferSort = new KBufferSortShader();
        kBufferSort.compile(
                event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "shaders/kbuffer_sort.comp")
        );
    }

    public static void sortKBuffer(int width, int height) {
        if (kBufferSort == null)
            return;
        kBufferSort.dispatch(width, height);
    }

    public static ShaderInstance getTranslucentCapture() {
        return translucentCapture;
    }
    public static ShaderInstance getDecalKBufferCapture() {
        return decalKBufferCapture;
    }
    public static ShaderInstance getDecalCoverage() {
        return decalCoverage;
    }
    public static ShaderInstance getDecalOpaqueResolve() {
        return decalOpaqueResolve;
    }
}
