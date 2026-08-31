package net.centertain.ceac.decal.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.centertain.ceac.decal.client.render.compute.DecalOcclusionShader;
import net.centertain.ceac.decal.client.render.compute.KBufferSortShader;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.io.IOException;

import static net.centertain.ceac.CeacMod.MOD_ID;

@Mod.EventBusSubscriber(
        modid = MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class DecalShaders {
    private static ShaderInstance translucentCapture;
    private static ShaderInstance decalKBufferResolve;
    private static ShaderInstance decalCoverage;
    private static ShaderInstance decalOpaqueResolve;
    private static ShaderInstance opaqueLightmapCapture;

    private static KBufferSortShader kBufferSort;
    private static DecalOcclusionShader decalOcclusion;

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
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "decal_kbuffer_resolve"),
                        DefaultVertexFormat.POSITION_COLOR_LIGHTMAP
                ),
                shader -> decalKBufferResolve = shader
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
        event.registerShader(
                new ShaderInstance(
                        event.getResourceProvider(),
                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "decal_opaque_lightmap_capture"),
                        DefaultVertexFormat.BLOCK
                ),
                shader -> opaqueLightmapCapture = shader
        );

        // Compute shaders

        // kbuffer_sort
        if (kBufferSort != null)
            kBufferSort.destroy();
        kBufferSort = new KBufferSortShader();
        kBufferSort.compile(
                event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "shaders/kbuffer_sort.comp")
        );
        //decal_occlusion
        if (decalOcclusion != null)
            decalOcclusion.destroy();
        decalOcclusion = new DecalOcclusionShader();
        decalOcclusion.compile(
                event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "shaders/decal_occlusion.comp")
        );
    }

    public static void sortKBuffer(int width, int height) {
        if (kBufferSort == null)
            return;
        kBufferSort.dispatch(width, height);
    }

    public static boolean[] runDecalOcclusion(
            int decalCount,
            Matrix4f viewProjection,
            int decalBuffer,
            int depthPyramidTexture,
            int pyramidLevels,
            int width,
            int height,
            Vec3 cameraPosition
    ) {
        if (decalOcclusion == null)
            return new boolean[decalCount];
        decalOcclusion.dispatch(
                decalCount,
                viewProjection,
                decalBuffer,
                depthPyramidTexture,
                pyramidLevels,
                width,
                height,
                cameraPosition
        );
        return decalOcclusion.readVisibility(decalCount);
    }

    public static ShaderInstance getTranslucentCapture() {
        return translucentCapture;
    }
    public static ShaderInstance getDecalKBufferResolve() {
        return decalKBufferResolve;
    }
    public static ShaderInstance getDecalCoverage() {
        return decalCoverage;
    }
    public static ShaderInstance getDecalOpaqueResolve() {
        return decalOpaqueResolve;
    }
    public static ShaderInstance getOpaqueLightmapCapture() {
        return opaqueLightmapCapture;
    }
}
