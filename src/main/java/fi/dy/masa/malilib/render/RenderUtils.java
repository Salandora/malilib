package fi.dy.masa.malilib.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import fi.dy.masa.malilib.config.HudAlignment;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.client.MainWindow;
import fi.dy.masa.malilib.util.InventoryUtils;
import fi.dy.masa.malilib.util.PositionUtils;
import fi.dy.masa.malilib.util.PositionUtils.HitPart;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.storage.MapData;

public class RenderUtils
{
    public static final ResourceLocation TEXTURE_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
    //private static final Vec3d LIGHT0_POS = (new Vec3d( 0.2D, 1.0D, -0.7D)).normalize();
    //private static final Vec3d LIGHT1_POS = (new Vec3d(-0.2D, 1.0D,  0.7D)).normalize();

    public static void drawOutlinedBox(int x, int y, int width, int height, int colorBg, int colorBorder)
    {
        drawOutlinedBox(x, y, width, height, colorBg, colorBorder, 0f);
    }

    public static void drawOutlinedBox(int x, int y, int width, int height, int colorBg, int colorBorder, float zLevel)
    {
        // Draw the background
        drawRect(x, y, width, height, colorBg, zLevel);

        // Draw the border
        drawOutline(x - 1, y - 1, width + 2, height + 2, colorBorder, zLevel);
    }

    public static void drawOutline(int x, int y, int width, int height, int colorBorder)
    {
        drawOutline(x, y, width, height, colorBorder, 0f);
    }

    public static void drawOutline(int x, int y, int width, int height, int colorBorder, float zLevel)
    {
        drawRect(x                    , y,      1, height, colorBorder, zLevel); // left edge
        drawRect(x + width - 1        , y,      1, height, colorBorder, zLevel); // right edge
        drawRect(x + 1,              y, width - 2,      1, colorBorder, zLevel); // top edge
        drawRect(x + 1, y + height - 1, width - 2,      1, colorBorder, zLevel); // bottom edge
    }

    public static void drawOutline(int x, int y, int width, int height, int borderWidth, int colorBorder)
    {
        drawOutline(x, y, width, height, borderWidth, colorBorder, 0f);
    }

    public static void drawOutline(int x, int y, int width, int height, int borderWidth, int colorBorder, float zLevel)
    {
        drawRect(x                      ,                        y, borderWidth            , height     , colorBorder, zLevel); // left edge
        drawRect(x + width - borderWidth,                        y, borderWidth            , height     , colorBorder, zLevel); // right edge
        drawRect(x + borderWidth        ,                        y, width - 2 * borderWidth, borderWidth, colorBorder, zLevel); // top edge
        drawRect(x + borderWidth        , y + height - borderWidth, width - 2 * borderWidth, borderWidth, colorBorder, zLevel); // bottom edge
    }

    public static void drawTexturedRect(int x, int y, int u, int v, int width, int height)
    {
        drawTexturedRect(x, y, u, v, width, height, 0);
    }

    public static void drawRect(int x, int y, int width, int height, int color)
    {
        drawRect(x, y, width, height, color, 0f);
    }

    public static void drawRect(int x, int y, int width, int height, int color, float zLevel)
    {
        float a = (float) (color >> 24 & 255) / 255.0F;
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >>  8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(r, g, b, a);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        buffer.pos(x        , y         , zLevel).endVertex();
        buffer.pos(x        , y + height, zLevel).endVertex();
        buffer.pos(x + width, y + height, zLevel).endVertex();
        buffer.pos(x + width, y         , zLevel).endVertex();

        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawTexturedRect(int x, int y, int u, int v, int width, int height, float zLevel)
    {
        float pixelWidth = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        buffer.pos(x        , y + height, zLevel).tex( u          * pixelWidth, (v + height) * pixelWidth).endVertex();
        buffer.pos(x + width, y + height, zLevel).tex((u + width) * pixelWidth, (v + height) * pixelWidth).endVertex();
        buffer.pos(x + width, y         , zLevel).tex((u + width) * pixelWidth,  v           * pixelWidth).endVertex();
        buffer.pos(x        , y         , zLevel).tex( u          * pixelWidth,  v           * pixelWidth).endVertex();

        tessellator.draw();
    }

    public static void drawTexturedRectBatched(int x, int y, int u, int v, int width, int height, BufferBuilder buffer)
    {
        drawTexturedRectBatched(x, y, u, v, width, height, 0, buffer);
    }

    public static void drawTexturedRectBatched(int x, int y, int u, int v, int width, int height, float zLevel, BufferBuilder buffer)
    {
        float pixelWidth = 0.00390625F;

        buffer.pos(x        , y + height, zLevel).tex( u          * pixelWidth, (v + height) * pixelWidth).endVertex();
        buffer.pos(x + width, y + height, zLevel).tex((u + width) * pixelWidth, (v + height) * pixelWidth).endVertex();
        buffer.pos(x + width, y         , zLevel).tex((u + width) * pixelWidth,  v           * pixelWidth).endVertex();
        buffer.pos(x        , y         , zLevel).tex( u          * pixelWidth,  v           * pixelWidth).endVertex();
    }

    public static void drawHoverText(int x, int y, List<String> textLines)
    {
        Minecraft mc = Minecraft.getInstance();

        if (textLines.isEmpty() == false && mc.currentScreen != null)
        {
            FontRenderer font = mc.fontRenderer;
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            int maxLineLength = 0;
            int maxWidth = mc.currentScreen.width;
            int maxHeight = mc.currentScreen.height;
            List<String> linesNew = new ArrayList<>();

            for (String lineOrig : textLines)
            {
                String[] lines = lineOrig.split("\\\\n");

                for (String line : lines)
                {
                    int length = font.getStringWidth(line);

                    if (length > maxLineLength)
                    {
                        maxLineLength = length;
                    }

                    linesNew.add(line);
                }
            }

            textLines = linesNew;

            final int lineHeight = font.FONT_HEIGHT + 1;
            int textStartX = x + 12;
            int textStartY = y - 12;
            int textHeight = textLines.size() * lineHeight - 2;

            if (textStartX + maxLineLength > maxWidth)
            {
                textStartX -= 28 + maxLineLength;
            }

            if (textStartY + textHeight + 6 > maxHeight)
            {
                textStartY = maxHeight - textHeight - 6;
            }

            double zLevel = 300;
            int borderColor = 0xF0100010;
            drawGradientRect(textStartX - 3, textStartY - 4, textStartX + maxLineLength + 3, textStartY - 3, zLevel, borderColor, borderColor);
            drawGradientRect(textStartX - 3, textStartY + textHeight + 3, textStartX + maxLineLength + 3, textStartY + textHeight + 4, zLevel, borderColor, borderColor);
            drawGradientRect(textStartX - 3, textStartY - 3, textStartX + maxLineLength + 3, textStartY + textHeight + 3, zLevel, borderColor, borderColor);
            drawGradientRect(textStartX - 4, textStartY - 3, textStartX - 3, textStartY + textHeight + 3, zLevel, borderColor, borderColor);
            drawGradientRect(textStartX + maxLineLength + 3, textStartY - 3, textStartX + maxLineLength + 4, textStartY + textHeight + 3, zLevel, borderColor, borderColor);

            int fillColor1 = 0x505000FF;
            int fillColor2 = 0x5028007F;
            drawGradientRect(textStartX - 3, textStartY - 3 + 1, textStartX - 3 + 1, textStartY + textHeight + 3 - 1, zLevel, fillColor1, fillColor2);
            drawGradientRect(textStartX + maxLineLength + 2, textStartY - 3 + 1, textStartX + maxLineLength + 3, textStartY + textHeight + 3 - 1, zLevel, fillColor1, fillColor2);
            drawGradientRect(textStartX - 3, textStartY - 3, textStartX + maxLineLength + 3, textStartY - 3 + 1, zLevel, fillColor1, fillColor1);
            drawGradientRect(textStartX - 3, textStartY + textHeight + 2, textStartX + maxLineLength + 3, textStartY + textHeight + 3, zLevel, fillColor2, fillColor2);

            for (int i = 0; i < textLines.size(); ++i)
            {
                String str = textLines.get(i);
                font.drawStringWithShadow(str, textStartX, textStartY, 0xFFFFFFFF);
                textStartY += lineHeight;
            }

            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, double zLevel, int startColor, int endColor)
    {
        float sa = (float)(startColor >> 24 & 0xFF) / 255.0F;
        float sr = (float)(startColor >> 16 & 0xFF) / 255.0F;
        float sg = (float)(startColor >>  8 & 0xFF) / 255.0F;
        float sb = (float)(startColor & 0xFF) / 255.0F;

        float ea = (float)(endColor >> 24 & 0xFF) / 255.0F;
        float er = (float)(endColor >> 16 & 0xFF) / 255.0F;
        float eg = (float)(endColor >>  8 & 0xFF) / 255.0F;
        float eb = (float)(endColor & 0xFF) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        bufferbuilder.pos(right, top,    zLevel).color(sr, sg, sb, sa).endVertex();
        bufferbuilder.pos(left,  top,    zLevel).color(sr, sg, sb, sa).endVertex();
        bufferbuilder.pos(left,  bottom, zLevel).color(er, eg, eb, ea).endVertex();
        bufferbuilder.pos(right, bottom, zLevel).color(er, eg, eb, ea).endVertex();

        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableTexture2D();
    }

    public static void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        fontRendererIn.drawStringWithShadow(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color);
    }

    public static void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
        String[] parts = text.split("\\\\n");

        for (String line : parts)
        {
            fontRendererIn.drawStringWithShadow(line, x, y, color);
            y += fontRendererIn.FONT_HEIGHT + 1;
        }
    }

    public static void drawHorizontalLine(int x, int y, int width, int color)
    {
        drawRect(x, y, width, 1, color);
    }

    public static void drawVerticalLine(int x, int y, int height, int color)
    {
        drawRect(x, y, 1, height, color);
    }

    public static void renderSprite(Minecraft mc, int x, int y, String texture, int width, int height)
    {
        if (texture != null)
        {
            TextureAtlasSprite sprite = mc.getTextureMap().getAtlasSprite(texture);
            GlStateManager.disableLighting();
            mc.ingameGUI.drawTexturedModalRect(x, y, sprite, width, height);
        }
    }

    public static void renderText(int x, int y, int color, List<String> lines, FontRenderer font)
    {
        if (lines.isEmpty() == false)
        {
            for (String line : lines)
            {
                font.drawString(line, x, y, color);
                y += font.FONT_HEIGHT + 2;
            }
        }
    }

    public static int renderText(Minecraft mc, int xOff, int yOff, double scale, int textColor, int bgColor,
            HudAlignment alignment, boolean useBackground, boolean useShadow, List<String> lines)
    {
        FontRenderer fontRenderer = mc.fontRenderer;
        MainWindow window = mc.mainWindow;
        final int lineHeight = fontRenderer.FONT_HEIGHT + 2;
        final int bgMargin = 2;
        final int contentHeight = lines.size() * lineHeight - 2;
        double posX = xOff + bgMargin;
        double posY = yOff + bgMargin;

        // Only Chuck Norris can divide by zero
        if (scale == 0d)
        {
            return 0;
        }

        posY += getHudOffsetForPotions(alignment, scale, mc.player);
        posY = getHudPosY((int) posY, yOff, contentHeight, scale, alignment);

        if (scale != 1d)
        {
            GlStateManager.pushMatrix();
            GlStateManager.scaled(scale, scale, 0);
        }

        for (String line : lines)
        {
            final int width = fontRenderer.getStringWidth(line);

            switch (alignment)
            {
                case TOP_RIGHT:
                case BOTTOM_RIGHT:
                    posX = (window.getScaledWidth() / scale) - width - xOff - bgMargin;
                    break;
                case CENTER:
                    posX = (window.getScaledWidth() / scale / 2) - (width / 2) - xOff;
                    break;
                default:
            }

            final int x = (int) posX;
            final int y = (int) posY;
            posY += (double) lineHeight;

            if (useBackground)
            {
                drawRect(x - bgMargin, y - bgMargin, width + bgMargin, bgMargin + fontRenderer.FONT_HEIGHT, bgColor);
            }

            if (useShadow)
            {
                fontRenderer.drawStringWithShadow(line, x, y, textColor);
            }
            else
            {
                fontRenderer.drawString(line, x, y, textColor);
            }
        }

        if (scale != 1d)
        {
            GlStateManager.popMatrix();
        }

        return contentHeight;
    }

    public static int getHudOffsetForPotions(HudAlignment alignment, double scale, EntityPlayer player)
    {
        if (alignment == HudAlignment.TOP_RIGHT)
        {
            // Only Chuck Norris can divide by zero
            if (scale == 0d)
            {
                return 0;
            }

            Collection<PotionEffect> effects = player.getActivePotionEffects();

            if (effects.isEmpty() == false)
            {
                int y1 = 0;
                int y2 = 0;

                for (PotionEffect effect : effects)
                {
                    Potion potion = effect.getPotion();

                    if (effect.doesShowParticles() && potion.hasStatusIcon())
                    {
                        if (potion.isBeneficial())
                        {
                            y1 = 26;
                        }
                        else
                        {
                            y2 = 52;
                            break;
                        }
                    }
                }

                return (int) (Math.max(y1, y2) / scale);
            }
        }

        return 0;
    }

    public static int getHudPosY(int yOrig, int yOffset, int contentHeight, double scale, HudAlignment alignment)
    {
        MainWindow window = Minecraft.getInstance().mainWindow;
        int posY = yOrig;

        switch (alignment)
        {
            case BOTTOM_LEFT:
            case BOTTOM_RIGHT:
                posY = (int) (window.getScaledHeight() / scale - (contentHeight) - yOffset);
                break;
            case CENTER:
                posY = (int) ((window.getScaledHeight() / scale / 2.0d) - (contentHeight / 2.0d) + yOffset);
                break;
            default:
        }

        return posY;
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void drawBlockBoundingBoxSidesBatchedQuads(BlockPos pos, Color4f color, double expand, BufferBuilder buffer)
    {
        double minX = pos.getX() - expand;
        double minY = pos.getY() - expand;
        double minZ = pos.getZ() - expand;
        double maxX = pos.getX() + expand + 1;
        double maxY = pos.getY() + expand + 1;
        double maxZ = pos.getZ() + expand + 1;

        drawBoxAllSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
    }

    /**
     * Assumes a BufferBuilder in GL_LINES mode has been initialized
     */
    public static void drawBlockBoundingBoxOutlinesBatchedLines(BlockPos pos, Color4f color, double expand, BufferBuilder buffer)
    {
        double minX = pos.getX() - expand;
        double minY = pos.getY() - expand;
        double minZ = pos.getZ() - expand;
        double maxX = pos.getX() + expand + 1;
        double maxY = pos.getY() + expand + 1;
        double maxZ = pos.getZ() + expand + 1;

        drawBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void drawBoxAllSidesBatchedQuads(double minX, double minY, double minZ, double maxX, double maxY, double maxZ,
            Color4f color, BufferBuilder buffer)
    {
        drawBoxHorizontalSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
        drawBoxTopBatchedQuads(minX, minZ, maxX, maxY, maxZ, color, buffer);
        drawBoxBottomBatchedQuads(minX, minY, minZ, maxX, maxZ, color, buffer);
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void drawBoxHorizontalSidesBatchedQuads(double minX, double minY, double minZ, double maxX, double maxY, double maxZ,
            Color4f color, BufferBuilder buffer)
    {
        // West side
        buffer.pos(minX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();

        // East side
        buffer.pos(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();

        // North side
        buffer.pos(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();

        // South side
        buffer.pos(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void drawBoxTopBatchedQuads(double minX, double minZ, double maxX, double maxY, double maxZ, Color4f color, BufferBuilder buffer)
    {
        // Top side
        buffer.pos(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
    }

    /**
     * Assumes a BufferBuilder in GL_QUADS mode has been initialized
     */
    public static void drawBoxBottomBatchedQuads(double minX, double minY, double minZ, double maxX, double maxZ, Color4f color, BufferBuilder buffer)
    {
        // Bottom side
        buffer.pos(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
    }

    /**
     * Assumes a BufferBuilder in GL_LINES mode has been initialized
     */
    public static void drawBoxAllEdgesBatchedLines(double minX, double minY, double minZ, double maxX, double maxY, double maxZ,
            Color4f color, BufferBuilder buffer)
    {
        // West side
        buffer.pos(minX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();

        // East side
        buffer.pos(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();

        // North side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.pos(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, minY, minZ).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).endVertex();

        // South side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.pos(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).endVertex();
    }

    public static void drawBox(AxisAlignedBB bb, Color4f color, BufferBuilder bufferQuads, BufferBuilder bufferLines)
    {
        double minX = bb.minX;
        double minY = bb.minY;
        double minZ = bb.minZ;
        double maxX = bb.maxX + 1;
        double maxY = bb.maxY + 1;
        double maxZ = bb.maxZ + 1;

        drawBoxAllSidesBatchedQuads(minX, minY, minZ, maxX, maxY, maxZ, color, bufferQuads);
        drawBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, bufferLines);
    }

    public static void renderBlockTargetingOverlay(Entity entity, BlockPos pos, EnumFacing side, Vec3d hitVec,
            double dx, double dy, double dz, Color4f color)
    {
        EnumFacing playerFacing = entity.getHorizontalFacing();
        HitPart part = PositionUtils.getHitPart(side, playerFacing, pos, hitVec);

        double x = pos.getX() + 0.5d - dx;
        double y = pos.getY() + 0.5d - dy;
        double z = pos.getZ() + 0.5d - dz;

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);

        switch (side)
        {
            case DOWN:
                GlStateManager.rotatef(180f - playerFacing.getHorizontalAngle(), 0, 1f, 0);
                GlStateManager.rotatef( 90f, 1f, 0, 0);
                break;
            case UP:
                GlStateManager.rotatef(180f - playerFacing.getHorizontalAngle(), 0, 1f, 0);
                GlStateManager.rotatef(-90f, 1f, 0, 0);
                break;
            case NORTH:
                GlStateManager.rotatef(180f, 0, 1f, 0);
                break;
            case SOUTH:
                GlStateManager.rotatef(   0, 0, 1f, 0);
                break;
            case WEST:
                GlStateManager.rotatef(-90f, 0, 1f, 0);
                break;
            case EAST:
                GlStateManager.rotatef( 90f, 0, 1f, 0);
                break;
        }

        GlStateManager.translated(-x, -y, -z + 0.501);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float quadAlpha = 0.18f;
        float ha = color.a;
        float hr = color.r;
        float hg = color.g;
        float hb = color.b;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // White full block background
        buffer.pos(x - 0.5, y - 0.5, z).color(1f, 1f, 1f, quadAlpha).endVertex();
        buffer.pos(x + 0.5, y - 0.5, z).color(1f, 1f, 1f, quadAlpha).endVertex();
        buffer.pos(x + 0.5, y + 0.5, z).color(1f, 1f, 1f, quadAlpha).endVertex();
        buffer.pos(x - 0.5, y + 0.5, z).color(1f, 1f, 1f, quadAlpha).endVertex();

        switch (part)
        {
            case CENTER:
                buffer.pos(x - 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                break;
            case LEFT:
                buffer.pos(x - 0.50, y - 0.50, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.50, y + 0.50, z).color(hr, hg, hb, ha).endVertex();
                break;
            case RIGHT:
                buffer.pos(x + 0.50, y - 0.50, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.50, y + 0.50, z).color(hr, hg, hb, ha).endVertex();
                break;
            case TOP:
                buffer.pos(x - 0.50, y + 0.50, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y + 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.50, y + 0.50, z).color(hr, hg, hb, ha).endVertex();
                break;
            case BOTTOM:
                buffer.pos(x - 0.50, y - 0.50, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x - 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.25, y - 0.25, z).color(hr, hg, hb, ha).endVertex();
                buffer.pos(x + 0.50, y - 0.50, z).color(hr, hg, hb, ha).endVertex();
                break;
            default:
        }

        tessellator.draw();

        GlStateManager.lineWidth(1.6f);

        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);

        // Middle small rectangle
        buffer.pos(x - 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x + 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x + 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x - 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).endVertex();
        tessellator.draw();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        // Bottom left
        buffer.pos(x - 0.50, y - 0.50, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x - 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).endVertex();

        // Top left
        buffer.pos(x - 0.50, y + 0.50, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x - 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).endVertex();

        // Bottom right
        buffer.pos(x + 0.50, y - 0.50, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x + 0.25, y - 0.25, z).color(1f, 1f, 1f, 1f).endVertex();

        // Top right
        buffer.pos(x + 0.50, y + 0.50, z).color(1f, 1f, 1f, 1f).endVertex();
        buffer.pos(x + 0.25, y + 0.25, z).color(1f, 1f, 1f, 1f).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();
    }

    public static void renderMapPreview(ItemStack stack, int x, int y, int dimensions)
    {
        if (stack.getItem() instanceof ItemMap && GuiScreen.isShiftKeyDown())
        {
            Minecraft mc = Minecraft.getInstance();

            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.color4f(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(fi.dy.masa.malilib.render.RenderUtils.TEXTURE_MAP_BACKGROUND);

            int y1 = y - dimensions - 20;
            int y2 = y1 + dimensions;
            int x1 = x + 8;
            int x2 = x1 + dimensions;
            int z = 300;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            buffer.pos(x1, y2, z).tex(0.0D, 1.0D).endVertex();
            buffer.pos(x2, y2, z).tex(1.0D, 1.0D).endVertex();
            buffer.pos(x2, y1, z).tex(1.0D, 0.0D).endVertex();
            buffer.pos(x1, y1, z).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();

            MapData mapdata = ItemMap.getMapData(stack, mc.world);

            if (mapdata != null)
            {
                x1 += 8;
                y1 += 8;
                z = 310;
                double scale = (double) (dimensions - 16) / 128.0D;
                GlStateManager.translatef(x1, y1, z);
                GlStateManager.scaled(scale, scale, 0);
                mc.gameRenderer.getMapItemRenderer().renderMap(mapdata, false);
            }

            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }

    public static void renderShulkerBoxPreview(ItemStack stack, int x, int y, boolean useBgColors)
    {
        if (GuiScreen.isShiftKeyDown() && stack.hasTag())
        {
            NonNullList<ItemStack> items = InventoryUtils.getStoredItems(stack, -1);

            if (items.size() == 0)
            {
                return;
            }

            GlStateManager.pushMatrix();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.translatef(0F, 0F, 700F);

            InventoryOverlay.InventoryRenderType type = InventoryOverlay.getInventoryType(stack);
            InventoryOverlay.InventoryProperties props = InventoryOverlay.getInventoryPropsTemp(type, items.size());

            x += 8;
            y -= (props.height + 18);

            if (stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).getBlock() instanceof BlockShulkerBox)
            {
                setShulkerboxBackgroundTintColor((BlockShulkerBox) ((ItemBlock) stack.getItem()).getBlock(), useBgColors);
            }
            else
            {
                GlStateManager.color4f(1, 1, 1, 1);
            }

            Minecraft mc = Minecraft.getInstance();
            InventoryOverlay.renderInventoryBackground(type, x, y, props.slotsPerRow, items.size(), mc);

            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableDepthTest();
            GlStateManager.enableRescaleNormal();

            IInventory inv = fi.dy.masa.malilib.util.InventoryUtils.getAsInventory(items);
            InventoryOverlay.renderInventoryStacks(type, inv, x + props.slotOffsetX, y + props.slotOffsetY, props.slotsPerRow, 0, -1, mc);

            GlStateManager.disableDepthTest();
            GlStateManager.popMatrix();
        }
    }

    /**
     * Calls GlStateManager.color() with the dye color of the provided shulker box block's color
     * @param block
     * @param useBgColors
     */
    public static void setShulkerboxBackgroundTintColor(@Nullable BlockShulkerBox block, boolean useBgColors)
    {
        if (block != null && useBgColors)
        {
            final EnumDyeColor dye = block.getColor();
            final float[] colors = dye.getColorComponentValues();
            GlStateManager.color3f(colors[0], colors[1], colors[2]);
        }
        else
        {
            GlStateManager.color4f(1, 1, 1, 1);
        }
    }

    /*
    public static void enableGUIStandardItemLighting(float scale)
    {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);

        enableStandardItemLighting(scale);

        GlStateManager.popMatrix();
    }

    public static void enableStandardItemLighting(float scale)
    {
        GlStateManager.enableLighting();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        GlStateManager.enableColorMaterial();
        GlStateManager.colorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
        GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, RenderHelper.setColorBuffer((float) LIGHT0_POS.x, (float) LIGHT0_POS.y, (float) LIGHT0_POS.z, 0.0f));

        float lightStrength = 0.3F * scale;
        GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(lightStrength, lightStrength, lightStrength, 1.0F));
        GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_POSITION, RenderHelper.setColorBuffer((float) LIGHT1_POS.x, (float) LIGHT1_POS.y, (float) LIGHT1_POS.z, 0.0f));
        GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, RenderHelper.setColorBuffer(lightStrength, lightStrength, lightStrength, 1.0F));
        GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_AMBIENT, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
        GlStateManager.glLight(GL11.GL_LIGHT1, GL11.GL_SPECULAR, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));

        GlStateManager.shadeModel(GL11.GL_FLAT);

        float ambientLightStrength = 0.4F;
        GlStateManager.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, RenderHelper.setColorBuffer(ambientLightStrength, ambientLightStrength, ambientLightStrength, 1.0F));
    }
    */
}
