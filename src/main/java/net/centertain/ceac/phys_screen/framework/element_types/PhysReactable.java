package net.centertain.ceac.phys_screen.framework.element_types;

import java.util.function.Supplier;

public interface PhysReactable {
    void parentHover(Supplier<Boolean> parentHoverState);
}
