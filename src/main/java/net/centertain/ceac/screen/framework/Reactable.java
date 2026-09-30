package net.centertain.ceac.screen.framework;

import java.util.function.Supplier;

public interface Reactable {
    void parentHover(Supplier<Boolean> parentHoverState);
}
