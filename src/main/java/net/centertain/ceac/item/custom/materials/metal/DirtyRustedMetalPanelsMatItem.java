package net.centertain.ceac.item.custom.materials.metal;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class DirtyRustedMetalPanelsMatItem extends MatItem {
    public DirtyRustedMetalPanelsMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.METAL,
                ModMaterials.DIRTY_RUSTED_METAL_PANELS
        );
    }
}
