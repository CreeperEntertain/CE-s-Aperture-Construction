package net.centertain.ceac.item.custom.materials.plastic;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class ReinforcedPlasticWallMatItem extends MatItem {
    public ReinforcedPlasticWallMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.PLASTIC,
                ModMaterials.REINFORCED_PLASTIC_WALL
        );
    }
}
