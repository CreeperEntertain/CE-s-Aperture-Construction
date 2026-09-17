package net.centertain.ceac.phys_screen;

import net.centertain.ceac.GuiConstants;
import net.centertain.ceac.decal.client.DecalLoader;
import net.centertain.ceac.phys_screen.elements.*;
import net.centertain.ceac.phys_screen.framework.PhysElement;
import net.centertain.ceac.phys_screen.framework.PhysScreen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

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

        addPhysElement(new PhysRect(
                0,
                0,
                WIDTH,
                HEIGHT,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75,
                GuiConstants.COLOR_SOLID_WHITE,
                1
        ));

        List<PhysElement> labels = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            labels.add(new PhysLabel(
                    0,
                    0,
                    GuiConstants.COLOR_SOLID_WHITE,
                    Component.literal("HAII!!!"),
                    false
            ));
        addPhysElement(new PhysStackPanel(
                15,
                15,
                PhysStackPanel.Alignment.VERTICAL,
                120,
                GuiConstants.ELEMENT_PADDING,
                labels
        ));
    }
}
