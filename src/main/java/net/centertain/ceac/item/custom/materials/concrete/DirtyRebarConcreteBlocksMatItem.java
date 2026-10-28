package net.centertain.ceac.item.custom.materials.concrete;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class DirtyRebarConcreteBlocksMatItem extends MatItem {
    public DirtyRebarConcreteBlocksMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.CONCRETE,
                ModMaterials.DIRTY_REBAR_CONCRETE_BLOCKS
        );
    }
}
