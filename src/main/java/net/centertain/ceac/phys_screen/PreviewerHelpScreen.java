package net.centertain.ceac.phys_screen;

import net.centertain.ceac.GuiConstants;
import net.centertain.ceac.decal.client.DecalLoader;
import net.centertain.ceac.phys_screen.elements.*;
import net.centertain.ceac.phys_screen.framework.PhysElement;
import net.centertain.ceac.phys_screen.framework.PhysScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.centertain.ceac.CeacMod.MOD_ID;

public class PreviewerHelpScreen extends PhysScreen {
    public static final int WIDTH = 150;
    public static final int HEIGHT = 361;

    public PreviewerHelpScreen() {
        super(Component.literal("Precise Placement Help Screen"));
    }

    private void example() {}

    @Override
    protected void init() {
        super.init();

        PhysRect background = new PhysRect(
                0,
                0,
                WIDTH,
                HEIGHT,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75,
                GuiConstants.COLOR_SOLID_WHITE,
                1
        );
        addPhysElement(background);

        ResourceLocation[] keys = {
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_enter.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/scroll_wheel.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_w.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_s.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_a.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_d.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_q.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_e.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_r.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_f.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_up.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_down.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_left.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_right.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_page_up.png"),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/phys_screen/key_page_down.png")
        };
        String[] prompts = {
                "Toggle Help UI",
                "Rotate Decal",
                "Move Decal Forward",
                "Move Decal Back",
                "Move Decal Left",
                "Move Decal Right",
                "Move Decal In",
                "Move Decal Out",
                "Stretch Decal",
                "Squash Decal",
                "Tilt Decal Up",
                "Tilt Decal Down",
                "Tilt Decal Left",
                "Tilt Decal Right",
                "Make Grid Larger",
                "Make Grid Smaller"
        };

        List<PhysElement> keyPrompts = new ArrayList<>();

        for (int i = 0; i < keys.length; i++) {
            PhysStackPanel keyPrompt = getKeyPrompt(keys, i, prompts);
            keyPrompts.add(keyPrompt);
        }

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

    private static @NotNull PhysStackPanel getKeyPrompt(ResourceLocation[] keys, int i, String[] prompts) {
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
