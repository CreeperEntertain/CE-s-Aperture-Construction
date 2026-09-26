package net.centertain.ceac.material.shapes.models;

import net.centertain.ceac.material.utility.ModelHelper;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class MaterialShapeHalfSlopeBottomGeometry implements IUnbakedGeometry<MaterialShapeHalfSlopeBottomGeometry> {
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
        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.0f, 0.0f, 0.0f, 0.0f, 1.0f),
                ModelHelper.vertex(1.0f, 0.0f, 0.0f, 1.0f, 1.0f),
                ModelHelper.vertex(1.0f, 0.0f, 1.0f, 1.0f, 0.0f),
                ModelHelper.vertex(0.0f, 0.0f, 1.0f, 0.0f, 0.0f)
        ));

        // Vertical end
        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1.0f, 0.0f, 0.0f, 1.0f, 1.0f),
                ModelHelper.vertex(1.0f, 0.5f, 0.0f, 1.0f, 0.0f),
                ModelHelper.vertex(1.0f, 0.5f, 1.0f, 0.0f, 0.0f),
                ModelHelper.vertex(1.0f, 0.0f, 1.0f, 0.0f, 1.0f)
        ));

        // Sloped face
        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.0f, 0.0f, 0.0f, 0.0f, 1.0f),
                ModelHelper.vertex(0.0f, 0.0f, 1.0f, 1.0f, 1.0f),
                ModelHelper.vertex(1.0f, 0.5f, 1.0f, 1.0f, 0.0f),
                ModelHelper.vertex(1.0f, 0.5f, 0.0f, 0.0f, 0.0f)
        ));

        // Triangle at z = 0
        builder.addUnculledFace(ModelHelper.triangle(
                sprite,
                ModelHelper.vertex(0.0f, 0.0f, 0.0f, 1.0f, 1.0f),
                ModelHelper.vertex(1.0f, 0.5f, 0.0f, 0.0f, 0.0f),
                ModelHelper.vertex(1.0f, 0.0f, 0.0f, 0.0f, 1.0f)
        ));

        // Triangle at z = 1
        builder.addUnculledFace(ModelHelper.triangle(
                sprite,
                ModelHelper.vertex(0.0f, 0.0f, 1.0f, 0.0f, 1.0f),
                ModelHelper.vertex(1.0f, 0.0f, 1.0f, 1.0f, 1.0f),
                ModelHelper.vertex(1.0f, 0.5f, 1.0f, 1.0f, 0.0f)
        ));

        return builder.build(context.getRenderType(modelLocation));
    }
}
