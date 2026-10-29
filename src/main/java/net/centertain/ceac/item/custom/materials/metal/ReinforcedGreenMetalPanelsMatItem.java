package net.centertain.ceac.item.custom.materials.metal;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class ReinforcedGreenMetalPanelsMatItem extends MatItem {
    public ReinforcedGreenMetalPanelsMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.METAL,
                ModMaterials.REINFORCED_GREEN_METAL_PANELS
        );
    }
}
