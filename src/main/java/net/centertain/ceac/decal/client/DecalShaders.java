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
    private static ShaderInstance decal;
    private static ShaderInstance translucentCapture;
    private static ShaderInstance kBufferDebug;

    private static KBufferSortShader kBufferSort;

    private DecalShaders() {}

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        // Draw shaders
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "decal"),
                        DefaultVertexFormat.POSITION_COLOR_LIGHTMAP
                ),
                shader -> decal = shader
        );
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
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "kbuffer_debug"),
                        DefaultVertexFormat.POSITION
                ),
                shader -> kBufferDebug = shader
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

    public static ShaderInstance getInstance() {
        return decal;
    }
    public static ShaderInstance getTranslucentCapture() {
        return translucentCapture;
    }
    public static ShaderInstance getKBufferDebug() {
        return kBufferDebug;
    }
}
