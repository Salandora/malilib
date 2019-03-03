package fi.dy.masa.malilib.gui;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public abstract class GuiListBase<TYPE, WIDGET extends WidgetListEntryBase<TYPE>, WIDGETLIST extends WidgetListBase<TYPE, WIDGET>> extends GuiBase
{
    private final int listX;
    private final int listY;
    private WIDGETLIST widget;

    protected GuiListBase(int listX, int listY)
    {
        this.listX = listX;
        this.listY = listY;
    }

    protected abstract WIDGETLIST createListWidget(int listX, int listY);

    protected abstract int getBrowserWidth();

    protected abstract int getBrowserHeight();

    @Nullable
    protected ISelectionListener<TYPE> getSelectionListener()
    {
        return null;
    }

    protected WIDGETLIST getListWidget()
    {
        if (this.widget == null)
        {
            this.reCreateListWidget();
        }

        return this.widget;
    }

    protected void reCreateListWidget()
    {
        this.widget = this.createListWidget(this.listX, this.listY);
    }

    @Override
    public GuiBase setParent(GuiScreen parent)
    {
        return super.setParent(parent);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.getListWidget().setSize(this.getBrowserWidth(), this.getBrowserHeight());
        this.getListWidget().initGui();
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        this.getListWidget().onGuiClosed();
    }

    @Override
    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (this.getListWidget().onMouseClicked(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        return super.onMouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        if (this.getListWidget().onMouseReleased(mouseX, mouseY, mouseButton))
        {
            return true;
        }

        return super.onMouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean onMouseScrolled(int mouseX, int mouseY, int mouseWheelDelta)
    {
        if (this.getListWidget().onMouseScrolled(mouseX, mouseY, mouseWheelDelta))
        {
            return true;
        }

        return super.onMouseScrolled(mouseX, mouseY, mouseWheelDelta);
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers)
    {
        if (this.getListWidget().onKeyTyped(keyCode, scanCode, modifiers))
        {
            return true;
        }

        return super.onKeyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onCharTyped(char charIn, int modifiers)
    {
        if (this.getListWidget().onCharTyped(charIn, modifiers))
        {
            return true;
        }

        return super.onCharTyped(charIn, modifiers);
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        super.setWorldAndResolution(mc, width, height);

        this.getListWidget().setWorldAndResolution(mc, width, height);
    }

    @Override
    public void drawContents(int mouseX, int mouseY, float partialTicks)
    {
        this.getListWidget().drawContents(mouseX, mouseY, partialTicks);
    }
}
