package fi.dy.masa.malilib.config.gui;

import javax.annotation.Nullable;
import fi.dy.masa.malilib.config.IConfigResettable;
import fi.dy.masa.malilib.config.IStringRepresentable;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import net.minecraft.client.gui.GuiTextField;

public class ConfigOptionListenerResetConfig implements IButtonActionListener<ButtonGeneric>
{
    private final IConfigResettable config;
    private final ButtonGeneric buttonReset;
    @Nullable private final ConfigResetterBase reset;
    @Nullable private final ButtonPressDirtyListenerSimple<ButtonBase> dirtyListener;

    public ConfigOptionListenerResetConfig(IConfigResettable config, @Nullable ConfigResetterBase reset,
            ButtonGeneric buttonReset, @Nullable ButtonPressDirtyListenerSimple<ButtonBase> dirtyListener)
    {
        this.config = config;
        this.reset = reset;
        this.buttonReset = buttonReset;
        this.dirtyListener = dirtyListener;
    }

    @Override
    public void actionPerformed(ButtonGeneric control)
    {
        this.config.resetToDefault();
        this.buttonReset.enabled = this.config.isModified();

        if (this.reset != null)
        {
            this.reset.resetConfigOption();
        }
    }

    @Override
    public void actionPerformedWithButton(ButtonGeneric control, int mouseButton)
    {
        this.actionPerformed(control);

        if (this.dirtyListener != null)
        {
            this.dirtyListener.actionPerformedWithButton(control, mouseButton);
        }
    }

    public abstract static class ConfigResetterBase
    {
        public abstract void resetConfigOption();
    }

    public static class ConfigResetterButton extends ConfigResetterBase
    {
        private final ButtonBase button;

        public ConfigResetterButton(ButtonBase button)
        {
            this.button = button;
        }

        @Override
        public void resetConfigOption()
        {
            this.button.updateDisplayString();
        }
    }

    public static class ConfigResetterTextField extends ConfigResetterBase
    {
        private final IStringRepresentable config;
        private final GuiTextField textField;

        public ConfigResetterTextField(IStringRepresentable config, GuiTextField textField)
        {
            this.config = config;
            this.textField = textField;
        }

        @Override
        public void resetConfigOption()
        {
            this.textField.setText(this.config.getStringValue());
        }
    }
}
