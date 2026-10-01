package net.centertain.ceac.screen.elements;

import net.centertain.ceac.screen.framework.Element;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class Spacer implements Element {
    private int width;
    private int height;

    private @Nullable Supplier<@NotNull Integer> dynamicWidth = null;
    private @Nullable Supplier<@NotNull Integer> dynamicHeight = null;

    public Spacer(
            int width,
            int height
    ) {
        this.width = width;
        this.height = height;
    }

    public Spacer(
            @NotNull Element scaleSupplier
    ) {
        this.width = scaleSupplier.getWidth();
        this.height = scaleSupplier.getHeight();
    }

    public int getX() {
        return 0;
    }
    public int getY() {
        return 0;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public @Nullable Supplier<@NotNull Integer> getDynamicWidth() {
        return dynamicWidth;
    }
    public @Nullable Supplier<@NotNull Integer> getDynamicHeight() {
        return dynamicHeight;
    }

    public void setX(int x) {}
    public void setY(int y) {}
    public void setWidth(int width) {
        this.width = width;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    public void setDynamicWidth(@Nullable Supplier<@NotNull Integer> dynamicWidth) {
        this.dynamicWidth = dynamicWidth;
    }
    public void setDynamicHeight(@Nullable Supplier<@NotNull Integer> dynamicHeight) {
        this.dynamicHeight = dynamicHeight;
    }


    private void applyDynamics() {
        if (dynamicWidth != null) setWidth(dynamicWidth.get());
        if (dynamicHeight != null) setHeight(dynamicHeight.get());
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        applyDynamics();
    }
}
