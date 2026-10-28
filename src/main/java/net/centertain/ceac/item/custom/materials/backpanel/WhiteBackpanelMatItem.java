package net.centertain.ceac.item.custom.materials.backpanel;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class WhiteBackpanelMatItem extends MatItem {
    public WhiteBackpanelMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.BACKPANEL,
                ModMaterials.WHITE_BACKPANEL
        );
    }
}
