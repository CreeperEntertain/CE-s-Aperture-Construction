package net.centertain.ceac.material.shapes;

import net.minecraft.world.phys.Vec3;

import java.util.List;

public record MaterialShapeFaceInstance(
        MaterialShapeFace face,
        int pieceIndex,
        int faceIndex,
        Vec3 offset
) {
    public MaterialFaceKey key() {
        return new MaterialFaceKey(pieceIndex, faceIndex);
    }

    public List<Vec3> getVertices() {
        return face.getVertices()
                .stream()
                .map(vertex -> vertex.add(offset))
                .toList();
    }
}
