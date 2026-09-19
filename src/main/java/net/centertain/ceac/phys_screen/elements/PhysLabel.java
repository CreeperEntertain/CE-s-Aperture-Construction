package net.centertain.ceac.phys_screen.elements;

import net.centertain.ceac.phys_screen.framework.PhysElement;
import net.centertain.ceac.phys_screen.framework.PhysGuiGraphics;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class PhysLabel implements PhysElement {
    private int x;
    private int y;
    private int color;
    private Component text;
    private boolean shadow;

    public PhysLabel(
            int color,
            Component text,
            boolean shadow
    ) {
        this.x = 0;
        this.y = 0;
        this.color = color;
        this.text = text;
        this.shadow = shadow;
    }
    public PhysLabel(
            int x,
            int y,
            int color,
            Component text,
            boolean shadow
    ) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.text = text;
        this.shadow = shadow;
    }
    public PhysLabel(
            @NotNull PhysElement positionSupplier,
            int color,
            Component text,
            boolean shadow
    ) {
        this.x = positionSupplier.getY();
        this.y = positionSupplier.getY();
        this.color = color;
        this.text = text;
        this.shadow = shadow;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getWidth() {
        return Minecraft.getInstance().font.width(text);
    }
    public int getHeight() {
        return Minecraft.getInstance().font.lineHeight;
    }
    public int getColor() {
        return color;
    }
    public Component getText() {
        return text;
    }
    public boolean getShadow() {
        return shadow;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setWidth(int width) {}
    public void setHeight(int height) {}
    public void setColor(int color) {
        this.color = color;
    }
    public void setText(Component text) {
        this.text = text;
    }
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }


    public void renderPhysical(PhysGuiGraphics guiGraphics) {
        guiGraphics.drawString(
                Minecraft.getInstance().font,
                text,
                x,
                y,
                color,
                shadow
        );
    }
}
