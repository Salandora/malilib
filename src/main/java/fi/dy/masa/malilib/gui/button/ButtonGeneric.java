package fi.dy.masa.malilib.gui.button;

import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class ButtonGeneric extends ButtonBase
{
    protected static final ResourceLocation BUTTON_TEXTURES = new ResourceLocation("minecraft", "textures/gui/widgets.png");

    @Nullable
    protected final IGuiIcon icon;
    protected LeftRight alignment = LeftRight.LEFT;
    protected boolean textCentered;
    protected boolean renderDefaultBackground = true;

    public ButtonGeneric(int x, int y, int width, boolean rightAlign, String translationKey, Object... args)
    {
        this(x, y, width, 20, I18n.format(translationKey, args));

        if (rightAlign)
        {
            this.x = x - this.width;
        }
    }

    public ButtonGeneric(int x, int y, int width, int height, String text, String... hoverStrings)
    {
        this(x, y, width, height, text, null, hoverStrings);

        this.textCentered = true;
    }

    public ButtonGeneric(int x, int y, int width, int height, String text, IGuiIcon icon, String... hoverStrings)
    {
        super(x, y, width, height, text);

        this.icon = icon;

        if (width == -1 && icon != null)
        {
            this.width += icon.getWidth() + 8;
        }

        if (hoverStrings.length > 0)
        {
            this.setHoverStrings(hoverStrings);
        }
    }

    public ButtonGeneric(int x, int y, IGuiIcon icon, String... hoverStrings)
    {
        this(x, y, icon.getWidth(), icon.getHeight(), "", icon, hoverStrings);

        this.setRenderDefaultBackground(false);
    }

    @Override
    public ButtonGeneric setActionListener(@Nullable IButtonActionListener actionListener)
    {
        this.actionListener = actionListener;
        return this;
    }

    public ButtonGeneric setTextCentered(boolean centered)
    {
        this.textCentered = centered;
        return this;
    }

    /**
     * Set the icon aligment.<br>
     * Note: Only LEFT and RIGHT alignments work properly.
     * @param alignment
     * @return
     */
    public ButtonGeneric setIconAlignment(LeftRight alignment)
    {
        this.alignment = alignment;
        return this;
    }

    public ButtonGeneric setRenderDefaultBackground(boolean render)
    {
        this.renderDefaultBackground = render;
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        if (this.visible)
        {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            int buttonStyle = this.getTextureOffset(this.hovered);

            GlStateManager.color4f(1f, 1f, 1f, 1f);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            if (this.renderDefaultBackground)
            {
                this.bindTexture(BUTTON_TEXTURES);
                RenderUtils.drawTexturedRect(this.x, this.y, 0, 46 + buttonStyle * 20, this.width / 2, this.height);
                RenderUtils.drawTexturedRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + buttonStyle * 20, this.width / 2, this.height);
            }

            if (this.icon != null)
            {
                int offset = this.renderDefaultBackground ? 4 : 0;
                int x = this.alignment == LeftRight.LEFT ? this.x + offset : this.x + this.width - this.icon.getWidth() - offset;
                int y = this.y + (this.height - this.icon.getHeight()) / 2;
                int u = this.icon.getU() + buttonStyle * this.icon.getWidth();

                this.bindTexture(this.icon.getTexture());
                RenderUtils.drawTexturedRect(x, y, u, this.icon.getV(), this.icon.getWidth(), this.icon.getHeight());
            }

            if (StringUtils.isBlank(this.displayString) == false)
            {
                int y = this.y + (this.height - 8) / 2;
                int color = 0xE0E0E0;

                if (this.enabled == false)
                {
                    color = 0xA0A0A0;
                }
                else if (this.hovered)
                {
                    color = 0xFFFFA0;
                }

                if (this.textCentered)
                {
                    RenderUtils.drawCenteredString(this.textRenderer, this.displayString, this.x + this.width / 2, y, color);
                }
                else
                {
                    int x = this.x + 6;

                    if (this.icon != null && this.alignment == LeftRight.LEFT)
                    {
                        x += this.icon.getWidth() + 2;
                    }

                    this.drawStringWithShadow(this.displayString, x, y, color);
                }
            }
        }
    }
}
