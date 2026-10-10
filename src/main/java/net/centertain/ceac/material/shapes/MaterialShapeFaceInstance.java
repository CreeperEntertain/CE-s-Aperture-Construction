package net.centertain.ceac.material.shapes;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public record MaterialShapeFaceInstance(
        MaterialShapeFace face,
        int pieceIndex,
        int faceIndex,
        Vec3 offset,
        Direction.Axis axis
) {
    public MaterialShapeFaceInstance(
            MaterialShapeFace face,
            int pieceIndex,
            int faceIndex,
            Vec3 offset
    ) {
        this(face, pieceIndex, faceIndex, offset, Direction.Axis.Y);
    }

    public MaterialFaceKey key() {
        return new MaterialFaceKey(pieceIndex, faceIndex);
    }

    public Vec3 transformPoint(Vec3 point) {
        Vec3 transformed = switch (axis) {
            case X -> new Vec3(point.y, 1.0 - point.x, point.z);
            case Y -> point;
            case Z -> new Vec3(point.x, 1.0 - point.z, point.y);
        };
        return transformed.add(offset);
    }

    public List<Vec3> getVertices() {
        return face.getVertices()
                .stream()
                .map(this::transformPoint)
                .toList();
    }
}
