package net.centertain.ceac.screen;

import net.centertain.ceac.constants.GuiConstants;
import net.centertain.ceac.decal.DecalDefinition;
import net.centertain.ceac.decal.client.DecalLoader;
import net.centertain.ceac.decal.client.DecalPack;
import net.centertain.ceac.decal.network.SyncDecalItemPacket;
import net.centertain.ceac.network.ModNetworking;
import net.centertain.ceac.screen.elements.*;
import net.centertain.ceac.screen.framework.Element;
import net.centertain.ceac.screen.framework.Screen;
import net.centertain.ceac.screen.templates.ImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DecalItemScreen extends Screen {
    private int left;
    private int top;
    private int right;
    private int bottom;

    @SuppressWarnings("FieldCanBeLocal")
    private ScrollContainer tabScroll;
    private ScrollContainer decalScroll;

    private final InteractionHand hand;

    public DecalItemScreen(InteractionHand hand) {
        super(Component.empty());
        this.hand = hand;
    }

    @Override
    public void render(
            @NotNull GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    public void setPack(DecalPack pack) {
        if (decalScroll != null)
            removeElement(decalScroll);

        List<Element> decals = new ArrayList<>();
        for (DecalDefinition decal : pack.getDecals()) {
            Button button = createButton(
                    GuiConstants.IMAGE_BUTTON_WIDTH,
                    GuiConstants.IMAGE_BUTTON_HEIGHT,
                    Component.literal(decal.getName()),
                    GuiConstants.COLOR_SOLID_WHITE,
                    0.5f,
                    GuiConstants.COLOR_TRANSPARENT,
                    GuiConstants.COLOR_SOLID_WHITE,
                    decal.getResourceLocation(),
                    () -> setDecal(decal)
            );
            decals.add(button);
        }

        FlowPanel decalPanel = new FlowPanel(
                left,
                top,
                FlowPanel.Alignment.HORIZONTAL,
                right - left,
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSLUCENT_BLACK_75,
                decals
        );
        decalScroll = new ScrollContainer(
                left,
                top,
                right - left,
                bottom - top,
                ScrollContainer.Alignment.VERTICAL,
                decalPanel,
                decalPanel.getHeight(),
                GuiConstants.FLOW_SCROLL_SPEED
        );

        addElement(decalScroll);
    }

    @SuppressWarnings("SameParameterValue")
    private Button createButton(
            int width,
            int height,
            Component text,
            int textColor,
            float textScale,
            int backgroundColor,
            int outlineColor,
            @Nullable ResourceLocation texture,
            @NotNull Runnable onPress
    ) {
        return ImageButton.create(
                width,
                height,
                text,
                textColor,
                textScale,
                backgroundColor,
                outlineColor,
                texture,
                onPress
        );
    }

    private void setDecal(DecalDefinition decal) {
        ResourceLocation texture = decal.getResourceLocation();
        ModNetworking.CHANNEL.sendToServer(new SyncDecalItemPacket(hand, texture));
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    protected void init() {
        super.init();

        left = GuiConstants.SCREEN_PADDING + GuiConstants.STACK_PANEL_WIDTH + GuiConstants.ELEMENT_PADDING;
        top = GuiConstants.SCREEN_PADDING;
        right = width - GuiConstants.SCREEN_PADDING;
        bottom = height - GuiConstants.SCREEN_PADDING;

        List<DecalPack> decalPacks = DecalLoader.getPacks();
        List<Element> tabs = new ArrayList<>();
        for (DecalPack decalPack : decalPacks) {
            Button button = createButton(
                    GuiConstants.STACK_PANEL_WIDTH,
                    GuiConstants.TAB_BUTTON_HEIGHT,
                    Component.literal(decalPack.getName()),
                    GuiConstants.COLOR_SOLID_WHITE,
                    1.0f,
                    GuiConstants.COLOR_TRANSLUCENT_BLACK_75,
                    GuiConstants.COLOR_SOLID_WHITE,
                    null,
                    () -> setPack(decalPack)
            );

            tabs.add(button);
        }

        StackPanel tabStack = new StackPanel(
                GuiConstants.SCREEN_PADDING,
                GuiConstants.SCREEN_PADDING,
                StackPanel.Alignment.VERTICAL,
                GuiConstants.STACK_PANEL_WIDTH,
                GuiConstants.ELEMENT_PADDING,
                GuiConstants.COLOR_TRANSPARENT,
                tabs
        );
        tabScroll = new ScrollContainer(
                GuiConstants.SCREEN_PADDING,
                GuiConstants.SCREEN_PADDING,
                GuiConstants.STACK_PANEL_WIDTH,
                height - GuiConstants.SCREEN_PADDING * 2,
                ScrollContainer.Alignment.VERTICAL,
                tabStack,
                tabStack.getHeight(),
                GuiConstants.STACK_SCROLL_SPEED
        );
        addElement(tabScroll);

        setPack(decalPacks.get(0));
    }
}