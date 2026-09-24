package net.centertain.ceac.material;

import net.centertain.ceac.item.custom.MatItem;
import net.centertain.ceac.material.utility.MaterialShapeHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MatItemGeometry implements IUnbakedGeometry<MatItemGeometry> {
    @Override
    public BakedModel bake(
            IGeometryBakingContext context,
            ModelBaker baker,
            Function<net.minecraft.client.resources.model.Material, TextureAtlasSprite> sprites,
            ModelState modelState,
            ItemOverrides overrides,
            ResourceLocation modelLocation
    ) {
        ResourceLocation itemId = ResourceLocation.fromNamespaceAndPath(modelLocation.getNamespace(), modelLocation.getPath());
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        if (!(item instanceof MatItem matItem))
            throw new IllegalStateException("Mat item model used for non-MatItem: " + itemId);

        Material material = matItem.getMaterial();
        SimpleBakedModel.Builder builder = new SimpleBakedModel.Builder(
                context.useAmbientOcclusion(),
                context.useBlockLight(),
                false, // Keeps it down low (2D)
                context.getTransforms(),
                overrides
        );

        int width = material.getTilingSize().x;
        int height = material.getTilingSize().y;

        // Fit that shit in 1x1 dimensions
        float fit = 1.0f / Math.max(width, height);

        float totalWidth = width * fit;
        float totalHeight = height * fit;

        float left = 0.5F - totalWidth * 0.5F;
        float bottom = 0.5F - totalHeight * 0.5F;

        // One plane, we like to keep it down low
        float z = 0.5f;

        List<BakedQuad> quads = new ArrayList<>(width * height * 2);

        for (int y = 0; y < height; y++) {
            float y0 = bottom + y * fit;
            float y1 = y0 + fit;

            for (int x = 0; x < width; x++) {
                float x0 = left + x * fit;
                float x1 = x0 + fit;

                ResourceLocation frontTexture = material.getTexture(new Vector2i(x, y));
                if (frontTexture == null)
                    continue;

                TextureAtlasSprite frontSprite = sprites.apply(
                        context.getMaterial("layer" + (y * width + x))
                );

                quads.add(MaterialShapeHelper.quad(
                        frontSprite,
                        MaterialShapeHelper.vertex(x0, y0, z, 0.0F, 1.0F),
                        MaterialShapeHelper.vertex(x1, y0, z, 1.0F, 1.0F),
                        MaterialShapeHelper.vertex(x1, y1, z, 1.0F, 0.0F),
                        MaterialShapeHelper.vertex(x0, y1, z, 0.0F, 0.0F)
                ));

                int backX = width - 1 - x;
                ResourceLocation backTexture = material.getTexture(new Vector2i(backX, y));
                if (backTexture == null)
                    continue;

                TextureAtlasSprite backSprite = sprites.apply(
                        context.getMaterial("layer" + (y * width + backX))
                );

                quads.add(MaterialShapeHelper.quad(
                        backSprite,
                        MaterialShapeHelper.vertex(x0, y0, z, 1.0F, 1.0F),
                        MaterialShapeHelper.vertex(x0, y1, z, 1.0F, 0.0F),
                        MaterialShapeHelper.vertex(x1, y1, z, 0.0F, 0.0F),
                        MaterialShapeHelper.vertex(x1, y0, z, 0.0F, 1.0F)
                ));
            }
        }

        for (BakedQuad quad : quads)
            builder.addUnculledFace(quad);
        if (!quads.isEmpty())
            builder.particle(quads.get(0).getSprite());

        return builder.build(context.getRenderType(modelLocation));
    }
}