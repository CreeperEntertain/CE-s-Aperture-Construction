package net.centertain.ceac.material.shapes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record MaterialFaceKey(
        int pieceIndex,
        int faceIndex
) {
    public MaterialFaceKey {
        if (pieceIndex < 0)
            throw new IllegalArgumentException("Piece index cannot be negative");
        if (faceIndex < 0)
            throw new IllegalArgumentException("Face index cannot be negative");
    }

    @Override
    public @NotNull String toString() {
        return pieceIndex + ":" + faceIndex;
    }

    public static @Nullable MaterialFaceKey parse(String value) {
        int separator = value.indexOf(':');

        if (separator < 0)
            try {
                return new MaterialFaceKey(0, Integer.parseInt(value));
            } catch (NumberFormatException ignored) {
                return null;
            }

        try {
            return new MaterialFaceKey(
                    Integer.parseInt(value.substring(0, separator)),
                    Integer.parseInt(value.substring(separator + 1))
            );
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
