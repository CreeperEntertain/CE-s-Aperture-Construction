package net.centertain.ceac.item.custom.materials.metal;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class LargeBlueMetalPanelMatItem extends MatItem {
    public LargeBlueMetalPanelMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.METAL,
                ModMaterials.LARGE_BLUE_METAL_PANEL
        );
    }
}
