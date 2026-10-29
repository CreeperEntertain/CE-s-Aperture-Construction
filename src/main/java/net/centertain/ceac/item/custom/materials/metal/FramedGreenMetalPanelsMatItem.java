package net.centertain.ceac.item.custom.materials.metal;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class FramedGreenMetalPanelsMatItem extends MatItem {
    public FramedGreenMetalPanelsMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.METAL,
                ModMaterials.FRAMED_GREEN_METAL_PANELS
        );
    }
}
