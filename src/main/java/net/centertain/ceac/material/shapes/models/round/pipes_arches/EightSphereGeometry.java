package net.centertain.ceac.material.shapes.models.round.pipes_arches;

import net.centertain.ceac.material.utility.ModelHelper;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class EightSphereGeometry implements IUnbakedGeometry<EightSphereGeometry> {
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
                ModelHelper.vertex(0f, 0f, 0f, -0f, 1f),
                ModelHelper.vertex(0.064586f, 0f, 0.353553f, 0.353553f, 1f),
                ModelHelper.vertex(0.122029f, 0.338502f, 0.338502f, 0.338502f, 0.661498f),
                ModelHelper.vertex(0.064586f, 0.353553f, 0f, -0f, 0.646447f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.064586f, 0f, 0.353553f, 0.353553f, 1f),
                ModelHelper.vertex(0.292893f, 0f, 0.707107f, 0.707107f, 1f),
                ModelHelper.vertex(0.322997f, 0.288675f, 0.677003f, 0.677003f, 0.711325f),
                ModelHelper.vertex(0.122029f, 0.338502f, 0.338502f, 0.338502f, 0.661498f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.064586f, 0.353553f, 0f, -0f, 0.646447f),
                ModelHelper.vertex(0.122029f, 0.338502f, 0.338502f, 0.338502f, 0.661498f),
                ModelHelper.vertex(0.322997f, 0.677003f, 0.288675f, 0.288675f, 0.322997f),
                ModelHelper.vertex(0.292893f, 0.707107f, 0f, -0f, 0.292893f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.122029f, 0.338502f, 0.338502f, 0.338502f, 0.661498f),
                ModelHelper.vertex(0.322997f, 0.288675f, 0.677003f, 0.677003f, 0.711325f),
                ModelHelper.vertex(0.42265f, 0.57735f, 0.57735f, 0.57735f, 0.42265f),
                ModelHelper.vertex(0.322997f, 0.677003f, 0.288675f, 0.288675f, 0.322997f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.292893f, 0f, 0.707107f, 0.292893f, 1f),
                ModelHelper.vertex(0.646447f, 0f, 0.935414f, 0.646447f, 1f),
                ModelHelper.vertex(0.661498f, 0.338502f, 0.877971f, 0.661498f, 0.661498f),
                ModelHelper.vertex(0.322997f, 0.288675f, 0.677003f, 0.322997f, 0.711325f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.646447f, 0f, 0.935414f, 0.646447f, 1f),
                ModelHelper.vertex(1f, 0f, 1f, 1f, 1f),
                ModelHelper.vertex(1f, 0.353553f, 0.935414f, 1f, 0.646447f),
                ModelHelper.vertex(0.661498f, 0.338502f, 0.877971f, 0.661498f, 0.661498f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.322997f, 0.288675f, 0.677003f, 0.322997f, 0.711325f),
                ModelHelper.vertex(0.661498f, 0.338502f, 0.877971f, 0.661498f, 0.661498f),
                ModelHelper.vertex(0.711325f, 0.677003f, 0.677003f, 0.711325f, 0.322997f),
                ModelHelper.vertex(0.42265f, 0.57735f, 0.57735f, 0.42265f, 0.42265f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.661498f, 0.338502f, 0.877971f, 0.661498f, 0.661498f),
                ModelHelper.vertex(1f, 0.353553f, 0.935414f, 1f, 0.646447f),
                ModelHelper.vertex(1f, 0.707107f, 0.707107f, 1f, 0.292893f),
                ModelHelper.vertex(0.711325f, 0.677003f, 0.677003f, 0.711325f, 0.322997f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 1f, 0f, 1f, 0f),
                ModelHelper.vertex(0.646447f, 0.935414f, 0f, 0.646447f, 0f),
                ModelHelper.vertex(0.661498f, 0.877971f, 0.338502f, 0.661499f, 0.338502f),
                ModelHelper.vertex(1f, 0.935414f, 0.353553f, 1f, 0.353554f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.646447f, 0.935414f, 0f, 0.646447f, 0f),
                ModelHelper.vertex(0.292893f, 0.707107f, 0f, 0.292894f, -0f),
                ModelHelper.vertex(0.322997f, 0.677003f, 0.288675f, 0.322997f, 0.288675f),
                ModelHelper.vertex(0.661498f, 0.877971f, 0.338502f, 0.661499f, 0.338502f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 0.935414f, 0.353553f, 1f, 0.353554f),
                ModelHelper.vertex(0.661498f, 0.877971f, 0.338502f, 0.661499f, 0.338502f),
                ModelHelper.vertex(0.711325f, 0.677003f, 0.677003f, 0.711325f, 0.677003f),
                ModelHelper.vertex(1f, 0.707107f, 0.707107f, 1f, 0.707107f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.661498f, 0.877971f, 0.338502f, 0.661499f, 0.338502f),
                ModelHelper.vertex(0.322997f, 0.677003f, 0.288675f, 0.322997f, 0.288675f),
                ModelHelper.vertex(0.42265f, 0.57735f, 0.57735f, 0.42265f, 0.57735f),
                ModelHelper.vertex(0.711325f, 0.677003f, 0.677003f, 0.711325f, 0.677003f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 1f, 0f, 0f, 0f),
                ModelHelper.vertex(1f, 0f, 0f, 0f, 1f),
                ModelHelper.vertex(0.292893f, 0.707107f, 0f, 0.707107f, 0.292893f),
                ModelHelper.vertex(0.646447f, 0.935414f, 0f, 0.353554f, 0.064586f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(0.064586f, 0.353553f, 0f, 0.935415f, 0.646447f),
                ModelHelper.vertex(0.292893f, 0.707107f, 0f, 0.707107f, 0.292893f),
                ModelHelper.vertex(1f, 0f, 0f, 0f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 1f, 0f, 1f, 0f),
                ModelHelper.vertex(1f, 0.935414f, 0.353553f, 0.646447f, 0.064586f),
                ModelHelper.vertex(1f, 0.707107f, 0.707107f, 0.292893f, 0.292893f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 0f, 1f, 0f, 1f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(1f, 0.707107f, 0.707107f, 0.292893f, 0.292893f),
                ModelHelper.vertex(1f, 0.353553f, 0.935414f, 0.064586f, 0.646447f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.292893f, 0f, 0.707107f, 0.292893f, 0.292893f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(1f, 0f, 1f, 1f, -0f),
                ModelHelper.vertex(0.646447f, 0f, 0.935414f, 0.646447f, 0.064585f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0f, 0f, 0f, -0f, 0.999999f),
                ModelHelper.vertex(1f, 0f, 0f, 1f, 1f),
                ModelHelper.vertex(0.292893f, 0f, 0.707107f, 0.292893f, 0.292893f),
                ModelHelper.vertex(0.064586f, 0f, 0.353553f, 0.064586f, 0.646446f)
        ));

        COLLISION_SHAPE = builder.build(context.getRenderType(modelLocation));
        return COLLISION_SHAPE;
    }
}
