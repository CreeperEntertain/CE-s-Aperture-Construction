package net.centertain.ceac.font;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.platform.NativeImage;
import dev.thecodewarrior.bitfont.data.BitGrid;
import dev.thecodewarrior.bitfont.data.Bitfont;
import dev.thecodewarrior.bitfont.data.Glyph;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.gui.font.providers.BitmapProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BitfontProvider implements GlyphProvider {
    private final Bitfont font;

    public BitfontProvider(Bitfont font) {
        this.font = font;
    }

    @Override
    public @Nullable GlyphInfo getGlyph(int codePoint) {
        Glyph glyph = font.getGlyphs().get(codePoint);
        if (glyph == null)
            return null;

        BitGrid grid = glyph.getImage();
        NativeImage image = new NativeImage(
                NativeImage.Format.RGBA,
                grid.getWidth(),
                grid.getHeight(),
                false
        );

        for (int y = 0; y < grid.getHeight(); y++)
            for (int x = 0; x < grid.getWidth(); x++)
                if (grid.get(x, y))
                    image.setPixelRGBA(x, y, 0xFFFFFFFF);

        return new BitmapProvider.Glyph(
                1.0f,
                image,
                0,
                0,
                glyph.getWidth(),
                glyph.getHeight(),
                glyph.getAdvance(),
                10 - glyph.getBearingY()
        );
    }

    @Override
    public @NotNull IntSet getSupportedGlyphs() {
        return font.getGlyphs().keySet();
    }
}
