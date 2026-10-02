package net.centertain.ceac.font;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.SheetGlyphInfo;
import com.mojang.blaze3d.platform.NativeImage;
import dev.thecodewarrior.bitfont.data.BitGrid;
import dev.thecodewarrior.bitfont.data.Bitfont;
import dev.thecodewarrior.bitfont.data.Glyph;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

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
        return new BitfontGlyphInfo(glyph);
    }

    @Override
    public @NotNull IntSet getSupportedGlyphs() {
        return font.getGlyphs().keySet();
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static final class BitfontGlyphInfo implements GlyphInfo {
        private final Glyph glyph;

        private BitfontGlyphInfo(Glyph glyph) {
            this.glyph = glyph;
        }

        @Override
        public float getAdvance() {
            return glyph.getAdvance();
        }

        @SuppressWarnings("resource")
        @Override
        public @NotNull BakedGlyph bake(@NotNull Function<SheetGlyphInfo, BakedGlyph> bakery) {
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

            return bakery.apply(new SheetGlyphInfo() {
                @Override
                public int getPixelWidth() {
                    return grid.getWidth();
                }

                @Override
                public int getPixelHeight() {
                    return grid.getHeight();
                }

                @Override
                public void upload(int x, int y) {
                    image.upload(
                            0, x, y, 0, 0, grid.getWidth(), grid.getHeight(),
                            false, false, false, true
                    );
                }

                @Override
                public boolean isColored() {
                    return false;
                }

                @Override
                public float getOversample() {
                    return 1.0f;
                }

                @Override
                public float getLeft() {
                    return glyph.getBearingX();
                }
                @Override
                public float getRight() {
                    return glyph.getBearingX() + glyph.getWidth();
                }
                @Override
                public float getUp() {
                    return glyph.getBearingY();
                }
                @Override
                public float getDown() {
                    return glyph.getBearingY() + glyph.getHeight();
                }

                @Override
                public float getBearingX() {
                    return glyph.getBearingX();
                }
                @Override
                public float getBearingY() {
                    return glyph.getBearingY();
                }
            });
        }
    }
}
