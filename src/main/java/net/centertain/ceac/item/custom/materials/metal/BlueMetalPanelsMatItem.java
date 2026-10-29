package net.centertain.ceac.item.custom.materials.metal;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class BlueMetalPanelsMatItem extends MatItem {
    public BlueMetalPanelsMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.METAL,
                ModMaterials.BLUE_METAL_PANELS
        );
    }
}
