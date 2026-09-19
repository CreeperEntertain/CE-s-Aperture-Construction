package net.centertain.ceac.item.custom;

import net.centertain.ceac.block.custom.MaterialShape;
import net.centertain.ceac.material.Material;
import net.centertain.ceac.material.MaterialShapeBlockEntity;
import net.centertain.ceac.material.MaterialShapeFace;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.List;

public abstract class MatItem extends Item {
    private final Material material;
    private Vector2i materialCoordinate;

    protected MatItem(
            Properties properties,
            Material material
    ) {
        super(properties);
        this.material = material;
        this.materialCoordinate = new Vector2i();
    }

    public Material getMaterial() {
        return material;
    }
    public Vector2i getMaterialCoordinate() {
        return materialCoordinate;
    }

    public boolean setMaterialCoordinate(Vector2i materialCoordinate) {
        if (!material.containsCoordinate(materialCoordinate))
            return false;
        this.materialCoordinate = materialCoordinate;
        return true;
    }


    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof MaterialShape shape))
            return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof MaterialShapeBlockEntity blockEntity))
            return InteractionResult.PASS;

        Vec3 localHit = context.getClickLocation().subtract(
                pos.getX(),
                pos.getY(),
                pos.getZ()
        );

        MaterialShapeFace face = findFace(shape, localHit);
        if (face == null)
            return InteractionResult.PASS;
        if (!shape.canApplyMaterial(state, face, context.getItemInHand()))
            return InteractionResult.PASS;
        if (!level.isClientSide)
            serverSide(
                    shape,
                    face,
                    blockEntity,
                    level,
                    pos,
                    state
            );

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void serverSide(
            MaterialShape shape,
            MaterialShapeFace face,
            MaterialShapeBlockEntity blockEntity,
            Level level,
            BlockPos pos,
            BlockState state
    ) {
        int faceIndex = shape.faces().indexOf(face);

        blockEntity.setMaterial(
                faceIndex,
                material,
                materialCoordinate
        );

        blockEntity.setChanged();
        blockEntity.requestModelDataUpdate();

        level.sendBlockUpdated(
                pos,
                state,
                state,
                Block.UPDATE_CLIENTS
        );
    }

    private @Nullable MaterialShapeFace findFace(
            MaterialShape shape,
            Vec3 point
    ) {
        for (MaterialShapeFace face : shape.faces())
            if (containsPoint(face.getVertices(), point))
                return face;
        return null;
    }

    private boolean containsPoint(
            List<Vec3> vertices,
            Vec3 point
    ) {
        if (vertices.size() < 3)
            return false;
        for (int i = 1; i < vertices.size() - 1; i++)
            if (pointIntTriangle(
                    point,
                    vertices.get(0),
                    vertices.get(i),
                    vertices.get(i + 1)
            ))
                return true;
        return false;
    }

    private boolean pointIntTriangle(
            Vec3 point,
            Vec3 a,
            Vec3 b,
            Vec3 c
    ) {
        Vec3 normal = b.subtract(a).cross(c.subtract(a)).normalize();
        if (Math.abs(point.subtract(a).dot(normal)) > 0.001)
            return false;

        Vec3 ab = b.subtract(a);
        Vec3 bc = c.subtract(b);
        Vec3 ca = a.subtract(c);

        Vec3 ap = point.subtract(a);
        Vec3 bp = point.subtract(b);
        Vec3 cp = point.subtract(c);

        double side1 = ab.cross(ap).dot(normal);
        double side2 = bc.cross(bp).dot(normal);
        double side3 = ca.cross(cp).dot(normal);

        return
                side1 >= -0.001 &&
                side2 >= -0.001 &&
                side3 >= -0.001;
    }
}
