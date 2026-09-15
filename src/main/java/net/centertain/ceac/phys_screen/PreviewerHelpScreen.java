package net.centertain.ceac.phys_screen;

import net.centertain.ceac.GuiConstants;
import net.centertain.ceac.PhysScreen;
import net.centertain.ceac.phys_screen.elements.PhysButton;
import net.minecraft.network.chat.Component;

public class PreviewerHelpScreen extends PhysScreen {
    public static final int WIDTH = 150;
    public static final int HEIGHT = 100;

    public PreviewerHelpScreen() {
        super(Component.literal("Example"));
    }

    private void example() {}

    @Override
    protected void init() {
        super.init();

        addWidget(new PhysButton(
                25,
                25,
                100,
                10,
                Component.literal("Hello!"),
                GuiConstants.COLOR_SOLID_WHITE,
                1.0f,
                GuiConstants.COLOR_TRANSPARENT,
                GuiConstants.COLOR_SOLID_WHITE,
                null,
                this::example
        ));
    }
}
