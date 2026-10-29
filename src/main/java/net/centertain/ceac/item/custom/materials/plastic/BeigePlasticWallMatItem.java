package net.centertain.ceac.item.custom.materials.plastic;

import net.centertain.ceac.constants.CategoryConstants;
import net.centertain.ceac.item.custom.types.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class BeigePlasticWallMatItem extends MatItem {
    public BeigePlasticWallMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.PLASTIC,
                ModMaterials.BEIGE_PLASTIC_WALL
        );
    }
}
