package net.centertain.ceac.screen.framework.element_types;

import java.util.function.Supplier;

public interface Reactable {
    void parentHover(Supplier<Boolean> parentHoverState);
}
