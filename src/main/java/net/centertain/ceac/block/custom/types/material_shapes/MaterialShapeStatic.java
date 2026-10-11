package net.centertain.ceac.block.custom.types.material_shapes;

import net.centertain.ceac.material.shapes.utility.MaterialShapeVoxelHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class MaterialShapeStatic extends MaterialShape {
    private final Supplier<BakedModel> shapeModel;
    private final boolean greedyMeshing;

    private volatile VoxelShape staticShape;

    protected MaterialShapeStatic(
            Properties properties,
            String subcategory,
            Supplier<BakedModel> shapeModel
    ) {
        this(properties, subcategory, shapeModel, true);
    }

    protected MaterialShapeStatic(
            Properties properties,
            String subcategory,
            Supplier<BakedModel> shapeModel,
            boolean greedyMeshing
    ) {
        super(properties, subcategory);
        this.shapeModel = shapeModel;
        this.greedyMeshing = greedyMeshing;
    }

    protected MaterialShapeStatic(
            Properties properties,
            @Nullable String category,
            String subcategory,
            @Nullable Double price,
            Supplier<BakedModel> shapeModel,
            boolean greedyMeshing
    ) {
        super(properties, category, subcategory, price);
        this.shapeModel = shapeModel;
        this.greedyMeshing = greedyMeshing;
    }

    protected MaterialShapeStatic(
            Properties properties,
            @Nullable String category,
            String subcategory,
            @Nullable Double price,
            Supplier<BakedModel> shapeModel
    ) {
        this(
                properties,
                category,
                subcategory,
                price,
                shapeModel,
                true
        );
    }


    private VoxelShape getStaticShape() {
        VoxelShape result = staticShape;
        if (result != null)
            return result;

        synchronized (this) {
            result = staticShape;
            if (result == null) {
                BakedModel model = shapeModel.get();
                if (model == null)
                    throw new IllegalStateException("Static shape model has not baked yet.");

                result = MaterialShapeVoxelHelper.makeShape(model, greedyMeshing);
                staticShape = result;
            }
        }

        return result;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return getStaticShape();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getCollisionShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return getStaticShape();
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getOcclusionShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos
    ) {
        return getStaticShape();
    }
}
