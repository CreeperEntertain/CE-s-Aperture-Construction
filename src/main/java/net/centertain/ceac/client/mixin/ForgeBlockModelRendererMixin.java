package net.centertain.ceac.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.centertain.ceac.material.shapes.MaterialShapeBakedModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.lighting.ForgeModelBlockRenderer;
import net.minecraftforge.client.model.lighting.QuadLighter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeModelBlockRenderer.class)
public abstract class ForgeBlockModelRendererMixin {
    @Shadow @Final
    private ThreadLocal<QuadLighter> smoothLighter;

    @Inject(
            method = "tesselateWithAO(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JILnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void ceac$renderMaterialShapesWithSmoothAO(
            BlockAndTintGetter level,
            BakedModel model,
            BlockState state,
            BlockPos pos,
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            boolean checkSides,
            RandomSource rand,
            long seed,
            int packedOverlay,
            ModelData modelData,
            RenderType renderType,
            CallbackInfo ci
    ) {
        if (!(model instanceof MaterialShapeBakedModel))
            return;

        ForgeModelBlockRenderer.render(
                vertexConsumer,
                smoothLighter.get(),
                level,
                model,
                state,
                pos,
                poseStack,
                checkSides,
                rand,
                seed,
                packedOverlay,
                modelData,
                renderType
        );

        ci.cancel();
    }
}
