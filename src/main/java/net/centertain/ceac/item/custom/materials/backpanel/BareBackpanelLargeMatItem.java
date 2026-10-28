package net.centertain.ceac.item.custom.materials.backpanel;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class BareBackpanelLargeMatItem extends MatItem {
    public BareBackpanelLargeMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.BACKPANEL,
                ModMaterials.BARE_BACKPANEL_LARGE
        );
    }
}
