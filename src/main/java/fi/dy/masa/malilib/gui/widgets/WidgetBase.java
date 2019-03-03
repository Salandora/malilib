package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.wrappers.ButtonWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;

public abstract class WidgetBase
{
    protected final Minecraft mc;
    protected final int x;
    protected final int y;
    protected final int width;
    protected final int height;
    protected final float zLevel;
    protected final List<WidgetBase> subWidgets = new ArrayList<>();
    protected final List<ButtonWrapper<?>> buttons = new ArrayList<>();
    @Nullable
    protected WidgetBase hoveredSubWidget = null;

    public WidgetBase(int x, int y, int width, int height, float zLevel)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zLevel = zLevel;
        this.mc = Minecraft.getMinecraft();
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return mouseX >= this.x && mouseX < this.x + this.width &&
               mouseY >= this.y && mouseY < this.y + this.height;
    }

    public final boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        boolean handled = false;

        if (this.isMouseOver(mouseX, mouseY))
        {
            if (this.subWidgets.isEmpty() == false)
            {
                for (WidgetBase widget : this.subWidgets)
                {
                    if (widget.isMouseOver(mouseX, mouseY) && widget.onMouseClicked(mouseX, mouseY, mouseButton))
                    {
                        // Don't call super if the button press got handled
                        handled = true;
                    }
                }
            }

            if (handled == false)
            {
                handled = this.onMouseClickedImpl(mouseX, mouseY, mouseButton);
            }
        }

        return handled;
    }

    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        for (ButtonWrapper<?> entry : this.buttons)
        {
            if (entry.mousePressed(this.mc, mouseX, mouseY, mouseButton))
            {
                // Don't call super if the button press got handled
                return true;
            }
        }

        return false;
    }

    public final boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        boolean handled = false;

        if (this.subWidgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.subWidgets)
            {
                if (widget.onKeyTyped(keyCode, scanCode, modifiers))
                {
                    // Don't call super if the key press got handled
                    handled = true;
                }
            }
        }

        if (handled == false)
        {
            handled = this.onKeyTypedImpl(keyCode, scanCode, modifiers);
        }

        return handled;
    }

    protected boolean onKeyTypedImpl(int keyCode, int scanCode, int modifiers)
    {
        return false;
    }

    public final boolean onCharTyped(char charIn, int modifiers)
    {
        boolean handled = false;

        if (this.subWidgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.subWidgets)
            {
                if (widget.onCharTyped(charIn, modifiers))
                {
                    // Don't call super if the key press got handled
                    handled = true;
                }
            }
        }

        if (handled == false)
        {
            handled = this.onCharTypedImpl(charIn, modifiers);
        }

        return handled;
    }

    protected boolean onCharTypedImpl(char charIn, int modifiers)
    {
        return false;
    }

    /**
     * Returns true if this widget can be selected by clicking at the given point
     */
    public boolean canSelectAt(int mouseX, int mouseY, int mouseButton)
    {
        return this.isMouseOver(mouseX, mouseY);
    }

    protected void addWidget(WidgetBase widget)
    {
        this.subWidgets.add(widget);
    }

    protected <T extends ButtonBase> void addButton(T button, IButtonActionListener<T> listener)
    {
        this.buttons.add(new ButtonWrapper<>(button, listener));
    }

    protected void addLabel(int x, int y, int width, int height, int textColor, String... lines)
    {
        if (lines != null && lines.length >= 1)
        {
            Minecraft mc = Minecraft.getInstance();

            if (width == -1)
            {
                for (String line : lines)
                {
                    width = Math.max(width, mc.fontRenderer.getStringWidth(line));
                }
            }

            WidgetLabel label = new WidgetLabel(x, y, width, height, this.zLevel, textColor, lines);
            this.addWidget(label);
        }
    }

    public void render(int mouseX, int mouseY, boolean selected)
    {
        for (int i = 0; i < this.buttons.size(); ++i)
        {
            this.buttons.get(i).draw(this.mc, mouseX, mouseY, 0);
        }
    }

    public void postRenderHovered(int mouseX, int mouseY, boolean selected)
    {
        this.drawHoveredSubWidget(mouseX, mouseY);
    }

    protected void drawSubWidgets(int mouseX, int mouseY)
    {
        this.hoveredSubWidget = null;

        if (this.subWidgets.isEmpty() == false)
        {
            for (WidgetBase widget : this.subWidgets)
            {
                widget.render(mouseX, mouseY, false);

                if (widget.isMouseOver(mouseX, mouseY))
                {
                    this.hoveredSubWidget = widget;
                }
            }
        }
    }

    protected void drawHoveredSubWidget(int mouseX, int mouseY)
    {
        if (this.hoveredSubWidget != null)
        {
            this.hoveredSubWidget.postRenderHovered(mouseX, mouseY, false);

            RenderHelper.disableStandardItemLighting();
        }
    }
}
