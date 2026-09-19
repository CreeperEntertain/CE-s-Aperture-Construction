package net.centertain.ceac.item.custom.materials;

import net.centertain.ceac.item.custom.MatItem;
import net.centertain.ceac.material.materials.ExampleMaterial;

public class ExampleMatItem extends MatItem {
    public ExampleMatItem(Properties properties) {
        super(properties, new ExampleMaterial());
    }
}
