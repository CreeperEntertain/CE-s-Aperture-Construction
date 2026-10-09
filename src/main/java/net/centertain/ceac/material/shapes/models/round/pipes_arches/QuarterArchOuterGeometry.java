package net.centertain.ceac.material.shapes.models.round.pipes_arches;

import net.centertain.ceac.material.utility.ModelHelper;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class QuarterArchOuterGeometry implements IUnbakedGeometry<QuarterArchOuterGeometry> {
    public static BakedModel COLLISION_SHAPE;

    @Override
    public BakedModel bake(
            IGeometryBakingContext context,
            ModelBaker baker,
            Function<Material, TextureAtlasSprite> sprites,
            ModelState modelState,
            ItemOverrides overrides,
            ResourceLocation modelLocation
    ) {
        TextureAtlasSprite sprite = sprites.apply(context.getMaterial("texture"));

        SimpleBakedModel.Builder builder = new SimpleBakedModel.Builder(
                context.useAmbientOcclusion(),
                context.useBlockLight(),
                context.isGui3d(),
                context.getTransforms(),
                overrides
        );

        builder.particle(sprite);

        builder.addUnculledFace(ModelHelper.triangle(
                sprite,
                ModelHelper.vertex(0f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(0.382683f, 0.07612f, 0f, 0.617317f, 0.92388f),
                ModelHelper.vertex(0.5f, 0f, 0f, 0.5f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.382683f, 0.07612f, 0f, 0.617317f, 0.92388f),
                ModelHelper.vertex(0.707107f, 0.292893f, 0f, 0.292893f, 0.707107f),
                ModelHelper.vertex(1f, 0f, 0f, 0f, 1f),
                ModelHelper.vertex(0.5f, 0f, 0f, 0.5f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.707107f, 0.292893f, 0f, 0.292893f, 0.707107f),
                ModelHelper.vertex(0.923879f, 0.617317f, 0f, 0.076121f, 0.382684f),
                ModelHelper.vertex(1f, 0.5f, 0f, 0f, 0.5f),
                ModelHelper.vertex(1f, 0f, 0f, 0f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.triangle(
                sprite,
                ModelHelper.vertex(1f, 0.5f, 0f, 0f, 0.5f),
                ModelHelper.vertex(0.923879f, 0.617317f, 0f, 0.076121f, 0.382684f),
                ModelHelper.vertex(1f, 1f, 0f, 0f, 0f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 1f, 0f, -0f, 1f),
                ModelHelper.vertex(1f, 1f, 0f, -0f, 1f),
                ModelHelper.vertex(1f, 0.5f, 0f, 0.5f, 0.5f),
                ModelHelper.vertex(1f, 0.5f, 0f, 0.5f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.429674f, 0.07612f, 0.236237f, 0.429674f, 0.236237f),
                ModelHelper.vertex(0.563491f, 0.07612f, 0.436509f, 0.563491f, 0.436509f),
                ModelHelper.vertex(0.792893f, 0.292893f, 0.207107f, 0.792893f, 0.207107f),
                ModelHelper.vertex(0.729402f, 0.292893f, 0.112085f, 0.729402f, 0.112086f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.929674f, 0.617317f, 0.02913f, 0.02913f, 0.382684f),
                ModelHelper.vertex(0.946175f, 0.617317f, 0.053825f, 0.053825f, 0.382684f),
                ModelHelper.vertex(1f, 1f, 0f, 0f, 0f),
                ModelHelper.vertex(1f, 1f, 0f, -0f, 0f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 0.5f, 0f, 0.5f, 0.5f),
                ModelHelper.vertex(1f, 0.5f, 0f, 0.5f, 0.5f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 0f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 0f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 0.5f, 0f, 0.5f, 0.5f),
                ModelHelper.vertex(1f, 0.5f, 0f, 0.5f, 0.5f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 0f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 0f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.923879f, 0.617317f, 0f, -0f, 0.382684f),
                ModelHelper.vertex(0.929674f, 0.617317f, 0.02913f, 0.02913f, 0.382684f),
                ModelHelper.vertex(1f, 1f, 0f, -0f, 0f),
                ModelHelper.vertex(1f, 1f, 0f, -0f, 0f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.382683f, 0.07612f, 0f, 0.382684f, 0f),
                ModelHelper.vertex(0.429674f, 0.07612f, 0.236237f, 0.429674f, 0.236237f),
                ModelHelper.vertex(0.729402f, 0.292893f, 0.112085f, 0.729402f, 0.112086f),
                ModelHelper.vertex(0.707107f, 0.292893f, 0f, 0.707107f, 0f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 1f, 0f, -0f, 1f),
                ModelHelper.vertex(1f, 1f, 0f, -0f, 1f),
                ModelHelper.vertex(1f, 0.5f, 0f, 0.5f, 0.5f),
                ModelHelper.vertex(1f, 0.5f, 0f, 0.5f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(0.53806f, 0f, 0.191342f, 0.53806f, 0.808658f),
                ModelHelper.vertex(0.5f, 0f, 0f, 0.5f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0f, 0f, 0.5f, 1f),
                ModelHelper.vertex(0.53806f, 0f, 0.191342f, 0.53806f, 0.808658f),
                ModelHelper.vertex(0.07612f, 0f, 0.382683f, 0.07612f, 0.617316f),
                ModelHelper.vertex(0f, 0f, 0f, -0f, 0.999999f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.707107f, 0.292893f, 0f, -0f, 0.707107f),
                ModelHelper.vertex(0.729402f, 0.292893f, 0.112085f, 0.112085f, 0.707107f),
                ModelHelper.vertex(0.929674f, 0.617317f, 0.02913f, 0.02913f, 0.382684f),
                ModelHelper.vertex(0.923879f, 0.617317f, 0f, -0f, 0.382684f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0f, 0f, 0f, 0f, -0f),
                ModelHelper.vertex(0.07612f, 0f, 0.382683f, 0.07612f, 0.382683f),
                ModelHelper.vertex(0.429674f, 0.07612f, 0.236237f, 0.429674f, 0.236237f),
                ModelHelper.vertex(0.382683f, 0.07612f, 0f, 0.382684f, 0f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.292893f, 0f, 0.707107f, 0.292893f, 0.707107f),
                ModelHelper.vertex(0.617316f, 0f, 0.92388f, 0.617316f, 0.92388f),
                ModelHelper.vertex(0.763763f, 0.07612f, 0.570326f, 0.763763f, 0.570326f),
                ModelHelper.vertex(0.563491f, 0.07612f, 0.436509f, 0.563491f, 0.436509f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.792893f, 0.292893f, 0.207107f, 0.792893f, 0.707107f),
                ModelHelper.vertex(0.887915f, 0.292893f, 0.270598f, 0.887915f, 0.707107f),
                ModelHelper.vertex(0.97087f, 0.617317f, 0.070326f, 0.97087f, 0.382684f),
                ModelHelper.vertex(0.946175f, 0.617317f, 0.053825f, 0.946175f, 0.382684f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.646447f, 0f, 0.353553f, 0.646446f, 0.646446f),
                ModelHelper.vertex(0.808658f, 0f, 0.46194f, 0.808658f, 0.53806f),
                ModelHelper.vertex(0.617316f, 0f, 0.92388f, 0.617317f, 0.07612f),
                ModelHelper.vertex(0.292893f, 0f, 0.707107f, 0.292893f, 0.292893f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(0.808658f, 0f, 0.46194f, 0.808658f, 0.53806f),
                ModelHelper.vertex(0.646447f, 0f, 0.353553f, 0.646446f, 0.646446f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(0.646447f, 0f, 0.353553f, 0.646446f, 0.646446f),
                ModelHelper.vertex(0.53806f, 0f, 0.191342f, 0.53806f, 0.808658f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.53806f, 0f, 0.191342f, 0.53806f, 0.808658f),
                ModelHelper.vertex(0.646447f, 0f, 0.353553f, 0.646446f, 0.646446f),
                ModelHelper.vertex(0.292893f, 0f, 0.707107f, 0.292893f, 0.292893f),
                ModelHelper.vertex(0.07612f, 0f, 0.382683f, 0.07612f, 0.617316f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.729402f, 0.292893f, 0.112085f, 0.112085f, 0.707107f),
                ModelHelper.vertex(0.792893f, 0.292893f, 0.207107f, 0.207107f, 0.707107f),
                ModelHelper.vertex(0.946175f, 0.617317f, 0.053825f, 0.053825f, 0.382684f),
                ModelHelper.vertex(0.929674f, 0.617317f, 0.02913f, 0.02913f, 0.382684f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.07612f, 0f, 0.382683f, 0.07612f, 0.382683f),
                ModelHelper.vertex(0.292893f, 0f, 0.707107f, 0.292893f, 0.707107f),
                ModelHelper.vertex(0.563491f, 0.07612f, 0.436509f, 0.563491f, 0.436509f),
                ModelHelper.vertex(0.429674f, 0.07612f, 0.236237f, 0.429674f, 0.236237f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(1f, 0f, 0.5f, 1f, 0.5f),
                ModelHelper.vertex(0.808658f, 0f, 0.46194f, 0.808658f, 0.53806f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 1f, 0f, -0f, 1f),
                ModelHelper.vertex(1f, 1f, 0f, -0f, 1f),
                ModelHelper.vertex(1f, 0.5f, 0f, 0.5f, 0.5f),
                ModelHelper.vertex(1f, 0.5f, 0f, 0.5f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.763763f, 0.07612f, 0.570326f, 0.763763f, 0.570326f),
                ModelHelper.vertex(1f, 0.07612f, 0.617317f, 1f, 0.617317f),
                ModelHelper.vertex(1f, 0.292893f, 0.292893f, 1f, 0.292894f),
                ModelHelper.vertex(0.887915f, 0.292893f, 0.270598f, 0.887915f, 0.270598f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.97087f, 0.617317f, 0.070326f, 0.97087f, 0.382684f),
                ModelHelper.vertex(1f, 0.617317f, 0.076121f, 1f, 0.382684f),
                ModelHelper.vertex(1f, 1f, 0f, 1f, 0f),
                ModelHelper.vertex(1f, 1f, 0f, 1f, 0f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 0.5f, 0f, 0.5f, 0.5f),
                ModelHelper.vertex(1f, 0.5f, 0f, 0.5f, 0.5f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 0f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 0f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.946175f, 0.617317f, 0.053825f, 0.946175f, 0.382684f),
                ModelHelper.vertex(0.97087f, 0.617317f, 0.070326f, 0.97087f, 0.382684f),
                ModelHelper.vertex(1f, 1f, 0f, 1f, 0f),
                ModelHelper.vertex(1f, 1f, 0f, 1f, 0f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.563491f, 0.07612f, 0.436509f, 0.563491f, 0.436509f),
                ModelHelper.vertex(0.763763f, 0.07612f, 0.570326f, 0.763763f, 0.570326f),
                ModelHelper.vertex(0.887915f, 0.292893f, 0.270598f, 0.887915f, 0.270598f),
                ModelHelper.vertex(0.792893f, 0.292893f, 0.207107f, 0.792893f, 0.207107f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 1f, 0f, -0f, 1f),
                ModelHelper.vertex(1f, 1f, 0f, -0f, 1f),
                ModelHelper.vertex(1f, 0.5f, 0f, 0.5f, 0.5f),
                ModelHelper.vertex(1f, 0.5f, 0f, 0.5f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.triangle(
                sprite,
                ModelHelper.vertex(1f, 0f, 1f, -0f, 1f),
                ModelHelper.vertex(1f, 0f, 0.5f, 0.5f, 1f),
                ModelHelper.vertex(1f, 0.07612f, 0.617317f, 0.382683f, 0.92388f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 0.07612f, 0.617317f, 0.382683f, 0.92388f),
                ModelHelper.vertex(1f, 0f, 0.5f, 0.5f, 1f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(1f, 0.292893f, 0.292893f, 0.707107f, 0.707107f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 0.292893f, 0.292893f, 0.707107f, 0.707107f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(1f, 0.5f, 0f, 1f, 0.5f),
                ModelHelper.vertex(1f, 0.617317f, 0.076121f, 0.923879f, 0.382683f)
        ));

        builder.addUnculledFace(ModelHelper.triangle(
                sprite,
                ModelHelper.vertex(1f, 0.5f, 0f, 1f, 0.5f),
                ModelHelper.vertex(1f, 1f, 0f, 1f, 0f),
                ModelHelper.vertex(1f, 0.617317f, 0.076121f, 0.923879f, 0.382683f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.808658f, 0f, 0.46194f, 0.808658f, 0.53806f),
                ModelHelper.vertex(1f, 0f, 0.5f, 1f, 0.5f),
                ModelHelper.vertex(1f, 0f, 1f, 1f, -0f),
                ModelHelper.vertex(0.617316f, 0f, 0.92388f, 0.617317f, 0.07612f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.887915f, 0.292893f, 0.270598f, 0.887915f, 0.707107f),
                ModelHelper.vertex(1f, 0.292893f, 0.292893f, 1f, 0.707107f),
                ModelHelper.vertex(1f, 0.617317f, 0.076121f, 1f, 0.382684f),
                ModelHelper.vertex(0.97087f, 0.617317f, 0.070326f, 0.97087f, 0.382684f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.617316f, 0f, 0.92388f, 0.617316f, 0.92388f),
                ModelHelper.vertex(1f, 0f, 1f, 1f, 1f),
                ModelHelper.vertex(1f, 0.07612f, 0.617317f, 1f, 0.617317f),
                ModelHelper.vertex(0.763763f, 0.07612f, 0.570326f, 0.763763f, 0.570326f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 0.5f, 0f, 0.5f, 0.5f),
                ModelHelper.vertex(1f, 0.5f, 0f, 0.5f, 0.5f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 0f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 0f)
        ));

        COLLISION_SHAPE = builder.build(context.getRenderType(modelLocation));
        return COLLISION_SHAPE;
    }
}
