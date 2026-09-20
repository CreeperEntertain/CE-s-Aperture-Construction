package net.centertain.ceac.material.utility;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

public final class MaterialShapeHelper {
    private MaterialShapeHelper() {}

    public static Vertex vertex(
            float x,
            float y,
            float z,
            float u,
            float v
    ) {
        return new Vertex(x, y, z, u, v);
    }

    public static BakedQuad quad(
            TextureAtlasSprite sprite,
            Vertex a,
            Vertex b,
            Vertex c,
            Vertex d
    ) {
        return makeQuad(sprite, a, b, c, d);
    }

    public static BakedQuad triangle(
            TextureAtlasSprite sprite,
            Vertex a,
            Vertex b,
            Vertex c
    ) {
        return makeQuad(sprite, a, b, c, c);
    }

    private static BakedQuad makeQuad(
            TextureAtlasSprite sprite,
            Vertex a,
            Vertex b,
            Vertex c,
            Vertex d
    ) {
        final int stride = DefaultVertexFormat.BLOCK.getIntegerSize();
        final int[] data = new int[stride * 4];

        Vector3f normal = new Vector3f(
                b.x - a.x,
                b.y - a.y,
                b.z - a.z
        ).cross(new Vector3f(
                c.x - a.x,
                c.y - a.y,
                c.z - a.z
        )).normalize();

        int packedNormal = packNormal(normal);

        Vertex[] vertices = {a, b, c, d};

        int colorOffset = DefaultVertexFormat.BLOCK.getOffset(1) / Integer.BYTES;
        int uvOffset = DefaultVertexFormat.BLOCK.getOffset(2) / Integer.BYTES;
        int lightOffset = DefaultVertexFormat.BLOCK.getOffset(3) / Integer.BYTES;
        int normalOffset = DefaultVertexFormat.BLOCK.getOffset(4) / Integer.BYTES;

        for (int i = 0; i < 4; i++) {
            Vertex vertex = vertices[i];
            int offset = i * stride;

            data[offset] = Float.floatToRawIntBits(vertex.x);
            data[offset + 1] = Float.floatToRawIntBits(vertex.y);
            data[offset + 2] = Float.floatToRawIntBits(vertex.z);

            data[offset + colorOffset] = -1;

            data[offset + uvOffset] = Float.floatToRawIntBits(sprite.getU(vertex.u * 16.0));
            data[offset + uvOffset + 1] = Float.floatToRawIntBits(sprite.getV(vertex.v * 16.0));

            data[offset + lightOffset] = 0;
            data[offset + lightOffset + 1] = 0;

            data[offset + normalOffset] = packedNormal;
        }

        return new BakedQuad(
                data,
                -1,
                Direction.getNearest(normal.x(), normal.y(), normal.z()),
                sprite,
                true,
                true
        );
    }

    private static int packNormal(Vector3f normal) {
        int x = ((byte) (normal.x() * 127.0f)) & 0xFF;
        int y = ((byte) (normal.y() * 127.0f)) & 0xFF;
        int z = ((byte) (normal.z() * 127.0f)) & 0xFF;
        return x | (y << 8) | (z << 16);
    }

    public record Vertex(
            float x,
            float y,
            float z,
            float u,
            float v
    ) {}
}
