package fi.dy.masa.malilib.gui.widgets;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiScrollBar;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.util.KeyCodes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

public abstract class WidgetListBase<TYPE, WIDGET extends WidgetListEntryBase<TYPE>> extends GuiBase
{
    protected final List<TYPE> listContents = new ArrayList<>();
    protected final List<WIDGET> listWidgets = new ArrayList<>();
    protected final GuiScrollBar scrollBar = new GuiScrollBar();
    protected final int posX;
    protected final int posY;
    protected int totalWidth;
    protected int totalHeight;
    protected int browserWidth;
    protected int browserHeight;
    protected int entryHeight;
    protected int browserEntriesStartX;
    protected int browserEntriesStartY;
    protected int browserEntriesOffsetY;
    protected int browserEntryWidth;
    protected int browserEntryHeight;
    protected int browserPaddingX;
    protected int browserPaddingY;
    protected int maxVisibleBrowserEntries;
    protected int selectedEntryIndex = -1;
    protected int lastScrollbarPosition = -1;
    @Nullable protected TYPE selectedEntry;
    @Nullable protected final ISelectionListener<TYPE> selectionListener;

    public WidgetListBase(int x, int y, int width, int height, @Nullable ISelectionListener<TYPE> selectionListener)
    {
        this.mc = Minecraft.getInstance();
        this.posX = x;
        this.posY = y;
        this.selectionListener = selectionListener;
        this.browserEntryHeight = 14;

        this.setSize(width, height);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.mc.keyboardListener.enableRepeatEvents(true);
        this.refreshEntries();
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0 && this.scrollBar.wasMouseOver())
        {
            this.scrollBar.setIsDragging(true);
            return true;
        }

        final int relativeY = mouseY - this.browserEntriesStartY - this.browserEntriesOffsetY;

        if (relativeY >= 0 &&
            mouseX >= this.browserEntriesStartX &&
            mouseX < this.browserEntriesStartX + this.browserEntryWidth)
        {
            for (int i = 0; i < this.listWidgets.size(); ++i)
            {
                WIDGET widget = this.listWidgets.get(i);

                if (widget.isMouseOver(mouseX, mouseY))
                {
                    if (widget.canSelectAt(mouseX, mouseY, mouseButton))
                    {
                        int entryIndex = widget.getListIndex();

                        if (entryIndex >= 0 && entryIndex < this.listContents.size())
                        {
                            this.onEntryClicked(this.listContents.get(entryIndex), entryIndex);
                        }
                    }

                    return widget.onMouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            this.scrollBar.setIsDragging(false);
        }

        return super.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseScrolled(int mouseX, int mouseY, int mouseWheelDelta)
    {
        if (mouseX >= this.posX && mouseX <= this.posX + this.browserWidth &&
            mouseY >= this.posY && mouseY <= this.posY + this.browserHeight)
        {
            this.offsetSelectionOrScrollbar(mouseWheelDelta < 0 ? 3 : -3, false);
            return true;
        }

        return false;
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == KeyCodes.KEY_UP)             this.offsetSelectionOrScrollbar(-1, true);
        else if (keyCode == KeyCodes.KEY_DOWN)      this.offsetSelectionOrScrollbar( 1, true);
        else if (keyCode == KeyCodes.KEY_PAGE_UP)   this.offsetSelectionOrScrollbar(-this.maxVisibleBrowserEntries / 2, true);
        else if (keyCode == KeyCodes.KEY_PAGE_DOWN) this.offsetSelectionOrScrollbar( this.maxVisibleBrowserEntries / 2, true);
        else if (keyCode == KeyCodes.KEY_HOME)      this.offsetSelectionOrScrollbar(-this.listContents.size(), true);
        else if (keyCode == KeyCodes.KEY_END)       this.offsetSelectionOrScrollbar( this.listContents.size(), true);
        else return false;

        return true;
    }

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        for (WIDGET widget : this.listWidgets)
        {
            if (widget.onCharTyped(charIn, modifiers))
            {
                return true;
            }
        }

        return super.onCharTyped(charIn, modifiers);
    }

    @Override
    public void drawContents(int mouseX, int mouseY, float partialTicks)
    {
        WIDGET hovered = null;
        boolean hoveredSelected = false;
        int scrollbarHeight = this.browserHeight - 8;
        int totalHeight = 0;

        for (int i = 0; i < this.listContents.size(); ++i)
        {
            totalHeight += this.getBrowserEntryHeightFor(this.listContents.get(i));
        }

        totalHeight = Math.max(totalHeight, scrollbarHeight);

        this.scrollBar.render(mouseX, mouseY, partialTicks,
                this.posX + this.browserWidth - 9, this.browserEntriesStartY, 8, scrollbarHeight, totalHeight);

        // The value gets updated in the drawScrollBar() method above, if dragging
        if (this.scrollBar.getValue() != this.lastScrollbarPosition)
        {
            this.lastScrollbarPosition = this.scrollBar.getValue();
            this.reCreateListEntryWidgets();
        }

        // Draw the currently visible directory entries
        for (int i = 0; i < this.listWidgets.size(); i++)
        {
            WIDGET widget = this.listWidgets.get(i);
            boolean isSelected = widget.getEntry() == this.selectedEntry;
            widget.render(mouseX, mouseY, isSelected);

            if (widget.isMouseOver(mouseX, mouseY))
            {
                hovered = widget;
                hoveredSelected = isSelected;
            }
        }

        if (hovered != null)
        {
            hovered.postRenderHovered(mouseX, mouseY, hoveredSelected);
        }

        GlStateManager.disableLighting();
        GlStateManager.color4f(1f, 1f, 1f, 1f);
    }

    public void setSize(int width, int height)
    {
        this.totalWidth = width;
        this.totalHeight = height;
        this.browserWidth = width;
        this.browserHeight = height;
        this.browserPaddingX = 3;
        this.browserPaddingY = 4;
        this.browserEntriesStartX = this.posX + this.browserPaddingX;
        this.browserEntriesStartY = this.posY + this.browserPaddingY;
        this.browserEntryWidth = this.browserWidth - 14;
    }

    protected int getBrowserEntryHeightFor(@Nullable TYPE type)
    {
        return this.browserEntryHeight;
    }

    protected void reCreateListEntryWidgets()
    {
        this.listWidgets.clear();
        this.maxVisibleBrowserEntries = 0;

        final int numEntries = this.listContents.size();
        int usableHeight = this.browserHeight - this.browserPaddingY - this.browserEntriesOffsetY;
        int usedHeight = 0;
        int x = this.posX + 2;
        int y = this.posY + 4 + this.browserEntriesOffsetY;
        int index = this.scrollBar.getValue();
        WIDGET widget = this.createHeaderWidget(x, y, index, usableHeight, usedHeight);

        if (widget != null)
        {
            this.listWidgets.add(widget);
            //this.maxVisibleBrowserEntries++;

            usedHeight += widget.getHeight();
            y += widget.getHeight();
        }

        for ( ; index < numEntries; ++index)
        {
            widget = this.createListEntryWidgetIfSpace(x, y, index, usableHeight, usedHeight);

            if (widget == null)
            {
                break;
            }

            this.listWidgets.add(widget);
            this.maxVisibleBrowserEntries++;

            usedHeight += widget.getHeight();
            y += widget.getHeight();
        }

        this.scrollBar.setMaxValue(this.listContents.size() - this.maxVisibleBrowserEntries);
    }

    @Nullable
    protected WIDGET createListEntryWidgetIfSpace(int x, int y, int listIndex, int usableHeight, int usedHeight)
    {
        TYPE entry = this.listContents.get(listIndex);
        int height = this.getBrowserEntryHeightFor(entry);

        if ((usedHeight + height) > usableHeight)
        {
            return null;
        }

        return this.createListEntryWidget(x, y, listIndex, (listIndex & 0x1) != 0, entry);
    }

    /**
     * Create a header widget, that will always be displayed as the first entry of the list.
     * If no such header should be used, then return null,
     * @param x
     * @param y
     * @param listIndexStart the listContents index of the first visible entry
     * @param usableHeight the total usable height available for the list entry widgets
     * @param usedHeight the currently used up height. Check that (usedHeight + widgetHeight) <= usableHeight before adding an entry widget.
     * @return the created header widget, or null if there is no separate header widget
     */
    @Nullable
    protected WIDGET createHeaderWidget(int x, int y, int listIndexStart, int usableHeight, int usedHeight)
    {
        return null;
    }

    public abstract void refreshEntries();

    protected abstract WIDGET createListEntryWidget(int x, int y, int listIndex, boolean isOdd, TYPE entry);

    @Nullable
    public TYPE getSelectedEntry()
    {
        return this.selectedEntry;
    }

    protected boolean onEntryClicked(@Nullable TYPE entry, int index)
    {
        this.setSelectedEntry(entry, index);
        return true;
    }

    public void setSelectedEntry(@Nullable TYPE entry, int index)
    {
        this.selectedEntry = entry;
        this.selectedEntryIndex = index;

        if (entry != null && this.selectionListener != null)
        {
            this.selectionListener.onSelectionChange(entry);
        }
    }

    public void clearSelection()
    {
        this.setSelectedEntry(null, -1);
    }

    protected void offsetSelectionOrScrollbar(int amount, boolean changeSelection)
    {
        if (changeSelection == false)
        {
            this.scrollBar.offsetValue(amount);
        }
        else if (this.selectedEntryIndex >= 0 && this.listContents.size() > 0)
        {
            int index = MathHelper.clamp(this.selectedEntryIndex + amount, 0, this.listContents.size() - 1);

            if (index != this.selectedEntryIndex)
            {
                if (index < this.scrollBar.getValue() || index >= this.scrollBar.getValue() + this.maxVisibleBrowserEntries)
                {
                    this.scrollBar.offsetValue(index - this.selectedEntryIndex);
                }

                this.setSelectedEntry(this.listContents.get(index), index);
            }
        }
        else
        {
            this.scrollBar.offsetValue(amount);

            int index = this.scrollBar.getValue();

            if (index >= 0 && index < this.listContents.size())
            {
                this.setSelectedEntry(this.listContents.get(index), index);
            }
        }

        this.reCreateListEntryWidgets();
    }

    public void resetScrollbarPosition()
    {
        this.scrollBar.setValue(0);
    }
}
