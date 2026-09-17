package net.centertain.ceac.phys_screen.framework;

import org.joml.Matrix4f;

public interface GameRendererViewOffsetAccessor {
    Matrix4f ceac$getProjectionBeforeViewOffset();
    Matrix4f ceac$getProjectionAfterViewOffset();
}
