package net.centertain.ceac.block.custom.material_shapes;

import net.centertain.ceac.block.custom.MaterialShape;
import net.minecraft.world.phys.Vec3;

public class MaterialShapeBlock extends MaterialShape {
    public MaterialShapeBlock(Properties properties) {
        super(
                null,
                10.0,
                properties
        );
    }

    @Override
    public boolean rotateFromViewDirection(Vec3 viewDirection, boolean clockwise) {
        return false;
    }
}
