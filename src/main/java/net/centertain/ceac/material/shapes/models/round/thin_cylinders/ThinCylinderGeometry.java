package net.centertain.ceac.material.shapes.models.round.thin_cylinders;

import net.centertain.ceac.material.utility.ModelHelper;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class ThinCylinderGeometry implements IUnbakedGeometry<ThinCylinderGeometry> {
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
                ModelHelper.vertex(0.5f, 0f, 0.25f, 0.5f, 1f),
                ModelHelper.vertex(0.5f, 1f, 0.25f, 0.5f, 0f),
                ModelHelper.vertex(0.595671f, 1f, 0.26903f, 0.404329f, 0f),
                ModelHelper.vertex(0.595671f, 0f, 0.26903f, 0.404329f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.595671f, 0f, 0.26903f, 0.404329f, 1f),
                ModelHelper.vertex(0.595671f, 1f, 0.26903f, 0.404329f, 0f),
                ModelHelper.vertex(0.676777f, 1f, 0.323223f, 0.323224f, 0f),
                ModelHelper.vertex(0.676777f, 0f, 0.323223f, 0.323224f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.676777f, 0f, 0.323223f, 0.676777f, 1f),
                ModelHelper.vertex(0.676777f, 1f, 0.323223f, 0.676777f, 0f),
                ModelHelper.vertex(0.73097f, 1f, 0.404329f, 0.595671f, 0f),
                ModelHelper.vertex(0.73097f, 0f, 0.404329f, 0.595671f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.73097f, 0f, 0.404329f, 0.595671f, 1f),
                ModelHelper.vertex(0.73097f, 1f, 0.404329f, 0.595671f, 0f),
                ModelHelper.vertex(0.75f, 1f, 0.5f, 0.5f, 0f),
                ModelHelper.vertex(0.75f, 0f, 0.5f, 0.5f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.75f, 0f, 0.5f, 0.5f, 1f),
                ModelHelper.vertex(0.75f, 1f, 0.5f, 0.5f, 0f),
                ModelHelper.vertex(0.73097f, 1f, 0.595671f, 0.404329f, 0f),
                ModelHelper.vertex(0.73097f, 0f, 0.595671f, 0.404329f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.73097f, 0f, 0.595671f, 0.404329f, 1f),
                ModelHelper.vertex(0.73097f, 1f, 0.595671f, 0.404329f, 0f),
                ModelHelper.vertex(0.676777f, 1f, 0.676777f, 0.323223f, 0f),
                ModelHelper.vertex(0.676777f, 0f, 0.676777f, 0.323223f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.676777f, 0f, 0.676777f, 0.676777f, 1f),
                ModelHelper.vertex(0.676777f, 1f, 0.676777f, 0.676777f, 0f),
                ModelHelper.vertex(0.595671f, 1f, 0.73097f, 0.595671f, 0f),
                ModelHelper.vertex(0.595671f, 0f, 0.73097f, 0.595671f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.595671f, 0f, 0.73097f, 0.595671f, 1f),
                ModelHelper.vertex(0.595671f, 1f, 0.73097f, 0.595671f, 0f),
                ModelHelper.vertex(0.5f, 1f, 0.75f, 0.5f, 0f),
                ModelHelper.vertex(0.5f, 0f, 0.75f, 0.5f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0f, 0.75f, 0.5f, 1f),
                ModelHelper.vertex(0.5f, 1f, 0.75f, 0.5f, 0f),
                ModelHelper.vertex(0.404329f, 1f, 0.73097f, 0.404329f, 0f),
                ModelHelper.vertex(0.404329f, 0f, 0.73097f, 0.404329f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.404329f, 0f, 0.73097f, 0.404329f, 1f),
                ModelHelper.vertex(0.404329f, 1f, 0.73097f, 0.404329f, 0f),
                ModelHelper.vertex(0.323223f, 1f, 0.676777f, 0.323223f, 0f),
                ModelHelper.vertex(0.323223f, 0f, 0.676777f, 0.323223f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.323223f, 0f, 0.676777f, 0.676777f, 1f),
                ModelHelper.vertex(0.323223f, 1f, 0.676777f, 0.676777f, 0f),
                ModelHelper.vertex(0.26903f, 1f, 0.595671f, 0.595671f, 0f),
                ModelHelper.vertex(0.26903f, 0f, 0.595671f, 0.595671f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.26903f, 0f, 0.595671f, 0.595671f, 1f),
                ModelHelper.vertex(0.26903f, 1f, 0.595671f, 0.595671f, 0f),
                ModelHelper.vertex(0.25f, 1f, 0.5f, 0.5f, 0f),
                ModelHelper.vertex(0.25f, 0f, 0.5f, 0.5f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.25f, 0f, 0.5f, 0.5f, 1f),
                ModelHelper.vertex(0.25f, 1f, 0.5f, 0.5f, 0f),
                ModelHelper.vertex(0.26903f, 1f, 0.404329f, 0.404329f, 0f),
                ModelHelper.vertex(0.26903f, 0f, 0.404329f, 0.404329f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.26903f, 0f, 0.404329f, 0.404329f, 1f),
                ModelHelper.vertex(0.26903f, 1f, 0.404329f, 0.404329f, 0f),
                ModelHelper.vertex(0.323223f, 1f, 0.323223f, 0.323223f, 0f),
                ModelHelper.vertex(0.323223f, 0f, 0.323223f, 0.323223f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.595671f, 1f, 0.26903f, 0.595671f, 0.26903f),
                ModelHelper.vertex(0.5f, 1f, 0.25f, 0.5f, 0.25f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.676777f, 1f, 0.323223f, 0.676777f, 0.323223f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.323223f, 0f, 0.323223f, 0.676777f, 1f),
                ModelHelper.vertex(0.323223f, 1f, 0.323223f, 0.676777f, 0f),
                ModelHelper.vertex(0.404329f, 1f, 0.26903f, 0.595671f, 0f),
                ModelHelper.vertex(0.404329f, 0f, 0.26903f, 0.595671f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.404329f, 0f, 0.26903f, 0.595671f, 1f),
                ModelHelper.vertex(0.404329f, 1f, 0.26903f, 0.595671f, 0f),
                ModelHelper.vertex(0.5f, 1f, 0.25f, 0.5f, 0f),
                ModelHelper.vertex(0.5f, 0f, 0.25f, 0.5f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0f, 0.25f, 0.5f, 0.75f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.323223f, 0f, 0.323223f, 0.323223f, 0.676776f),
                ModelHelper.vertex(0.404329f, 0f, 0.26903f, 0.404329f, 0.730969f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.75f, 1f, 0.5f, 0.75f, 0.5f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.676777f, 1f, 0.676777f, 0.676777f, 0.676777f),
                ModelHelper.vertex(0.73097f, 1f, 0.595671f, 0.73097f, 0.595671f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.5f, 1f, 0.25f, 0.5f, 0.25f),
                ModelHelper.vertex(0.404329f, 1f, 0.26903f, 0.404329f, 0.26903f),
                ModelHelper.vertex(0.323223f, 1f, 0.323223f, 0.323223f, 0.323223f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 1f, 0.75f, 0.5f, 0.75f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.323223f, 1f, 0.676777f, 0.323223f, 0.676777f),
                ModelHelper.vertex(0.404329f, 1f, 0.73097f, 0.404329f, 0.73097f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.676777f, 1f, 0.323223f, 0.676777f, 0.323223f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.75f, 1f, 0.5f, 0.75f, 0.5f),
                ModelHelper.vertex(0.73097f, 1f, 0.404329f, 0.73097f, 0.404329f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.323223f, 1f, 0.676777f, 0.323223f, 0.676777f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.25f, 1f, 0.5f, 0.25f, 0.5f),
                ModelHelper.vertex(0.26903f, 1f, 0.595671f, 0.26903f, 0.595671f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.676777f, 1f, 0.676777f, 0.676777f, 0.676777f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.5f, 1f, 0.75f, 0.5f, 0.75f),
                ModelHelper.vertex(0.595671f, 1f, 0.73097f, 0.595671f, 0.73097f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.323223f, 1f, 0.323223f, 0.323223f, 0.323223f),
                ModelHelper.vertex(0.26903f, 1f, 0.404329f, 0.26903f, 0.404329f),
                ModelHelper.vertex(0.25f, 1f, 0.5f, 0.25f, 0.5f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.25f, 0f, 0.5f, 0.25f, 0.5f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.323223f, 0f, 0.676777f, 0.323223f, 0.323223f),
                ModelHelper.vertex(0.26903f, 0f, 0.595671f, 0.26903f, 0.404329f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0f, 0.75f, 0.5f, 0.25f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.676777f, 0f, 0.676777f, 0.676777f, 0.323223f),
                ModelHelper.vertex(0.595671f, 0f, 0.73097f, 0.595671f, 0.26903f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.676777f, 0f, 0.323223f, 0.676777f, 0.676776f),
                ModelHelper.vertex(0.73097f, 0f, 0.404329f, 0.73097f, 0.59567f),
                ModelHelper.vertex(0.75f, 0f, 0.5f, 0.75f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.676777f, 0f, 0.323223f, 0.676777f, 0.676776f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.5f, 0f, 0.25f, 0.5f, 0.75f),
                ModelHelper.vertex(0.595671f, 0f, 0.26903f, 0.595671f, 0.730969f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.323223f, 0f, 0.676777f, 0.323223f, 0.323223f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.5f, 0f, 0.75f, 0.5f, 0.25f),
                ModelHelper.vertex(0.404329f, 0f, 0.73097f, 0.404329f, 0.26903f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.323223f, 0f, 0.323223f, 0.323223f, 0.676776f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.25f, 0f, 0.5f, 0.25f, 0.5f),
                ModelHelper.vertex(0.26903f, 0f, 0.404329f, 0.26903f, 0.59567f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.676777f, 0f, 0.676777f, 0.676777f, 0.323223f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.75f, 0f, 0.5f, 0.75f, 0.5f),
                ModelHelper.vertex(0.73097f, 0f, 0.595671f, 0.73097f, 0.404329f)
        ));

        COLLISION_SHAPE = builder.build(context.getRenderType(modelLocation));
        return COLLISION_SHAPE;
    }
}
