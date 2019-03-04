package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.util.KeyCodes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.SharedConstants;

public class WidgetSearchBar extends WidgetBase
{
    protected final WidgetIcon iconSearch;
    protected final LeftRight iconAlignment;
    protected final GuiTextFieldGeneric searchBox;
    protected boolean searchOpen;

    public WidgetSearchBar(int x, int y, int width, int height, float zLevel,
            int searchBarOffsetX, IGuiIcon iconSearch, LeftRight iconAlignment, Minecraft mc)
    {
        super(x, y, width, height, zLevel);

        int iw = iconSearch.getWidth();
        int ix = iconAlignment == LeftRight.RIGHT ? x + width - iw - 1 : x + 2;
        int tx = iconAlignment == LeftRight.RIGHT ? x - searchBarOffsetX + 3 : x + iw + 6 + searchBarOffsetX;
        this.iconSearch = new WidgetIcon(ix, y + 1, zLevel, iconSearch, mc);
        this.iconAlignment = iconAlignment;
        this.searchBox = new GuiTextFieldGeneric(tx, y, width - iw - 8 - Math.abs(searchBarOffsetX), height, mc.fontRenderer);
    }

    public String getFilter()
    {
        return this.searchOpen ? this.searchBox.getText() : "";
    }

    public boolean hasFilter()
    {
        return this.searchOpen && this.searchBox.getText().isEmpty() == false;
    }

    public boolean isSearchOpen()
    {
        return this.searchOpen;
    }

    public void setSearchOpen(boolean isOpen)
    {
        this.searchOpen = isOpen;

        if (this.searchOpen)
        {
            this.searchBox.setFocused(true);
        }
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.searchOpen && this.searchBox.mouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }
        else if (this.iconSearch.isMouseOver(mouseX, mouseY))
        {
            this.setSearchOpen(! this.searchOpen);
            return true;
        }

        return false;
    }

    @Override
    protected boolean onKeyTypedImpl(int keyCode, int scanCode, int modifiers)
    {
        if (this.searchOpen)
        {
            if (this.searchBox.keyPressed(keyCode, scanCode, modifiers))
            {
                return true;
            }
            else if (keyCode == KeyCodes.KEY_ESCAPE)
            {
                if (GuiScreen.isShiftKeyDown())
                {
                    this.mc.displayGuiScreen(null);
                }

                this.searchOpen = false;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onCharTypedImpl(char charIn, int modifiers)
    {
        if (this.searchOpen)
        {
            return this.searchBox.charTyped(charIn, modifiers);
        }
        /*else if (SharedConstants.isAllowedCharacter(charIn))
        {
            this.searchOpen = true;
            this.searchBox.setFocused(true);
            this.searchBox.setText("");
            this.searchBox.setCursorPositionEnd();
            this.searchBox.charTyped(charIn, modifiers);
            return true;
        }*/

        return false;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        GlStateManager.color4f(1f, 1f, 1f, 1f);
        this.iconSearch.render(false, this.iconSearch.isMouseOver(mouseX, mouseY));

        if (this.searchOpen)
        {
            this.searchBox.drawTextField(mouseX, mouseY, 0);
        }
    }
}
