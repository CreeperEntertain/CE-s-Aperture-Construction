package net.centertain.ceac.phys_screen.framework;

import java.util.function.Supplier;

public interface PhysReactable {
    void parentHover(Supplier<Boolean> parentHoverState);
}
