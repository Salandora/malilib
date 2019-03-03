package fi.dy.masa.malilib.gui.widgets;

import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import fi.dy.masa.malilib.hotkeys.KeybindSettings.Context;
import fi.dy.masa.malilib.util.KeyCodes;
import net.minecraft.client.Minecraft;

public class WidgetSearchBarConfigs extends WidgetSearchBar
{
    protected final KeybindMulti searchKey;
    protected final ConfigButtonKeybind button;

    public WidgetSearchBarConfigs(int x, int y, int width, int height, float zLevel, int searchBarOffsetX,
            IGuiIcon iconSearch, LeftRight iconAlignment, Minecraft mc)
    {
        super(x, y + 3, width - 160, 14, zLevel, searchBarOffsetX, iconSearch, iconAlignment, mc);

        KeybindSettings settings = KeybindSettings.create(Context.ANY, KeyAction.BOTH, true, true, false, false, false);
        this.searchKey = KeybindMulti.fromStorageString("", settings);
        this.button = new ConfigButtonKeybind(x + width - 150, y, 140, 20, this.searchKey, null);
    }

    public IKeybind getKeybind()
    {
        return this.searchKey;
    }

    @Override
    public boolean hasFilter()
    {
        return super.hasFilter() || (this.searchOpen && this.searchKey.getKeys().size() > 0);
    }

    @Override
    protected boolean onMouseClickedImpl(int mouseX, int mouseY, int mouseButton)
    {
        if (this.searchOpen)
        {
            if (this.button.mouseClicked(mouseX, mouseY, mouseButton))
            {
                boolean selectedPre = this.button.isSelected();
                this.button.onMouseButtonClicked(mouseButton);

                if (selectedPre == false)
                {
                    this.button.onSelected();
                }

                return true;
            }
            else if (this.button.isSelected())
            {
                this.button.onClearSelection();
                return true;
            }
        }

        return super.onMouseClickedImpl(mouseX, mouseY, mouseButton);
    }

    @Override
    protected boolean onKeyTypedImpl(int keyCode, int scanCode, int modifiers)
    {
        if (this.searchOpen && this.button.isSelected())
        {
            this.button.onKeyPressed(keyCode);

            if (keyCode == KeyCodes.KEY_ESCAPE)
            {
                this.button.onClearSelection();
            }

            return true;
        }

        return super.onKeyTypedImpl(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected)
    {
        super.render(mouseX, mouseY, selected);

        if (this.searchOpen)
        {
            this.button.render(mouseX, mouseY, 0);
        }
    }
}
