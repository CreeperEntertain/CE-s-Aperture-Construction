package net.centertain.ceac.material.shapes.models.basic_shapes.quarters;

import net.centertain.ceac.material.utility.ModelHelper;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class SixteenthPillarGeometry implements IUnbakedGeometry<SixteenthPillarGeometry> {
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
                ModelHelper.vertex(0f, 0f, 0.25f, 0.25f, 1f),
                ModelHelper.vertex(0f, 1f, 0.25f, 0.25f, 0f),
                ModelHelper.vertex(0f, 1f, 0f, -0f, 0f),
                ModelHelper.vertex(0f, 0f, 0f, -0f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(0f, 1f, 0f, 1f, 0f),
                ModelHelper.vertex(0.25f, 1f, 0f, 0.75f, 0f),
                ModelHelper.vertex(0.25f, 0f, 0f, 0.75f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.25f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(0.25f, 1f, 0f, 1f, 0f),
                ModelHelper.vertex(0.25f, 1f, 0.25f, 0.75f, 0f),
                ModelHelper.vertex(0.25f, 0f, 0.25f, 0.75f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.25f, 0f, 0.25f, 0.25f, 1f),
                ModelHelper.vertex(0.25f, 1f, 0.25f, 0.25f, 0f),
                ModelHelper.vertex(0f, 1f, 0.25f, 0f, 0f),
                ModelHelper.vertex(0f, 0f, 0.25f, 0f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0f, 0f, 0f, -0f, 0.999999f),
                ModelHelper.vertex(0.25f, 0f, 0f, 0.25f, 1f),
                ModelHelper.vertex(0.25f, 0f, 0.25f, 0.25f, 0.749999f),
                ModelHelper.vertex(0f, 0f, 0.25f, -0f, 0.749999f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.25f, 1f, 0f, 0.25f, -0f),
                ModelHelper.vertex(0f, 1f, 0f, 0f, -0f),
                ModelHelper.vertex(0f, 1f, 0.25f, 0f, 0.25f),
                ModelHelper.vertex(0.25f, 1f, 0.25f, 0.25f, 0.25f)
        ));

        COLLISION_SHAPE = builder.build(context.getRenderType(modelLocation));
        return COLLISION_SHAPE;
    }
}
