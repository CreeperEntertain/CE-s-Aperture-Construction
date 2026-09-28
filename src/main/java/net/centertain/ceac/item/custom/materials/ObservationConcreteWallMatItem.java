package net.centertain.ceac.item.custom.materials;

import net.centertain.ceac.CategoryConstants;
import net.centertain.ceac.item.custom.MatItem;
import net.centertain.ceac.material.ModMaterials;

public class ObservationConcreteWallMatItem extends MatItem {
    public ObservationConcreteWallMatItem(Properties properties) {
        super(
                properties,
                CategoryConstants.Sub.Mats.MISC,
                ModMaterials.OBSERVATION_CONCRETE_WALL
        );
    }
}
