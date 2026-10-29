package net.centertain.ceac.item.custom.materials.metal;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class RustedRectangularMetalPanelsMatItem extends MatItem {
    public RustedRectangularMetalPanelsMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.METAL,
                ModMaterials.RUSTED_RECTANGULAR_METAL_PANELS
        );
    }
}
