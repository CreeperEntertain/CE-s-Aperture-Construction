package net.centertain.ceac.material.shapes.models.round.thin_cylinders;

import net.centertain.ceac.material.utility.ModelHelper;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class ThinCylinderEndGeometry implements IUnbakedGeometry<ThinCylinderEndGeometry> {
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
                ModelHelper.vertex(0.676777f, 0.75f, 0.323223f, 0.323224f, 0.25f),
                ModelHelper.vertex(0.595671f, 0.75f, 0.26903f, 0.404329f, 0.25f),
                ModelHelper.vertex(0.584625f, 0.834625f, 0.280507f, 0.415375f, 0.165375f),
                ModelHelper.vertex(0.669251f, 0.822169f, 0.330749f, 0.33075f, 0.177831f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.595671f, 0.75f, 0.26903f, 0.404329f, 0.25f),
                ModelHelper.vertex(0.5f, 0.75f, 0.25f, 0.5f, 0.25f),
                ModelHelper.vertex(0.5f, 0.838388f, 0.266146f, 0.5f, 0.161612f),
                ModelHelper.vertex(0.584625f, 0.834625f, 0.280507f, 0.415375f, 0.165375f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.75f, 0.25f, 0.5f, 0.25f),
                ModelHelper.vertex(0.404329f, 0.75f, 0.26903f, 0.595671f, 0.25f),
                ModelHelper.vertex(0.415375f, 0.834625f, 0.280507f, 0.584626f, 0.165375f),
                ModelHelper.vertex(0.5f, 0.838388f, 0.266146f, 0.5f, 0.161612f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.404329f, 0.75f, 0.26903f, 0.595671f, 0.25f),
                ModelHelper.vertex(0.323223f, 0.75f, 0.323223f, 0.676777f, 0.25f),
                ModelHelper.vertex(0.330749f, 0.822169f, 0.330749f, 0.669251f, 0.177831f),
                ModelHelper.vertex(0.415375f, 0.834625f, 0.280507f, 0.584626f, 0.165375f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.669251f, 0.822169f, 0.330749f, 0.33075f, 0.177831f),
                ModelHelper.vertex(0.584625f, 0.834625f, 0.280507f, 0.415375f, 0.165375f),
                ModelHelper.vertex(0.572169f, 0.919251f, 0.330749f, 0.427832f, 0.080749f),
                ModelHelper.vertex(0.644338f, 0.894338f, 0.355662f, 0.355663f, 0.105663f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.584625f, 0.834625f, 0.280507f, 0.415375f, 0.165375f),
                ModelHelper.vertex(0.5f, 0.838388f, 0.266146f, 0.5f, 0.161612f),
                ModelHelper.vertex(0.5f, 0.926777f, 0.323223f, 0.5f, 0.073223f),
                ModelHelper.vertex(0.572169f, 0.919251f, 0.330749f, 0.427832f, 0.080749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.838388f, 0.266146f, 0.5f, 0.161612f),
                ModelHelper.vertex(0.415375f, 0.834625f, 0.280507f, 0.584626f, 0.165375f),
                ModelHelper.vertex(0.427831f, 0.919251f, 0.330749f, 0.572169f, 0.080749f),
                ModelHelper.vertex(0.5f, 0.926777f, 0.323223f, 0.5f, 0.073223f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.415375f, 0.834625f, 0.280507f, 0.584626f, 0.165375f),
                ModelHelper.vertex(0.330749f, 0.822169f, 0.330749f, 0.669251f, 0.177831f),
                ModelHelper.vertex(0.355662f, 0.894338f, 0.355662f, 0.644338f, 0.105663f),
                ModelHelper.vertex(0.427831f, 0.919251f, 0.330749f, 0.572169f, 0.080749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.323223f, 0.75f, 0.323223f, 0.323223f, 0.25f),
                ModelHelper.vertex(0.26903f, 0.75f, 0.404329f, 0.404329f, 0.25f),
                ModelHelper.vertex(0.280507f, 0.834625f, 0.415375f, 0.415375f, 0.165375f),
                ModelHelper.vertex(0.330749f, 0.822169f, 0.330749f, 0.330749f, 0.177831f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.26903f, 0.75f, 0.404329f, 0.404329f, 0.25f),
                ModelHelper.vertex(0.25f, 0.75f, 0.5f, 0.5f, 0.25f),
                ModelHelper.vertex(0.266146f, 0.838388f, 0.5f, 0.5f, 0.161612f),
                ModelHelper.vertex(0.280507f, 0.834625f, 0.415375f, 0.415375f, 0.165375f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.25f, 0.75f, 0.5f, 0.5f, 0.25f),
                ModelHelper.vertex(0.26903f, 0.75f, 0.595671f, 0.595671f, 0.25f),
                ModelHelper.vertex(0.280507f, 0.834625f, 0.584625f, 0.584625f, 0.165375f),
                ModelHelper.vertex(0.266146f, 0.838388f, 0.5f, 0.5f, 0.161612f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.26903f, 0.75f, 0.595671f, 0.595671f, 0.25f),
                ModelHelper.vertex(0.323223f, 0.75f, 0.676777f, 0.676777f, 0.25f),
                ModelHelper.vertex(0.330749f, 0.822169f, 0.669251f, 0.669251f, 0.177831f),
                ModelHelper.vertex(0.280507f, 0.834625f, 0.584625f, 0.584625f, 0.165375f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.330749f, 0.822169f, 0.330749f, 0.330749f, 0.177831f),
                ModelHelper.vertex(0.280507f, 0.834625f, 0.415375f, 0.415375f, 0.165375f),
                ModelHelper.vertex(0.330749f, 0.919251f, 0.427831f, 0.427831f, 0.080749f),
                ModelHelper.vertex(0.355662f, 0.894338f, 0.355662f, 0.355662f, 0.105663f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.280507f, 0.834625f, 0.415375f, 0.415375f, 0.165375f),
                ModelHelper.vertex(0.266146f, 0.838388f, 0.5f, 0.5f, 0.161612f),
                ModelHelper.vertex(0.323223f, 0.926777f, 0.5f, 0.5f, 0.073223f),
                ModelHelper.vertex(0.330749f, 0.919251f, 0.427831f, 0.427831f, 0.080749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.266146f, 0.838388f, 0.5f, 0.5f, 0.161612f),
                ModelHelper.vertex(0.280507f, 0.834625f, 0.584625f, 0.584625f, 0.165375f),
                ModelHelper.vertex(0.330749f, 0.919251f, 0.572169f, 0.572169f, 0.080749f),
                ModelHelper.vertex(0.323223f, 0.926777f, 0.5f, 0.5f, 0.073223f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.280507f, 0.834625f, 0.584625f, 0.584625f, 0.165375f),
                ModelHelper.vertex(0.330749f, 0.822169f, 0.669251f, 0.669251f, 0.177831f),
                ModelHelper.vertex(0.355662f, 0.894338f, 0.644338f, 0.644337f, 0.105663f),
                ModelHelper.vertex(0.330749f, 0.919251f, 0.572169f, 0.572169f, 0.080749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.323223f, 0.75f, 0.676777f, 0.323223f, 0.25f),
                ModelHelper.vertex(0.404329f, 0.75f, 0.73097f, 0.404329f, 0.25f),
                ModelHelper.vertex(0.415375f, 0.834625f, 0.719493f, 0.415375f, 0.165375f),
                ModelHelper.vertex(0.330749f, 0.822169f, 0.669251f, 0.330749f, 0.177831f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.404329f, 0.75f, 0.73097f, 0.404329f, 0.25f),
                ModelHelper.vertex(0.5f, 0.75f, 0.75f, 0.5f, 0.25f),
                ModelHelper.vertex(0.5f, 0.838388f, 0.733854f, 0.5f, 0.161612f),
                ModelHelper.vertex(0.415375f, 0.834625f, 0.719493f, 0.415375f, 0.165375f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.75f, 0.75f, 0.5f, 0.25f),
                ModelHelper.vertex(0.595671f, 0.75f, 0.73097f, 0.595671f, 0.25f),
                ModelHelper.vertex(0.584625f, 0.834625f, 0.719493f, 0.584625f, 0.165375f),
                ModelHelper.vertex(0.5f, 0.838388f, 0.733854f, 0.5f, 0.161612f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.595671f, 0.75f, 0.73097f, 0.595671f, 0.25f),
                ModelHelper.vertex(0.676777f, 0.75f, 0.676777f, 0.676777f, 0.25f),
                ModelHelper.vertex(0.669251f, 0.822169f, 0.669251f, 0.669251f, 0.177831f),
                ModelHelper.vertex(0.584625f, 0.834625f, 0.719493f, 0.584625f, 0.165375f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.330749f, 0.822169f, 0.669251f, 0.330749f, 0.177831f),
                ModelHelper.vertex(0.415375f, 0.834625f, 0.719493f, 0.415375f, 0.165375f),
                ModelHelper.vertex(0.427831f, 0.919251f, 0.669251f, 0.427831f, 0.080749f),
                ModelHelper.vertex(0.355662f, 0.894338f, 0.644338f, 0.355662f, 0.105663f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.415375f, 0.834625f, 0.719493f, 0.415375f, 0.165375f),
                ModelHelper.vertex(0.5f, 0.838388f, 0.733854f, 0.5f, 0.161612f),
                ModelHelper.vertex(0.5f, 0.926777f, 0.676777f, 0.5f, 0.073223f),
                ModelHelper.vertex(0.427831f, 0.919251f, 0.669251f, 0.427831f, 0.080749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.838388f, 0.733854f, 0.5f, 0.161612f),
                ModelHelper.vertex(0.584625f, 0.834625f, 0.719493f, 0.584625f, 0.165375f),
                ModelHelper.vertex(0.572169f, 0.919251f, 0.669251f, 0.572169f, 0.080749f),
                ModelHelper.vertex(0.5f, 0.926777f, 0.676777f, 0.5f, 0.073223f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.584625f, 0.834625f, 0.719493f, 0.584625f, 0.165375f),
                ModelHelper.vertex(0.669251f, 0.822169f, 0.669251f, 0.669251f, 0.177831f),
                ModelHelper.vertex(0.644338f, 0.894338f, 0.644338f, 0.644338f, 0.105663f),
                ModelHelper.vertex(0.572169f, 0.919251f, 0.669251f, 0.572169f, 0.080749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.676777f, 0.75f, 0.676777f, 0.323223f, 0.25f),
                ModelHelper.vertex(0.73097f, 0.75f, 0.595671f, 0.404329f, 0.25f),
                ModelHelper.vertex(0.719493f, 0.834625f, 0.584625f, 0.415375f, 0.165375f),
                ModelHelper.vertex(0.669251f, 0.822169f, 0.669251f, 0.330749f, 0.177831f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.73097f, 0.75f, 0.595671f, 0.404329f, 0.25f),
                ModelHelper.vertex(0.75f, 0.75f, 0.5f, 0.5f, 0.25f),
                ModelHelper.vertex(0.733854f, 0.838388f, 0.5f, 0.5f, 0.161612f),
                ModelHelper.vertex(0.719493f, 0.834625f, 0.584625f, 0.415375f, 0.165375f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.75f, 0.75f, 0.5f, 0.5f, 0.25f),
                ModelHelper.vertex(0.73097f, 0.75f, 0.404329f, 0.595671f, 0.25f),
                ModelHelper.vertex(0.719493f, 0.834625f, 0.415375f, 0.584625f, 0.165375f),
                ModelHelper.vertex(0.733854f, 0.838388f, 0.5f, 0.5f, 0.161612f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.73097f, 0.75f, 0.404329f, 0.595671f, 0.25f),
                ModelHelper.vertex(0.676777f, 0.75f, 0.323223f, 0.676777f, 0.25f),
                ModelHelper.vertex(0.669251f, 0.822169f, 0.330749f, 0.669251f, 0.177831f),
                ModelHelper.vertex(0.719493f, 0.834625f, 0.415375f, 0.584625f, 0.165375f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.669251f, 0.822169f, 0.669251f, 0.330749f, 0.177831f),
                ModelHelper.vertex(0.719493f, 0.834625f, 0.584625f, 0.415375f, 0.165375f),
                ModelHelper.vertex(0.669251f, 0.919251f, 0.572169f, 0.427831f, 0.080749f),
                ModelHelper.vertex(0.644338f, 0.894338f, 0.644338f, 0.355663f, 0.105663f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.719493f, 0.834625f, 0.584625f, 0.415375f, 0.165375f),
                ModelHelper.vertex(0.733854f, 0.838388f, 0.5f, 0.5f, 0.161612f),
                ModelHelper.vertex(0.676777f, 0.926777f, 0.5f, 0.5f, 0.073223f),
                ModelHelper.vertex(0.669251f, 0.919251f, 0.572169f, 0.427831f, 0.080749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.733854f, 0.838388f, 0.5f, 0.5f, 0.161612f),
                ModelHelper.vertex(0.719493f, 0.834625f, 0.415375f, 0.584625f, 0.165375f),
                ModelHelper.vertex(0.669251f, 0.919251f, 0.427831f, 0.572169f, 0.080749f),
                ModelHelper.vertex(0.676777f, 0.926777f, 0.5f, 0.5f, 0.073223f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.719493f, 0.834625f, 0.415375f, 0.584625f, 0.165375f),
                ModelHelper.vertex(0.669251f, 0.822169f, 0.330749f, 0.669251f, 0.177831f),
                ModelHelper.vertex(0.644338f, 0.894338f, 0.355662f, 0.644338f, 0.105663f),
                ModelHelper.vertex(0.669251f, 0.919251f, 0.427831f, 0.572169f, 0.080749f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.644338f, 0.894338f, 0.355662f, 0.644338f, 0.355663f),
                ModelHelper.vertex(0.572169f, 0.919251f, 0.330749f, 0.572169f, 0.330749f),
                ModelHelper.vertex(0.584625f, 0.969493f, 0.415375f, 0.584625f, 0.415375f),
                ModelHelper.vertex(0.669251f, 0.919251f, 0.427831f, 0.669251f, 0.427831f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.572169f, 0.919251f, 0.330749f, 0.572169f, 0.330749f),
                ModelHelper.vertex(0.5f, 0.926777f, 0.323223f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.5f, 0.983854f, 0.411612f, 0.5f, 0.411612f),
                ModelHelper.vertex(0.584625f, 0.969493f, 0.415375f, 0.584625f, 0.415375f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.926777f, 0.323223f, 0.5f, 0.323223f),
                ModelHelper.vertex(0.427831f, 0.919251f, 0.330749f, 0.427831f, 0.330749f),
                ModelHelper.vertex(0.415375f, 0.969493f, 0.415375f, 0.415375f, 0.415375f),
                ModelHelper.vertex(0.5f, 0.983854f, 0.411612f, 0.5f, 0.411612f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.427831f, 0.919251f, 0.330749f, 0.427831f, 0.330749f),
                ModelHelper.vertex(0.355662f, 0.894338f, 0.355662f, 0.355662f, 0.355662f),
                ModelHelper.vertex(0.330749f, 0.919251f, 0.427831f, 0.330749f, 0.427831f),
                ModelHelper.vertex(0.415375f, 0.969493f, 0.415375f, 0.415375f, 0.415375f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.669251f, 0.919251f, 0.427831f, 0.669251f, 0.427831f),
                ModelHelper.vertex(0.584625f, 0.969493f, 0.415375f, 0.584625f, 0.415375f),
                ModelHelper.vertex(0.588388f, 0.983854f, 0.5f, 0.588388f, 0.5f),
                ModelHelper.vertex(0.676777f, 0.926777f, 0.5f, 0.676777f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.584625f, 0.969493f, 0.415375f, 0.584625f, 0.415375f),
                ModelHelper.vertex(0.5f, 0.983854f, 0.411612f, 0.5f, 0.411612f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.588388f, 0.983854f, 0.5f, 0.588388f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.983854f, 0.411612f, 0.5f, 0.411612f),
                ModelHelper.vertex(0.415375f, 0.969493f, 0.415375f, 0.415375f, 0.415375f),
                ModelHelper.vertex(0.411612f, 0.983854f, 0.5f, 0.411612f, 0.5f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.415375f, 0.969493f, 0.415375f, 0.415375f, 0.415375f),
                ModelHelper.vertex(0.330749f, 0.919251f, 0.427831f, 0.330749f, 0.427831f),
                ModelHelper.vertex(0.323223f, 0.926777f, 0.5f, 0.323223f, 0.5f),
                ModelHelper.vertex(0.411612f, 0.983854f, 0.5f, 0.411612f, 0.5f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.676777f, 0.926777f, 0.5f, 0.676777f, 0.5f),
                ModelHelper.vertex(0.588388f, 0.983854f, 0.5f, 0.588388f, 0.5f),
                ModelHelper.vertex(0.584625f, 0.969493f, 0.584625f, 0.584625f, 0.584625f),
                ModelHelper.vertex(0.669251f, 0.919251f, 0.572169f, 0.669251f, 0.572169f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.588388f, 0.983854f, 0.5f, 0.588388f, 0.5f),
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.5f, 0.983854f, 0.588388f, 0.5f, 0.588388f),
                ModelHelper.vertex(0.584625f, 0.969493f, 0.584625f, 0.584625f, 0.584625f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 1f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.411612f, 0.983854f, 0.5f, 0.411612f, 0.5f),
                ModelHelper.vertex(0.415375f, 0.969493f, 0.584625f, 0.415375f, 0.584625f),
                ModelHelper.vertex(0.5f, 0.983854f, 0.588388f, 0.5f, 0.588388f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.411612f, 0.983854f, 0.5f, 0.411612f, 0.5f),
                ModelHelper.vertex(0.323223f, 0.926777f, 0.5f, 0.323223f, 0.5f),
                ModelHelper.vertex(0.330749f, 0.919251f, 0.572169f, 0.330749f, 0.572169f),
                ModelHelper.vertex(0.415375f, 0.969493f, 0.584625f, 0.415375f, 0.584625f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.669251f, 0.919251f, 0.572169f, 0.669251f, 0.572169f),
                ModelHelper.vertex(0.584625f, 0.969493f, 0.584625f, 0.584625f, 0.584625f),
                ModelHelper.vertex(0.572169f, 0.919251f, 0.669251f, 0.572169f, 0.669251f),
                ModelHelper.vertex(0.644338f, 0.894338f, 0.644338f, 0.644337f, 0.644338f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.584625f, 0.969493f, 0.584625f, 0.584625f, 0.584625f),
                ModelHelper.vertex(0.5f, 0.983854f, 0.588388f, 0.5f, 0.588388f),
                ModelHelper.vertex(0.5f, 0.926777f, 0.676777f, 0.5f, 0.676777f),
                ModelHelper.vertex(0.572169f, 0.919251f, 0.669251f, 0.572169f, 0.669251f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0.983854f, 0.588388f, 0.5f, 0.588388f),
                ModelHelper.vertex(0.415375f, 0.969493f, 0.584625f, 0.415375f, 0.584625f),
                ModelHelper.vertex(0.427831f, 0.919251f, 0.669251f, 0.427831f, 0.669251f),
                ModelHelper.vertex(0.5f, 0.926777f, 0.676777f, 0.5f, 0.676777f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.415375f, 0.969493f, 0.584625f, 0.415375f, 0.584625f),
                ModelHelper.vertex(0.330749f, 0.919251f, 0.572169f, 0.330749f, 0.572169f),
                ModelHelper.vertex(0.355662f, 0.894338f, 0.644338f, 0.355662f, 0.644337f),
                ModelHelper.vertex(0.427831f, 0.919251f, 0.669251f, 0.427831f, 0.669251f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.404329f, 0f, 0.26903f, 0.595671f, 1f),
                ModelHelper.vertex(0.404329f, 0.75f, 0.26903f, 0.595671f, 0.25f),
                ModelHelper.vertex(0.5f, 0.75f, 0.25f, 0.5f, 0.25f),
                ModelHelper.vertex(0.5f, 0f, 0.25f, 0.5f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.323223f, 0f, 0.323223f, 0.676777f, 1f),
                ModelHelper.vertex(0.323223f, 0.75f, 0.323223f, 0.676777f, 0.25f),
                ModelHelper.vertex(0.404329f, 0.75f, 0.26903f, 0.595671f, 0.25f),
                ModelHelper.vertex(0.404329f, 0f, 0.26903f, 0.595671f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.26903f, 0f, 0.404329f, 0.404329f, 1f),
                ModelHelper.vertex(0.26903f, 0.75f, 0.404329f, 0.404329f, 0.25f),
                ModelHelper.vertex(0.323223f, 0.75f, 0.323223f, 0.323223f, 0.25f),
                ModelHelper.vertex(0.323223f, 0f, 0.323223f, 0.323223f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.25f, 0f, 0.5f, 0.5f, 1f),
                ModelHelper.vertex(0.25f, 0.75f, 0.5f, 0.5f, 0.25f),
                ModelHelper.vertex(0.26903f, 0.75f, 0.404329f, 0.404329f, 0.25f),
                ModelHelper.vertex(0.26903f, 0f, 0.404329f, 0.404329f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.26903f, 0f, 0.595671f, 0.595671f, 1f),
                ModelHelper.vertex(0.26903f, 0.75f, 0.595671f, 0.595671f, 0.25f),
                ModelHelper.vertex(0.25f, 0.75f, 0.5f, 0.5f, 0.25f),
                ModelHelper.vertex(0.25f, 0f, 0.5f, 0.5f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.323223f, 0f, 0.676777f, 0.676777f, 1f),
                ModelHelper.vertex(0.323223f, 0.75f, 0.676777f, 0.676777f, 0.25f),
                ModelHelper.vertex(0.26903f, 0.75f, 0.595671f, 0.595671f, 0.25f),
                ModelHelper.vertex(0.26903f, 0f, 0.595671f, 0.595671f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.404329f, 0f, 0.73097f, 0.404329f, 1f),
                ModelHelper.vertex(0.404329f, 0.75f, 0.73097f, 0.404329f, 0.25f),
                ModelHelper.vertex(0.323223f, 0.75f, 0.676777f, 0.323223f, 0.25f),
                ModelHelper.vertex(0.323223f, 0f, 0.676777f, 0.323223f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0f, 0.75f, 0.5f, 1f),
                ModelHelper.vertex(0.5f, 0.75f, 0.75f, 0.5f, 0.25f),
                ModelHelper.vertex(0.404329f, 0.75f, 0.73097f, 0.404329f, 0.25f),
                ModelHelper.vertex(0.404329f, 0f, 0.73097f, 0.404329f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.595671f, 0f, 0.73097f, 0.595671f, 1f),
                ModelHelper.vertex(0.595671f, 0.75f, 0.73097f, 0.595671f, 0.25f),
                ModelHelper.vertex(0.5f, 0.75f, 0.75f, 0.5f, 0.25f),
                ModelHelper.vertex(0.5f, 0f, 0.75f, 0.5f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.676777f, 0f, 0.676777f, 0.676777f, 1f),
                ModelHelper.vertex(0.676777f, 0.75f, 0.676777f, 0.676777f, 0.25f),
                ModelHelper.vertex(0.595671f, 0.75f, 0.73097f, 0.595671f, 0.25f),
                ModelHelper.vertex(0.595671f, 0f, 0.73097f, 0.595671f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.73097f, 0f, 0.595671f, 0.404329f, 1f),
                ModelHelper.vertex(0.73097f, 0.75f, 0.595671f, 0.404329f, 0.25f),
                ModelHelper.vertex(0.676777f, 0.75f, 0.676777f, 0.323223f, 0.25f),
                ModelHelper.vertex(0.676777f, 0f, 0.676777f, 0.323223f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.75f, 0f, 0.5f, 0.5f, 1f),
                ModelHelper.vertex(0.75f, 0.75f, 0.5f, 0.5f, 0.25f),
                ModelHelper.vertex(0.73097f, 0.75f, 0.595671f, 0.404329f, 0.25f),
                ModelHelper.vertex(0.73097f, 0f, 0.595671f, 0.404329f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.73097f, 0f, 0.404329f, 0.595671f, 1f),
                ModelHelper.vertex(0.73097f, 0.75f, 0.404329f, 0.595671f, 0.25f),
                ModelHelper.vertex(0.75f, 0.75f, 0.5f, 0.5f, 0.25f),
                ModelHelper.vertex(0.75f, 0f, 0.5f, 0.5f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.676777f, 0f, 0.323223f, 0.676777f, 1f),
                ModelHelper.vertex(0.676777f, 0.75f, 0.323223f, 0.676777f, 0.25f),
                ModelHelper.vertex(0.73097f, 0.75f, 0.404329f, 0.595671f, 0.25f),
                ModelHelper.vertex(0.73097f, 0f, 0.404329f, 0.595671f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.595671f, 0f, 0.26903f, 0.404329f, 1f),
                ModelHelper.vertex(0.595671f, 0.75f, 0.26903f, 0.404329f, 0.25f),
                ModelHelper.vertex(0.676777f, 0.75f, 0.323223f, 0.323224f, 0.25f),
                ModelHelper.vertex(0.676777f, 0f, 0.323223f, 0.323224f, 1f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.5f, 0f, 0.25f, 0.5f, 1f),
                ModelHelper.vertex(0.5f, 0.75f, 0.25f, 0.5f, 0.25f),
                ModelHelper.vertex(0.595671f, 0.75f, 0.26903f, 0.404329f, 0.25f),
                ModelHelper.vertex(0.595671f, 0f, 0.26903f, 0.404329f, 1f)
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
                ModelHelper.vertex(0.5f, 0f, 0.75f, 0.5f, 0.25f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.676777f, 0f, 0.676777f, 0.676777f, 0.323223f),
                ModelHelper.vertex(0.595671f, 0f, 0.73097f, 0.595671f, 0.26903f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.75f, 0f, 0.5f, 0.75f, 0.5f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.676777f, 0f, 0.323223f, 0.676777f, 0.676776f),
                ModelHelper.vertex(0.73097f, 0f, 0.404329f, 0.73097f, 0.59567f)
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
                ModelHelper.vertex(0.676777f, 0f, 0.676777f, 0.676777f, 0.323223f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.75f, 0f, 0.5f, 0.75f, 0.5f),
                ModelHelper.vertex(0.73097f, 0f, 0.595671f, 0.73097f, 0.404329f)
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
                ModelHelper.vertex(0.323223f, 0f, 0.676777f, 0.323223f, 0.323223f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.5f, 0f, 0.75f, 0.5f, 0.25f),
                ModelHelper.vertex(0.404329f, 0f, 0.73097f, 0.404329f, 0.26903f)
        ));

        builder.addUnculledFace(ModelHelper.quad(
                sprite,
                ModelHelper.vertex(0.676777f, 0f, 0.323223f, 0.676777f, 0.676776f),
                ModelHelper.vertex(0.5f, 0f, 0.5f, 0.5f, 0.5f),
                ModelHelper.vertex(0.5f, 0f, 0.25f, 0.5f, 0.75f),
                ModelHelper.vertex(0.595671f, 0f, 0.26903f, 0.595671f, 0.730969f)
        ));

        COLLISION_SHAPE = builder.build(context.getRenderType(modelLocation));
        return COLLISION_SHAPE;
    }
}
