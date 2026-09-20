package net.centertain.ceac.material.models;

import net.centertain.ceac.material.utility.ModelShapeHelper;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class ModelShapeSlopeGeometry implements IUnbakedGeometry<ModelShapeSlopeGeometry> {
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

        // Bottom
        builder.addUnculledFace(ModelShapeHelper.quad(
                sprite,
                ModelShapeHelper.vertex(0, 0, 0, 0, 0),
                ModelShapeHelper.vertex(1, 0, 0, 1, 0),
                ModelShapeHelper.vertex(1, 0, 1, 1, 1),
                ModelShapeHelper.vertex(0, 0, 1, 0, 1)
        ));

        // Vertical end
        builder.addUnculledFace(ModelShapeHelper.quad(
                sprite,
                ModelShapeHelper.vertex(1, 0, 0, 0, 0),
                ModelShapeHelper.vertex(1, 1, 0, 0, 1),
                ModelShapeHelper.vertex(1, 1, 1, 1, 1),
                ModelShapeHelper.vertex(1, 0, 1, 1, 0)
        ));

        // Sloped face
        builder.addUnculledFace(ModelShapeHelper.quad(
                sprite,
                ModelShapeHelper.vertex(0, 0, 0, 0, 0),
                ModelShapeHelper.vertex(0, 0, 1, 0, 1),
                ModelShapeHelper.vertex(1, 1, 1, 1, 1),
                ModelShapeHelper.vertex(1, 1, 0, 1, 0)
        ));

        // Triangle at z = 0
        builder.addUnculledFace(ModelShapeHelper.triangle(
                sprite,
                ModelShapeHelper.vertex(0, 0, 0, 0, 0),
                ModelShapeHelper.vertex(1, 1, 0, 1, 1),
                ModelShapeHelper.vertex(1, 0, 0, 1, 0)
        ));

        // Triangle at z = 1
        builder.addUnculledFace(ModelShapeHelper.triangle(
                sprite,
                ModelShapeHelper.vertex(0, 0, 1, 0, 0),
                ModelShapeHelper.vertex(1, 0, 1, 1, 0),
                ModelShapeHelper.vertex(1, 1, 1, 1, 1)
        ));

        return builder.build(context.getRenderType(modelLocation));
    }
}
