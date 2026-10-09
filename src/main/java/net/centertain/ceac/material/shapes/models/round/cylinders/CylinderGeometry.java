package net.centertain.ceac.material.shapes.models.round.cylinders;

import net.centertain.ceac.material.utility.ModelHelper;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class CylinderGeometry implements IUnbakedGeometry<CylinderGeometry> {
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
                ModelHelper.vertex(0.5f, 0f, 0f, 0.5f, 1f),
                ModelHelper.vertex(0.5f, 1f, 0f, 0.5f, 0f),
                ModelHelper.vertex(0.691342f, 1f, 0.03806f, 0.308658f, 0f),
                ModelHelper.vertex(0.691342f, 0f, 0.03806f, 0.308658f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.691342f, 0f, 0.03806f, 0.308658f, 1f),
                ModelHelper.vertex(0.691342f, 1f, 0.03806f, 0.308658f, 0f),
                ModelHelper.vertex(0.853553f, 1f, 0.146447f, 0.146447f, 0f),
                ModelHelper.vertex(0.853553f, 0f, 0.146447f, 0.146447f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.853553f, 0f, 0.146447f, 0.853553f, 1f),
                ModelHelper.vertex(0.853553f, 1f, 0.146447f, 0.853553f, 0f),
                ModelHelper.vertex(0.96194f, 1f, 0.308658f, 0.691342f, 0f),
                ModelHelper.vertex(0.96194f, 0f, 0.308658f, 0.691342f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.96194f, 0f, 0.308658f, 0.691342f, 1f),
                ModelHelper.vertex(0.96194f, 1f, 0.308658f, 0.691342f, 0f),
                ModelHelper.vertex(1f, 1f, 0.5f, 0.5f, 0f),
                ModelHelper.vertex(1f, 0f, 0.5f, 0.5f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 0f, 0.5f, 0.5f, 1f),
                ModelHelper.vertex(1f, 1f, 0.5f, 0.5f, 0f),
                ModelHelper.vertex(0.96194f, 1f, 0.691342f, 0.308658f, 0f),
                ModelHelper.vertex(0.96194f, 0f, 0.691342f, 0.308658f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.96194f, 0f, 0.691342f, 0.308658f, 1f),
                ModelHelper.vertex(0.96194f, 1f, 0.691342f, 0.308658f, 0f),
                ModelHelper.vertex(0.853553f, 1f, 0.853553f, 0.146447f, 0f),
                ModelHelper.vertex(0.853553f, 0f, 0.853553f, 0.146447f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.853553f, 0f, 0.853553f, 0.853553f, 1f),
                ModelHelper.vertex(0.853553f, 1f, 0.853553f, 0.853553f, 0f),
                ModelHelper.vertex(0.691342f, 1f, 0.96194f, 0.691342f, 0f),
                ModelHelper.vertex(0.691342f, 0f, 0.96194f, 0.691342f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.691342f, 0f, 0.96194f, 0.691342f, 1f),
                ModelHelper.vertex(0.691342f, 1f, 0.96194f, 0.691342f, 0f),
                ModelHelper.vertex(0.5f, 1f, 1f, 0.5f, 0f),
                ModelHelper.vertex(0.5f, 0f, 1f, 0.5f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0f, 1f, 0.5f, 1f),
                ModelHelper.vertex(0.5f, 1f, 1f, 0.5f, 0f),
                ModelHelper.vertex(0.308658f, 1f, 0.96194f, 0.308658f, 0f),
                ModelHelper.vertex(0.308658f, 0f, 0.96194f, 0.308658f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.308658f, 0f, 0.96194f, 0.308658f, 1f),
                ModelHelper.vertex(0.308658f, 1f, 0.96194f, 0.308658f, 0f),
                ModelHelper.vertex(0.146447f, 1f, 0.853553f, 0.146447f, 0f),
                ModelHelper.vertex(0.146447f, 0f, 0.853553f, 0.146447f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.146447f, 0f, 0.853553f, 0.853553f, 1f),
                ModelHelper.vertex(0.146447f, 1f, 0.853553f, 0.853553f, 0f),
                ModelHelper.vertex(0.03806f, 1f, 0.691342f, 0.691342f, 0f),
                ModelHelper.vertex(0.03806f, 0f, 0.691342f, 0.691342f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.03806f, 0f, 0.691342f, 0.691342f, 1f),
                ModelHelper.vertex(0.03806f, 1f, 0.691342f, 0.691342f, 0f),
                ModelHelper.vertex(0f, 1f, 0.5f, 0.5f, 0f),
                ModelHelper.vertex(0f, 0f, 0.5f, 0.5f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0f, 0f, 0.5f, 0.5f, 1f),
                ModelHelper.vertex(0f, 1f, 0.5f, 0.5f, 0f),
                ModelHelper.vertex(0.03806f, 1f, 0.308658f, 0.308658f, 0f),
                ModelHelper.vertex(0.03806f, 0f, 0.308658f, 0.308658f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.03806f, 0f, 0.308658f, 0.308658f, 1f),
                ModelHelper.vertex(0.03806f, 1f, 0.308658f, 0.308658f, 0f),
                ModelHelper.vertex(0.146447f, 1f, 0.146447f, 0.146447f, 0f),
                ModelHelper.vertex(0.146447f, 0f, 0.146447f, 0.146447f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.691342f, 1f, 0.03806f, 0.691342f, 0.03806f),
                ModelHelper.vertex(0.5f, 1f, 0f, 0.5f, 0f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.853553f, 1f, 0.146447f, 0.853554f, 0.146447f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.146447f, 0f, 0.146447f, 0.853554f, 1f),
                ModelHelper.vertex(0.146447f, 1f, 0.146447f, 0.853554f, 0f),
                ModelHelper.vertex(0.308658f, 1f, 0.03806f, 0.691342f, 0f),
                ModelHelper.vertex(0.308658f, 0f, 0.03806f, 0.691342f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.308658f, 0f, 0.03806f, 0.691342f, 1f),
                ModelHelper.vertex(0.308658f, 1f, 0.03806f, 0.691342f, 0f),
                ModelHelper.vertex(0.5f, 1f, 0f, 0.5f, 0f),
                ModelHelper.vertex(0.5f, 0f, 0f, 0.5f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0f, 0f, 0.5f, 1f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.146447f, 0f, 0.146447f, 0.146446f, 0.853553f),
                ModelHelper.vertex(0.308658f, 0f, 0.03806f, 0.308658f, 0.961939f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 1f, 0.5f, 1f, 0.5f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.853553f, 1f, 0.853553f, 0.853553f, 0.853554f),
                ModelHelper.vertex(0.96194f, 1f, 0.691342f, 0.96194f, 0.691342f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.5f, 1f, 0f, 0.5f, 0f),
                ModelHelper.vertex(0.308658f, 1f, 0.03806f, 0.308659f, 0.03806f),
                ModelHelper.vertex(0.146447f, 1f, 0.146447f, 0.146447f, 0.146446f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 1f, 1f, 0.5f, 1f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.146447f, 1f, 0.853553f, 0.146446f, 0.853553f),
                ModelHelper.vertex(0.308658f, 1f, 0.96194f, 0.308658f, 0.96194f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.853553f, 1f, 0.146447f, 0.853554f, 0.146447f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(1f, 1f, 0.5f, 1f, 0.5f),
                ModelHelper.vertex(0.96194f, 1f, 0.308658f, 0.96194f, 0.308659f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.146447f, 1f, 0.853553f, 0.146446f, 0.853553f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0f, 1f, 0.5f, 0f, 0.5f),
                ModelHelper.vertex(0.03806f, 1f, 0.691342f, 0.03806f, 0.691341f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.853553f, 1f, 0.853553f, 0.853553f, 0.853554f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.5f, 1f, 1f, 0.5f, 1f),
                ModelHelper.vertex(0.691342f, 1f, 0.96194f, 0.691341f, 0.96194f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.146447f, 1f, 0.146447f, 0.146447f, 0.146446f),
                ModelHelper.vertex(0.03806f, 1f, 0.308658f, 0.03806f, 0.308658f),
                ModelHelper.vertex(0f, 1f, 0.5f, 0f, 0.5f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0f, 0f, 0.5f, -0f, 0.499999f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.146447f, 0f, 0.853553f, 0.146447f, 0.146446f),
                ModelHelper.vertex(0.03806f, 0f, 0.691342f, 0.03806f, 0.308658f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0f, 1f, 0.5f, -0f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.853553f, 0f, 0.853553f, 0.853553f, 0.146446f),
                ModelHelper.vertex(0.691342f, 0f, 0.96194f, 0.691342f, 0.03806f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.853553f, 0f, 0.146447f, 0.853553f, 0.853553f),
                ModelHelper.vertex(0.96194f, 0f, 0.308658f, 0.96194f, 0.691341f),
                ModelHelper.vertex(1f, 0f, 0.5f, 1f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.853553f, 0f, 0.146447f, 0.853553f, 0.853553f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.5f, 0f, 0f, 0.5f, 1f),
                ModelHelper.vertex(0.691342f, 0f, 0.03806f, 0.691342f, 0.961939f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.146447f, 0f, 0.853553f, 0.146447f, 0.146446f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.5f, 0f, 1f, 0.5f, -0f),
                ModelHelper.vertex(0.308658f, 0f, 0.96194f, 0.308658f, 0.03806f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.146447f, 0f, 0.146447f, 0.146446f, 0.853553f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0f, 0f, 0.5f, -0f, 0.499999f),
                ModelHelper.vertex(0.03806f, 0f, 0.308658f, 0.03806f, 0.691341f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.853553f, 0f, 0.853553f, 0.853553f, 0.146446f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(1f, 0f, 0.5f, 1f, 0.5f),
                ModelHelper.vertex(0.96194f, 0f, 0.691342f, 0.96194f, 0.308658f)
        ));

        COLLISION_SHAPE = builder.build(context.getRenderType(modelLocation));
        return COLLISION_SHAPE;
    }
}
