package net.centertain.ceac.font;

import dev.thecodewarrior.bitfont.data.Bitfont;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public final class BitfontManager {
    private BitfontManager() {}

    private static final ResourceLocation FONT_RESOURCE =
            ResourceLocation.fromNamespaceAndPath("ceac", "font/mc_classic_plus.bitfont");
    private static final ResourceLocation FONT_ID =
            ResourceLocation.fromNamespaceAndPath("ceac", "bitfont");

    private static @Nullable FontSet fontSet = null;

    public static final Font FONT = new Font(id -> {
        if (fontSet == null)
            throw new IllegalStateException("Bitfont has not been loaded yet");
        return fontSet;
    }, false);

    public static final PreparableReloadListener RELOAD_LISTENER = new SimplePreparableReloadListener<Bitfont>() {
        @Override
        protected @NotNull Bitfont prepare(
                @NotNull ResourceManager resourceManager,
                @NotNull ProfilerFiller profiler
        ) {
            Resource resource = resourceManager
                    .getResource(FONT_RESOURCE)
                    .orElseThrow(() -> new IllegalStateException("Missing Bitfont resource: " + FONT_RESOURCE));

            try (InputStream stream = resource.open()) {
                return Bitfont.unpack(stream);
            } catch (IOException exception) {
                throw new RuntimeException("Failed to load Bitfont: " + FONT_RESOURCE, exception);
            }
        }

        @Override
        protected void apply(
                @NotNull Bitfont bitfont,
                @NotNull ResourceManager resourceManager,
                @NotNull ProfilerFiller profiler
        ) {
            FontSet newFontSet = new FontSet(
                    Minecraft.getInstance().getTextureManager(),
                    FONT_ID
            );
            newFontSet.reload(List.of(new BitfontProvider(bitfont)));

            FontSet oldFontSet = fontSet; // Ugh, yucky, terrible. We don't want this...
            fontSet = newFontSet; // Nice, shiny, brushed. We want this!

            if (oldFontSet != null)
                oldFontSet.close();
        }
    };
}
