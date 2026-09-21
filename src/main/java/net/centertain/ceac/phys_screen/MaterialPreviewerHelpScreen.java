package net.centertain.ceac.phys_screen;

import net.centertain.ceac.GuiConstants;
import net.centertain.ceac.phys_screen.elements.*;
import net.centertain.ceac.phys_screen.framework.PhysElement;
import net.centertain.ceac.phys_screen.framework.PhysScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class MaterialPreviewerHelpScreen extends PhysScreen {
    public static final int WIDTH = 150;
    public static final int HEIGHT = 114;

    public MaterialPreviewerHelpScreen() {
        super(Component.literal("Material Preview Help Screen"));
    }

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

        ResourceLocation[] keys = {
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_enter.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_w.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_a.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_s.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_d.png")
        };
        String[] prompts = {
                "Toggle Help UI",
                "Shift Material Up",
                "Shift Material Left",
                "Shift Material Down",
                "Shift Material Right"
        };

        List<PhysElement> keyPrompts = new ArrayList<>();

        for (int i = 0; i < keys.length; i++)
            keyPrompts.add(getKeyPrompt(keys, i, prompts));

        PhysStackPanel promptList = new PhysStackPanel(
                PhysStackPanel.Alignment.VERTICAL,
                WIDTH - (GuiConstants.SCREEN_PADDING * 2),
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                keyPrompts
        );

        addPhysElement(new PhysPadder(
                WIDTH,
                HEIGHT,
                GuiConstants.SCREEN_PADDING,
                promptList
        ));
    }

    private static @NotNull PhysElement getKeyPrompt(ResourceLocation[] keys, int i, String[] prompts) {
        PhysImage key = new PhysImage(
                16,
                16,
                keys[i]
        );
        PhysLabel prompt = new PhysLabel(
                GuiConstants.COLOR_SOLID_WHITE,
                Component.literal(prompts[i]),
                false
        );
        PhysAligner alignedPrompt = new PhysAligner(
                WIDTH - (2 * GuiConstants.SCREEN_PADDING) - GuiConstants.ELEMENT_PADDING - 16,
                16,
                PhysAligner.Alignment.LEFT,
                prompt
        );

        List<PhysElement> pair = new ArrayList<>(List.of(key, alignedPrompt));

        return new PhysStackPanel(
                PhysStackPanel.Alignment.HORIZONTAL,
                16,
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                pair
        );
    }
}
