package net.centertain.ceac.material.shapes.models.round.cylinders;

import net.centertain.ceac.material.utility.ModelHelper;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class SphereGeometry implements IUnbakedGeometry<SphereGeometry> {
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
                ModelHelper.vertex(0.788675f, 0.211325f, 0.211325f, 0.211325f, 0.788675f),
                ModelHelper.vertex(0.644338f, 0.161498f, 0.161498f, 0.355663f, 0.838502f),
                ModelHelper.vertex(0.669251f, 0.330749f, 0.061014f, 0.330749f, 0.669251f),
                ModelHelper.vertex(0.838502f, 0.355662f, 0.161498f, 0.161499f, 0.644338f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.644338f, 0.161498f, 0.161498f, 0.355663f, 0.838502f),
                ModelHelper.vertex(0.5f, 0.146447f, 0.146447f, 0.5f, 0.853553f),
                ModelHelper.vertex(0.5f, 0.323223f, 0.032293f, 0.5f, 0.676777f),
                ModelHelper.vertex(0.669251f, 0.330749f, 0.061014f, 0.330749f, 0.669251f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.146447f, 0.146447f, 0.5f, 0.853553f),
                ModelHelper.vertex(0.355662f, 0.161498f, 0.161498f, 0.644338f, 0.838502f),
                ModelHelper.vertex(0.330749f, 0.330749f, 0.061014f, 0.669251f, 0.669251f),
                ModelHelper.vertex(0.5f, 0.323223f, 0.032293f, 0.5f, 0.676777f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.355662f, 0.161498f, 0.161498f, 0.644338f, 0.838502f),
                ModelHelper.vertex(0.211325f, 0.211325f, 0.211325f, 0.788675f, 0.788675f),
                ModelHelper.vertex(0.161498f, 0.355662f, 0.161498f, 0.838502f, 0.644338f),
                ModelHelper.vertex(0.330749f, 0.330749f, 0.061014f, 0.669251f, 0.669251f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.838502f, 0.355662f, 0.161498f, 0.161499f, 0.644338f),
                ModelHelper.vertex(0.669251f, 0.330749f, 0.061014f, 0.330749f, 0.669251f),
                ModelHelper.vertex(0.676777f, 0.5f, 0.032293f, 0.323223f, 0.5f),
                ModelHelper.vertex(0.853553f, 0.5f, 0.146447f, 0.146447f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.669251f, 0.330749f, 0.061014f, 0.330749f, 0.669251f),
                ModelHelper.vertex(0.5f, 0.323223f, 0.032293f, 0.5f, 0.676777f),
                ModelHelper.vertex(0.5f, 0.5f, 0f, 0.5f, 0.5f),
                ModelHelper.vertex(0.676777f, 0.5f, 0.032293f, 0.323223f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.323223f, 0.032293f, 0.5f, 0.676777f),
                ModelHelper.vertex(0.330749f, 0.330749f, 0.061014f, 0.669251f, 0.669251f),
                ModelHelper.vertex(0.323223f, 0.5f, 0.032293f, 0.676777f, 0.5f),
                ModelHelper.vertex(0.5f, 0.5f, 0f, 0.5f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.330749f, 0.330749f, 0.061014f, 0.669251f, 0.669251f),
                ModelHelper.vertex(0.161498f, 0.355662f, 0.161498f, 0.838502f, 0.644338f),
                ModelHelper.vertex(0.146447f, 0.5f, 0.146447f, 0.853554f, 0.5f),
                ModelHelper.vertex(0.323223f, 0.5f, 0.032293f, 0.676777f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.853553f, 0.5f, 0.146447f, 0.146447f, 0.5f),
                ModelHelper.vertex(0.676777f, 0.5f, 0.032293f, 0.323223f, 0.5f),
                ModelHelper.vertex(0.669251f, 0.669251f, 0.061014f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.838502f, 0.644338f, 0.161498f, 0.161499f, 0.355663f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.676777f, 0.5f, 0.032293f, 0.323223f, 0.5f),
                ModelHelper.vertex(0.5f, 0.5f, 0f, 0.5f, 0.5f),
                ModelHelper.vertex(0.5f, 0.676777f, 0.032293f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.669251f, 0.669251f, 0.061014f, 0.330749f, 0.330749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.5f, 0f, 0.5f, 0.5f),
                ModelHelper.vertex(0.323223f, 0.5f, 0.032293f, 0.676777f, 0.5f),
                ModelHelper.vertex(0.330749f, 0.669251f, 0.061014f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.5f, 0.676777f, 0.032293f, 0.5f, 0.323223f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.323223f, 0.5f, 0.032293f, 0.676777f, 0.5f),
                ModelHelper.vertex(0.146447f, 0.5f, 0.146447f, 0.853554f, 0.5f),
                ModelHelper.vertex(0.161498f, 0.644338f, 0.161498f, 0.838502f, 0.355663f),
                ModelHelper.vertex(0.330749f, 0.669251f, 0.061014f, 0.669251f, 0.330749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.838502f, 0.644338f, 0.161498f, 0.161499f, 0.355663f),
                ModelHelper.vertex(0.669251f, 0.669251f, 0.061014f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.644338f, 0.838502f, 0.161498f, 0.355663f, 0.161498f),
                ModelHelper.vertex(0.788675f, 0.788675f, 0.211325f, 0.211325f, 0.211325f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.669251f, 0.669251f, 0.061014f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.5f, 0.676777f, 0.032293f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.5f, 0.853553f, 0.146447f, 0.5f, 0.146447f),
                ModelHelper.vertex(0.644338f, 0.838502f, 0.161498f, 0.355663f, 0.161498f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.676777f, 0.032293f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.330749f, 0.669251f, 0.061014f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.355662f, 0.838502f, 0.161498f, 0.644338f, 0.161498f),
                ModelHelper.vertex(0.5f, 0.853553f, 0.146447f, 0.5f, 0.146447f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.330749f, 0.669251f, 0.061014f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.161498f, 0.644338f, 0.161498f, 0.838502f, 0.355663f),
                ModelHelper.vertex(0.211325f, 0.788675f, 0.211325f, 0.788675f, 0.211325f),
                ModelHelper.vertex(0.355662f, 0.838502f, 0.161498f, 0.644338f, 0.161498f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.211325f, 0.211325f, 0.211325f, 0.211325f, 0.788675f),
                ModelHelper.vertex(0.161498f, 0.161498f, 0.355662f, 0.355662f, 0.838502f),
                ModelHelper.vertex(0.061014f, 0.330749f, 0.330749f, 0.330749f, 0.669251f),
                ModelHelper.vertex(0.161498f, 0.355662f, 0.161498f, 0.161498f, 0.644338f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.161498f, 0.161498f, 0.355662f, 0.355662f, 0.838502f),
                ModelHelper.vertex(0.146447f, 0.146447f, 0.5f, 0.5f, 0.853553f),
                ModelHelper.vertex(0.032293f, 0.323223f, 0.5f, 0.5f, 0.676777f),
                ModelHelper.vertex(0.061014f, 0.330749f, 0.330749f, 0.330749f, 0.669251f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.146447f, 0.146447f, 0.5f, 0.5f, 0.853553f),
                ModelHelper.vertex(0.161498f, 0.161498f, 0.644338f, 0.644337f, 0.838502f),
                ModelHelper.vertex(0.061014f, 0.330749f, 0.669251f, 0.669251f, 0.669251f),
                ModelHelper.vertex(0.032293f, 0.323223f, 0.5f, 0.5f, 0.676777f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.161498f, 0.161498f, 0.644338f, 0.644337f, 0.838502f),
                ModelHelper.vertex(0.211325f, 0.211325f, 0.788675f, 0.788675f, 0.788675f),
                ModelHelper.vertex(0.161498f, 0.355662f, 0.838502f, 0.838502f, 0.644338f),
                ModelHelper.vertex(0.061014f, 0.330749f, 0.669251f, 0.669251f, 0.669251f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.161498f, 0.355662f, 0.161498f, 0.161498f, 0.644338f),
                ModelHelper.vertex(0.061014f, 0.330749f, 0.330749f, 0.330749f, 0.669251f),
                ModelHelper.vertex(0.032293f, 0.5f, 0.323223f, 0.323223f, 0.5f),
                ModelHelper.vertex(0.146447f, 0.5f, 0.146447f, 0.146447f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.061014f, 0.330749f, 0.330749f, 0.330749f, 0.669251f),
                ModelHelper.vertex(0.032293f, 0.323223f, 0.5f, 0.5f, 0.676777f),
                ModelHelper.vertex(0f, 0.5f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.032293f, 0.5f, 0.323223f, 0.323223f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.032293f, 0.323223f, 0.5f, 0.5f, 0.676777f),
                ModelHelper.vertex(0.061014f, 0.330749f, 0.669251f, 0.669251f, 0.669251f),
                ModelHelper.vertex(0.032293f, 0.5f, 0.676777f, 0.676777f, 0.5f),
                ModelHelper.vertex(0f, 0.5f, 0.5f, 0.5f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.061014f, 0.330749f, 0.669251f, 0.669251f, 0.669251f),
                ModelHelper.vertex(0.161498f, 0.355662f, 0.838502f, 0.838502f, 0.644338f),
                ModelHelper.vertex(0.146447f, 0.5f, 0.853553f, 0.853553f, 0.5f),
                ModelHelper.vertex(0.032293f, 0.5f, 0.676777f, 0.676777f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.146447f, 0.5f, 0.146447f, 0.146447f, 0.5f),
                ModelHelper.vertex(0.032293f, 0.5f, 0.323223f, 0.323223f, 0.5f),
                ModelHelper.vertex(0.061014f, 0.669251f, 0.330749f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.161498f, 0.644338f, 0.161498f, 0.161498f, 0.355663f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.032293f, 0.5f, 0.323223f, 0.323223f, 0.5f),
                ModelHelper.vertex(0f, 0.5f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.032293f, 0.676777f, 0.5f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.061014f, 0.669251f, 0.330749f, 0.330749f, 0.330749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0f, 0.5f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.032293f, 0.5f, 0.676777f, 0.676777f, 0.5f),
                ModelHelper.vertex(0.061014f, 0.669251f, 0.669251f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.032293f, 0.676777f, 0.5f, 0.5f, 0.323223f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.032293f, 0.5f, 0.676777f, 0.676777f, 0.5f),
                ModelHelper.vertex(0.146447f, 0.5f, 0.853553f, 0.853553f, 0.5f),
                ModelHelper.vertex(0.161498f, 0.644338f, 0.838502f, 0.838502f, 0.355663f),
                ModelHelper.vertex(0.061014f, 0.669251f, 0.669251f, 0.669251f, 0.330749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.161498f, 0.644338f, 0.161498f, 0.161498f, 0.355663f),
                ModelHelper.vertex(0.061014f, 0.669251f, 0.330749f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.161498f, 0.838502f, 0.355662f, 0.355662f, 0.161498f),
                ModelHelper.vertex(0.211325f, 0.788675f, 0.211325f, 0.211325f, 0.211325f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.061014f, 0.669251f, 0.330749f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.032293f, 0.676777f, 0.5f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.146447f, 0.853553f, 0.5f, 0.5f, 0.146447f),
                ModelHelper.vertex(0.161498f, 0.838502f, 0.355662f, 0.355662f, 0.161498f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.032293f, 0.676777f, 0.5f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.061014f, 0.669251f, 0.669251f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.161498f, 0.838502f, 0.644338f, 0.644337f, 0.161498f),
                ModelHelper.vertex(0.146447f, 0.853553f, 0.5f, 0.5f, 0.146447f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.061014f, 0.669251f, 0.669251f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.161498f, 0.644338f, 0.838502f, 0.838502f, 0.355663f),
                ModelHelper.vertex(0.211325f, 0.788675f, 0.788675f, 0.788675f, 0.211325f),
                ModelHelper.vertex(0.161498f, 0.838502f, 0.644338f, 0.644337f, 0.161498f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.211325f, 0.211325f, 0.788675f, 0.211325f, 0.788675f),
                ModelHelper.vertex(0.355662f, 0.161498f, 0.838502f, 0.355662f, 0.838502f),
                ModelHelper.vertex(0.330749f, 0.330749f, 0.938986f, 0.330749f, 0.669251f),
                ModelHelper.vertex(0.161498f, 0.355662f, 0.838502f, 0.161498f, 0.644338f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.355662f, 0.161498f, 0.838502f, 0.355662f, 0.838502f),
                ModelHelper.vertex(0.5f, 0.146447f, 0.853553f, 0.5f, 0.853553f),
                ModelHelper.vertex(0.5f, 0.323223f, 0.967707f, 0.5f, 0.676777f),
                ModelHelper.vertex(0.330749f, 0.330749f, 0.938986f, 0.330749f, 0.669251f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.146447f, 0.853553f, 0.5f, 0.853553f),
                ModelHelper.vertex(0.644338f, 0.161498f, 0.838502f, 0.644338f, 0.838502f),
                ModelHelper.vertex(0.669251f, 0.330749f, 0.938986f, 0.669251f, 0.669251f),
                ModelHelper.vertex(0.5f, 0.323223f, 0.967707f, 0.5f, 0.676777f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.644338f, 0.161498f, 0.838502f, 0.644338f, 0.838502f),
                ModelHelper.vertex(0.788675f, 0.211325f, 0.788675f, 0.788675f, 0.788675f),
                ModelHelper.vertex(0.838502f, 0.355662f, 0.838502f, 0.838502f, 0.644338f),
                ModelHelper.vertex(0.669251f, 0.330749f, 0.938986f, 0.669251f, 0.669251f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.161498f, 0.355662f, 0.838502f, 0.161498f, 0.644338f),
                ModelHelper.vertex(0.330749f, 0.330749f, 0.938986f, 0.330749f, 0.669251f),
                ModelHelper.vertex(0.323223f, 0.5f, 0.967707f, 0.323223f, 0.5f),
                ModelHelper.vertex(0.146447f, 0.5f, 0.853553f, 0.146447f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.330749f, 0.330749f, 0.938986f, 0.330749f, 0.669251f),
                ModelHelper.vertex(0.5f, 0.323223f, 0.967707f, 0.5f, 0.676777f),
                ModelHelper.vertex(0.5f, 0.5f, 1f, 0.5f, 0.5f),
                ModelHelper.vertex(0.323223f, 0.5f, 0.967707f, 0.323223f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.323223f, 0.967707f, 0.5f, 0.676777f),
                ModelHelper.vertex(0.669251f, 0.330749f, 0.938986f, 0.669251f, 0.669251f),
                ModelHelper.vertex(0.676777f, 0.5f, 0.967707f, 0.676777f, 0.5f),
                ModelHelper.vertex(0.5f, 0.5f, 1f, 0.5f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.669251f, 0.330749f, 0.938986f, 0.669251f, 0.669251f),
                ModelHelper.vertex(0.838502f, 0.355662f, 0.838502f, 0.838502f, 0.644338f),
                ModelHelper.vertex(0.853553f, 0.5f, 0.853553f, 0.853553f, 0.5f),
                ModelHelper.vertex(0.676777f, 0.5f, 0.967707f, 0.676777f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.146447f, 0.5f, 0.853553f, 0.146447f, 0.5f),
                ModelHelper.vertex(0.323223f, 0.5f, 0.967707f, 0.323223f, 0.5f),
                ModelHelper.vertex(0.330749f, 0.669251f, 0.938986f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.161498f, 0.644338f, 0.838502f, 0.161498f, 0.355662f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.323223f, 0.5f, 0.967707f, 0.323223f, 0.5f),
                ModelHelper.vertex(0.5f, 0.5f, 1f, 0.5f, 0.5f),
                ModelHelper.vertex(0.5f, 0.676777f, 0.967707f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.330749f, 0.669251f, 0.938986f, 0.330749f, 0.330749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.5f, 1f, 0.5f, 0.5f),
                ModelHelper.vertex(0.676777f, 0.5f, 0.967707f, 0.676777f, 0.5f),
                ModelHelper.vertex(0.669251f, 0.669251f, 0.938986f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.5f, 0.676777f, 0.967707f, 0.5f, 0.323223f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.676777f, 0.5f, 0.967707f, 0.676777f, 0.5f),
                ModelHelper.vertex(0.853553f, 0.5f, 0.853553f, 0.853553f, 0.5f),
                ModelHelper.vertex(0.838502f, 0.644338f, 0.838502f, 0.838502f, 0.355662f),
                ModelHelper.vertex(0.669251f, 0.669251f, 0.938986f, 0.669251f, 0.330749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.161498f, 0.644338f, 0.838502f, 0.161498f, 0.355662f),
                ModelHelper.vertex(0.330749f, 0.669251f, 0.938986f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.355662f, 0.838502f, 0.838502f, 0.355662f, 0.161498f),
                ModelHelper.vertex(0.211325f, 0.788675f, 0.788675f, 0.211325f, 0.211325f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.330749f, 0.669251f, 0.938986f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.5f, 0.676777f, 0.967707f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.5f, 0.853553f, 0.853553f, 0.5f, 0.146447f),
                ModelHelper.vertex(0.355662f, 0.838502f, 0.838502f, 0.355662f, 0.161498f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.676777f, 0.967707f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.669251f, 0.669251f, 0.938986f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.644338f, 0.838502f, 0.838502f, 0.644338f, 0.161498f),
                ModelHelper.vertex(0.5f, 0.853553f, 0.853553f, 0.5f, 0.146447f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.669251f, 0.669251f, 0.938986f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.838502f, 0.644338f, 0.838502f, 0.838502f, 0.355662f),
                ModelHelper.vertex(0.788675f, 0.788675f, 0.788675f, 0.788675f, 0.211325f),
                ModelHelper.vertex(0.644338f, 0.838502f, 0.838502f, 0.644338f, 0.161498f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.788675f, 0.211325f, 0.788675f, 0.211325f, 0.788675f),
                ModelHelper.vertex(0.838502f, 0.161498f, 0.644338f, 0.355662f, 0.838502f),
                ModelHelper.vertex(0.938986f, 0.330749f, 0.669251f, 0.330749f, 0.669251f),
                ModelHelper.vertex(0.838502f, 0.355662f, 0.838502f, 0.161498f, 0.644338f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.838502f, 0.161498f, 0.644338f, 0.355662f, 0.838502f),
                ModelHelper.vertex(0.853553f, 0.146447f, 0.5f, 0.5f, 0.853553f),
                ModelHelper.vertex(0.967707f, 0.323223f, 0.5f, 0.5f, 0.676777f),
                ModelHelper.vertex(0.938986f, 0.330749f, 0.669251f, 0.330749f, 0.669251f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.853553f, 0.146447f, 0.5f, 0.5f, 0.853553f),
                ModelHelper.vertex(0.838502f, 0.161498f, 0.355662f, 0.644338f, 0.838502f),
                ModelHelper.vertex(0.938986f, 0.330749f, 0.330749f, 0.669251f, 0.669251f),
                ModelHelper.vertex(0.967707f, 0.323223f, 0.5f, 0.5f, 0.676777f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.838502f, 0.161498f, 0.355662f, 0.644338f, 0.838502f),
                ModelHelper.vertex(0.788675f, 0.211325f, 0.211325f, 0.788675f, 0.788675f),
                ModelHelper.vertex(0.838502f, 0.355662f, 0.161498f, 0.838502f, 0.644338f),
                ModelHelper.vertex(0.938986f, 0.330749f, 0.330749f, 0.669251f, 0.669251f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.838502f, 0.355662f, 0.838502f, 0.161498f, 0.644338f),
                ModelHelper.vertex(0.938986f, 0.330749f, 0.669251f, 0.330749f, 0.669251f),
                ModelHelper.vertex(0.967707f, 0.5f, 0.676777f, 0.323223f, 0.5f),
                ModelHelper.vertex(0.853553f, 0.5f, 0.853553f, 0.146447f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.938986f, 0.330749f, 0.669251f, 0.330749f, 0.669251f),
                ModelHelper.vertex(0.967707f, 0.323223f, 0.5f, 0.5f, 0.676777f),
                ModelHelper.vertex(1f, 0.5f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.967707f, 0.5f, 0.676777f, 0.323223f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.967707f, 0.323223f, 0.5f, 0.5f, 0.676777f),
                ModelHelper.vertex(0.938986f, 0.330749f, 0.330749f, 0.669251f, 0.669251f),
                ModelHelper.vertex(0.967707f, 0.5f, 0.323223f, 0.676777f, 0.5f),
                ModelHelper.vertex(1f, 0.5f, 0.5f, 0.5f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.938986f, 0.330749f, 0.330749f, 0.669251f, 0.669251f),
                ModelHelper.vertex(0.838502f, 0.355662f, 0.161498f, 0.838502f, 0.644338f),
                ModelHelper.vertex(0.853553f, 0.5f, 0.146447f, 0.853553f, 0.5f),
                ModelHelper.vertex(0.967707f, 0.5f, 0.323223f, 0.676777f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.853553f, 0.5f, 0.853553f, 0.146447f, 0.5f),
                ModelHelper.vertex(0.967707f, 0.5f, 0.676777f, 0.323223f, 0.5f),
                ModelHelper.vertex(0.938986f, 0.669251f, 0.669251f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.838502f, 0.644338f, 0.838502f, 0.161498f, 0.355662f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.967707f, 0.5f, 0.676777f, 0.323223f, 0.5f),
                ModelHelper.vertex(1f, 0.5f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.967707f, 0.676777f, 0.5f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.938986f, 0.669251f, 0.669251f, 0.330749f, 0.330749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(1f, 0.5f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.967707f, 0.5f, 0.323223f, 0.676777f, 0.5f),
                ModelHelper.vertex(0.938986f, 0.669251f, 0.330749f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.967707f, 0.676777f, 0.5f, 0.5f, 0.323223f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.967707f, 0.5f, 0.323223f, 0.676777f, 0.5f),
                ModelHelper.vertex(0.853553f, 0.5f, 0.146447f, 0.853553f, 0.5f),
                ModelHelper.vertex(0.838502f, 0.644338f, 0.161498f, 0.838502f, 0.355662f),
                ModelHelper.vertex(0.938986f, 0.669251f, 0.330749f, 0.669251f, 0.330749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.838502f, 0.644338f, 0.838502f, 0.161498f, 0.355662f),
                ModelHelper.vertex(0.938986f, 0.669251f, 0.669251f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.838502f, 0.838502f, 0.644338f, 0.355662f, 0.161498f),
                ModelHelper.vertex(0.788675f, 0.788675f, 0.788675f, 0.211325f, 0.211325f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.938986f, 0.669251f, 0.669251f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.967707f, 0.676777f, 0.5f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.853553f, 0.853553f, 0.5f, 0.5f, 0.146447f),
                ModelHelper.vertex(0.838502f, 0.838502f, 0.644338f, 0.355662f, 0.161498f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.967707f, 0.676777f, 0.5f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.938986f, 0.669251f, 0.330749f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.838502f, 0.838502f, 0.355662f, 0.644338f, 0.161498f),
                ModelHelper.vertex(0.853553f, 0.853553f, 0.5f, 0.5f, 0.146447f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.938986f, 0.669251f, 0.330749f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.838502f, 0.644338f, 0.161498f, 0.838502f, 0.355662f),
                ModelHelper.vertex(0.788675f, 0.788675f, 0.211325f, 0.788675f, 0.211325f),
                ModelHelper.vertex(0.838502f, 0.838502f, 0.355662f, 0.644338f, 0.161498f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.788675f, 0.211325f, 0.788675f, 0.788675f, 0.211324f),
                ModelHelper.vertex(0.644338f, 0.161498f, 0.838502f, 0.644338f, 0.161498f),
                ModelHelper.vertex(0.669251f, 0.061014f, 0.669251f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.838502f, 0.161498f, 0.644338f, 0.838502f, 0.355662f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.644338f, 0.161498f, 0.838502f, 0.644338f, 0.161498f),
                ModelHelper.vertex(0.5f, 0.146447f, 0.853553f, 0.5f, 0.146446f),
                ModelHelper.vertex(0.5f, 0.032293f, 0.676777f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.669251f, 0.061014f, 0.669251f, 0.669251f, 0.330749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.146447f, 0.853553f, 0.5f, 0.146446f),
                ModelHelper.vertex(0.355662f, 0.161498f, 0.838502f, 0.355662f, 0.161498f),
                ModelHelper.vertex(0.330749f, 0.061014f, 0.669251f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.5f, 0.032293f, 0.676777f, 0.5f, 0.323223f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.355662f, 0.161498f, 0.838502f, 0.355662f, 0.161498f),
                ModelHelper.vertex(0.211325f, 0.211325f, 0.788675f, 0.211325f, 0.211324f),
                ModelHelper.vertex(0.161498f, 0.161498f, 0.644338f, 0.161498f, 0.355662f),
                ModelHelper.vertex(0.330749f, 0.061014f, 0.669251f, 0.330749f, 0.330749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.838502f, 0.161498f, 0.644338f, 0.838502f, 0.355662f),
                ModelHelper.vertex(0.669251f, 0.061014f, 0.669251f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.676777f, 0.032293f, 0.5f, 0.676777f, 0.5f),
                ModelHelper.vertex(0.853553f, 0.146447f, 0.5f, 0.853553f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.669251f, 0.061014f, 0.669251f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.5f, 0.032293f, 0.676777f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.676777f, 0.032293f, 0.5f, 0.676777f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.032293f, 0.676777f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.330749f, 0.061014f, 0.669251f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.323223f, 0.032293f, 0.5f, 0.323223f, 0.499999f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.330749f, 0.061014f, 0.669251f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.161498f, 0.161498f, 0.644338f, 0.161498f, 0.355662f),
                ModelHelper.vertex(0.146447f, 0.146447f, 0.5f, 0.146447f, 0.499999f),
                ModelHelper.vertex(0.323223f, 0.032293f, 0.5f, 0.323223f, 0.499999f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.853553f, 0.146447f, 0.5f, 0.853553f, 0.5f),
                ModelHelper.vertex(0.676777f, 0.032293f, 0.5f, 0.676777f, 0.5f),
                ModelHelper.vertex(0.669251f, 0.061014f, 0.330749f, 0.669251f, 0.66925f),
                ModelHelper.vertex(0.838502f, 0.161498f, 0.355662f, 0.838501f, 0.644337f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.676777f, 0.032293f, 0.5f, 0.676777f, 0.5f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.5f, 0.032293f, 0.323223f, 0.5f, 0.676776f),
                ModelHelper.vertex(0.669251f, 0.061014f, 0.330749f, 0.669251f, 0.66925f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.323223f, 0.032293f, 0.5f, 0.323223f, 0.499999f),
                ModelHelper.vertex(0.330749f, 0.061014f, 0.330749f, 0.330749f, 0.66925f),
                ModelHelper.vertex(0.5f, 0.032293f, 0.323223f, 0.5f, 0.676776f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.323223f, 0.032293f, 0.5f, 0.323223f, 0.499999f),
                ModelHelper.vertex(0.146447f, 0.146447f, 0.5f, 0.146447f, 0.499999f),
                ModelHelper.vertex(0.161498f, 0.161498f, 0.355662f, 0.161498f, 0.644337f),
                ModelHelper.vertex(0.330749f, 0.061014f, 0.330749f, 0.330749f, 0.66925f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.838502f, 0.161498f, 0.355662f, 0.838501f, 0.644337f),
                ModelHelper.vertex(0.669251f, 0.061014f, 0.330749f, 0.669251f, 0.66925f),
                ModelHelper.vertex(0.644338f, 0.161498f, 0.161498f, 0.644337f, 0.838501f),
                ModelHelper.vertex(0.788675f, 0.211325f, 0.211325f, 0.788675f, 0.788675f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.669251f, 0.061014f, 0.330749f, 0.669251f, 0.66925f),
                ModelHelper.vertex(0.5f, 0.032293f, 0.323223f, 0.5f, 0.676776f),
                ModelHelper.vertex(0.5f, 0.146447f, 0.146447f, 0.5f, 0.853553f),
                ModelHelper.vertex(0.644338f, 0.161498f, 0.161498f, 0.644337f, 0.838501f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.032293f, 0.323223f, 0.5f, 0.676776f),
                ModelHelper.vertex(0.330749f, 0.061014f, 0.330749f, 0.330749f, 0.66925f),
                ModelHelper.vertex(0.355662f, 0.161498f, 0.161498f, 0.355662f, 0.838501f),
                ModelHelper.vertex(0.5f, 0.146447f, 0.146447f, 0.5f, 0.853553f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.330749f, 0.061014f, 0.330749f, 0.330749f, 0.66925f),
                ModelHelper.vertex(0.161498f, 0.161498f, 0.355662f, 0.161498f, 0.644337f),
                ModelHelper.vertex(0.211325f, 0.211325f, 0.211325f, 0.211325f, 0.788674f),
                ModelHelper.vertex(0.355662f, 0.161498f, 0.161498f, 0.355662f, 0.838501f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.788675f, 0.788675f, 0.211325f, 0.788675f, 0.211325f),
                ModelHelper.vertex(0.644338f, 0.838502f, 0.161498f, 0.644338f, 0.161499f),
                ModelHelper.vertex(0.669251f, 0.938986f, 0.330749f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.838502f, 0.838502f, 0.355662f, 0.838502f, 0.355663f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.644338f, 0.838502f, 0.161498f, 0.644338f, 0.161499f),
                ModelHelper.vertex(0.5f, 0.853553f, 0.146447f, 0.5f, 0.146447f),
                ModelHelper.vertex(0.5f, 0.967707f, 0.323223f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.669251f, 0.938986f, 0.330749f, 0.669251f, 0.330749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.853553f, 0.146447f, 0.5f, 0.146447f),
                ModelHelper.vertex(0.355662f, 0.838502f, 0.161498f, 0.355663f, 0.161498f),
                ModelHelper.vertex(0.330749f, 0.938986f, 0.330749f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.5f, 0.967707f, 0.323223f, 0.5f, 0.323223f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.355662f, 0.838502f, 0.161498f, 0.355663f, 0.161498f),
                ModelHelper.vertex(0.211325f, 0.788675f, 0.211325f, 0.211325f, 0.211325f),
                ModelHelper.vertex(0.161498f, 0.838502f, 0.355662f, 0.161498f, 0.355662f),
                ModelHelper.vertex(0.330749f, 0.938986f, 0.330749f, 0.330749f, 0.330749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.838502f, 0.838502f, 0.355662f, 0.838502f, 0.355663f),
                ModelHelper.vertex(0.669251f, 0.938986f, 0.330749f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.676777f, 0.967707f, 0.5f, 0.676777f, 0.5f),
                ModelHelper.vertex(0.853553f, 0.853553f, 0.5f, 0.853553f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.669251f, 0.938986f, 0.330749f, 0.669251f, 0.330749f),
                ModelHelper.vertex(0.5f, 0.967707f, 0.323223f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.676777f, 0.967707f, 0.5f, 0.676777f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.967707f, 0.323223f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.330749f, 0.938986f, 0.330749f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.323223f, 0.967707f, 0.5f, 0.323223f, 0.5f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.330749f, 0.938986f, 0.330749f, 0.330749f, 0.330749f),
                ModelHelper.vertex(0.161498f, 0.838502f, 0.355662f, 0.161498f, 0.355662f),
                ModelHelper.vertex(0.146447f, 0.853553f, 0.5f, 0.146447f, 0.5f),
                ModelHelper.vertex(0.323223f, 0.967707f, 0.5f, 0.323223f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.853553f, 0.853553f, 0.5f, 0.853553f, 0.5f),
                ModelHelper.vertex(0.676777f, 0.967707f, 0.5f, 0.676777f, 0.5f),
                ModelHelper.vertex(0.669251f, 0.938986f, 0.669251f, 0.669251f, 0.669251f),
                ModelHelper.vertex(0.838502f, 0.838502f, 0.644338f, 0.838501f, 0.644338f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.676777f, 0.967707f, 0.5f, 0.676777f, 0.5f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.5f, 0.967707f, 0.676777f, 0.5f, 0.676777f),
                ModelHelper.vertex(0.669251f, 0.938986f, 0.669251f, 0.669251f, 0.669251f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.323223f, 0.967707f, 0.5f, 0.323223f, 0.5f),
                ModelHelper.vertex(0.330749f, 0.938986f, 0.669251f, 0.330749f, 0.669251f),
                ModelHelper.vertex(0.5f, 0.967707f, 0.676777f, 0.5f, 0.676777f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.323223f, 0.967707f, 0.5f, 0.323223f, 0.5f),
                ModelHelper.vertex(0.146447f, 0.853553f, 0.5f, 0.146447f, 0.5f),
                ModelHelper.vertex(0.161498f, 0.838502f, 0.644338f, 0.161498f, 0.644337f),
                ModelHelper.vertex(0.330749f, 0.938986f, 0.669251f, 0.330749f, 0.669251f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.838502f, 0.838502f, 0.644338f, 0.838501f, 0.644338f),
                ModelHelper.vertex(0.669251f, 0.938986f, 0.669251f, 0.669251f, 0.669251f),
                ModelHelper.vertex(0.644338f, 0.838502f, 0.838502f, 0.644337f, 0.838502f),
                ModelHelper.vertex(0.788675f, 0.788675f, 0.788675f, 0.788675f, 0.788675f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.669251f, 0.938986f, 0.669251f, 0.669251f, 0.669251f),
                ModelHelper.vertex(0.5f, 0.967707f, 0.676777f, 0.5f, 0.676777f),
                ModelHelper.vertex(0.5f, 0.853553f, 0.853553f, 0.5f, 0.853553f),
                ModelHelper.vertex(0.644338f, 0.838502f, 0.838502f, 0.644337f, 0.838502f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.967707f, 0.676777f, 0.5f, 0.676777f),
                ModelHelper.vertex(0.330749f, 0.938986f, 0.669251f, 0.330749f, 0.669251f),
                ModelHelper.vertex(0.355662f, 0.838502f, 0.838502f, 0.355662f, 0.838502f),
                ModelHelper.vertex(0.5f, 0.853553f, 0.853553f, 0.5f, 0.853553f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.330749f, 0.938986f, 0.669251f, 0.330749f, 0.669251f),
                ModelHelper.vertex(0.161498f, 0.838502f, 0.644338f, 0.161498f, 0.644337f),
                ModelHelper.vertex(0.211325f, 0.788675f, 0.788675f, 0.211325f, 0.788675f),
                ModelHelper.vertex(0.355662f, 0.838502f, 0.838502f, 0.355662f, 0.838502f)
        ));

        COLLISION_SHAPE = builder.build(context.getRenderType(modelLocation));
        return COLLISION_SHAPE;
    }
}
