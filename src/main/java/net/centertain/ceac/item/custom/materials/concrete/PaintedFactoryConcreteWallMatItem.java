package net.centertain.ceac.item.custom.materials.concrete;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class PaintedFactoryConcreteWallMatItem extends MatItem {
    public PaintedFactoryConcreteWallMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.CONCRETE,
                ModMaterials.PAINTED_FACTORY_CONCRETE_WALL
        );
    }
}
