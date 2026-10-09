package net.centertain.ceac.material.shapes.models.round.pipes_arches;

import net.centertain.ceac.material.utility.ModelHelper;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class QuarterCylinderGeometry implements IUnbakedGeometry<QuarterCylinderGeometry> {
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

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 0f, 1f, 1f, 1f),
                ModelHelper.vertex(1f, 1f, 1f, 1f, 0f),
                ModelHelper.vertex(0.617317f, 1f, 0.92388f, 0.617317f, 0f),
                ModelHelper.vertex(0.617317f, 0f, 0.92388f, 0.617317f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.617317f, 0f, 0.92388f, 0.617317f, 1f),
                ModelHelper.vertex(0.617317f, 1f, 0.92388f, 0.617317f, 0f),
                ModelHelper.vertex(0.292893f, 1f, 0.707107f, 0.292893f, 0f),
                ModelHelper.vertex(0.292893f, 0f, 0.707107f, 0.292893f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.292893f, 0f, 0.707107f, 0.707107f, 1f),
                ModelHelper.vertex(0.292893f, 1f, 0.707107f, 0.707107f, 0f),
                ModelHelper.vertex(0.07612f, 1f, 0.382683f, 0.382683f, 0f),
                ModelHelper.vertex(0.07612f, 0f, 0.382683f, 0.382683f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.07612f, 0f, 0.382683f, 0.382683f, 1f),
                ModelHelper.vertex(0.07612f, 1f, 0.382683f, 0.382683f, 0f),
                ModelHelper.vertex(0f, 1f, 0f, -0f, 0f),
                ModelHelper.vertex(0f, 0f, 0f, -0f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 1f, 1f, 0f, 0f),
                ModelHelper.vertex(1f, 0f, 1f, 0f, 1f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(1f, 1f, 0f, 1f, 0f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0f, 0f, 0f, -0f, 0.999999f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(0.292893f, 0f, 0.707107f, 0.292893f, 0.292893f),
                ModelHelper.vertex(0.07612f, 0f, 0.382683f, 0.07612f, 0.617316f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.292893f, 1f, 0.707107f, 0.292893f, 0.707107f),
                ModelHelper.vertex(1f, 1f, 0f, 1f, 0f),
                ModelHelper.vertex(0f, 1f, 0f, 0f, -0f),
                ModelHelper.vertex(0.07612f, 1f, 0.382683f, 0.076121f, 0.382683f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.292893f, 0f, 0.707107f, 0.292893f, 0.292893f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(1f, 0f, 1f, 1f, -0f),
                ModelHelper.vertex(0.617317f, 0f, 0.92388f, 0.617317f, 0.07612f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0f, 1f, 0f, 1f, 0f),
                ModelHelper.vertex(1f, 1f, 0f, 0f, 0f),
                ModelHelper.vertex(1f, 0f, 0f, 0f, 1f),
                ModelHelper.vertex(0f, 0f, 0f, 1f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 1f, 1f, 1f, 1f),
                ModelHelper.vertex(1f, 1f, 0f, 1f, 0f),
                ModelHelper.vertex(0.292893f, 1f, 0.707107f, 0.292893f, 0.707107f),
                ModelHelper.vertex(0.617317f, 1f, 0.92388f, 0.617316f, 0.92388f)
        ));

        COLLISION_SHAPE = builder.build(context.getRenderType(modelLocation));
        return COLLISION_SHAPE;
    }
}
