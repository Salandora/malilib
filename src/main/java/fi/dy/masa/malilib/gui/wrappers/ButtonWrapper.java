package fi.dy.masa.malilib.gui.wrappers;

import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import net.minecraft.client.Minecraft;

public class ButtonWrapper<T extends ButtonBase>
{
    private final T button;
    private final IButtonActionListener<T> listener;
    
    public ButtonWrapper(T button, IButtonActionListener<T> listener)
    {
        this.button = button;
        this.listener = listener;
    }

    public T getButton()
    {
        return this.button;
    }

    public IButtonActionListener<T> getListener()
    {
        return this.listener;
    }

    public void draw(Minecraft minecraft, int mouseX, int mouseY, float partialTicks)
    {
        this.button.render(mouseX, mouseY, partialTicks);
    }

    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY, int mouseButton)
    {
        if (this.button.isMouseOver(mouseX, mouseY))
        {
            this.button.onMouseButtonClicked(mouseButton);

            if (this.listener != null)
            {
                this.listener.actionPerformedWithButton(this.button, mouseButton);
            }

            return true;
        }

        return false;
    }

    public void mouseReleased(Minecraft minecraft, int mouseX, int mouseY, int mouseButton)
    {
        this.button.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public boolean onMouseScrolled(Minecraft mc, int mouseX, int mouseY, int mouseWheelDelta)
    {
        if (this.button.isMouseOver(mouseX, mouseY))
        {
            int mouseButton = mouseWheelDelta < 0 ? 1 : 0;
            this.button.onMouseButtonClicked(mouseButton);

            if (this.listener != null)
            {
                this.listener.actionPerformedWithButton(this.button, mouseButton);
            }

            return true;
        }

        return false;
    }
}
