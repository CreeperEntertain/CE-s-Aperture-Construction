package net.centertain.ceac.item.custom.materials.backpanel;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class WeatheredBackpanelMatItem extends MatItem {
    public WeatheredBackpanelMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.BACKPANEL,
                ModMaterials.WEATHERED_BACKPANEL
        );
    }
}
