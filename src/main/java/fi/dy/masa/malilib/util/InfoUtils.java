package fi.dy.masa.malilib.util;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import fi.dy.masa.malilib.interfaces.IStringConsumer;
import fi.dy.masa.malilib.render.MessageRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentTranslation;

import static fi.dy.masa.malilib.util.InfoType.MESSAGE_OVERLAY;

public class InfoUtils
{
    private static final MessageRenderer IN_GAME_MESSAGES = new MessageRenderer(0xA0000000, 0).setBackgroundStyle(true, false).setCentered(true, false).setExpandUp(true);

    public static final IStringConsumer INFO_MESSAGE_CONSUMER = new InfoMessageConsumer();
    public static final IMessageConsumer INGAME_MESSAGE_CONSUMER = new InGameMessageConsumer();


    public static void showMessage(InfoType infoType, MessageType messageType, String translationKey, Object... args)
    {
        showMessage(infoType, messageType, 5000, translationKey, args);
    }

    public static void showMessage(InfoType infoType, MessageType messageType, int lifeTime, String translationKey, Object... args)
    {
        switch (infoType)
        {
            case MESSAGE_OVERLAY:
            {
                showGuiMessage(messageType, lifeTime, translationKey, args);
                break;
            }
            case INGAME_MESSAGE:
            {
                showGuiOrInGameMessage(messageType, lifeTime, translationKey, args);
                break;
            }
            case ACTION_BAR:
            {
                showGuiOrActionBarMessage(messageType, lifeTime, translationKey, args);
                break;
            }
        }
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open.
     * @param type
     * @param translationKey
     * @param args
     */
    public static void showGuiMessage(MessageType type, String translationKey, Object... args)
    {
        showGuiMessage(type, 5000, translationKey, args);
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open.
     * @param type
     * @param lifeTime
     * @param translationKey
     * @param args
     */
    public static void showGuiMessage(MessageType type, int lifeTime, String translationKey, Object... args)
    {
        if (GuiUtils.getCurrentScreen() instanceof IMessageConsumer)
        {
            ((IMessageConsumer) GuiUtils.getCurrentScreen()).addMessage(type, lifeTime, translationKey, args);
        }
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open. Otherwise prints the message to the action bar.
     * @param type
     * @param translationKey
     * @param args
     */
    public static void showGuiOrActionBarMessage(MessageType type, String translationKey, Object... args)
    {
        showGuiOrActionBarMessage(type, 5000, translationKey, args);
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open. Otherwise prints the message to the action bar.
     * @param type
     * @param lifeTime
     * @param translationKey
     * @param args
     */
    public static void showGuiOrActionBarMessage(MessageType type, int lifeTime, String translationKey, Object... args)
    {
        if (GuiUtils.getCurrentScreen() instanceof IMessageConsumer)
        {
            ((IMessageConsumer) GuiUtils.getCurrentScreen()).addMessage(type, lifeTime, translationKey, args);
        }
        else
        {
            String msg = type.getFormatting() + StringUtils.translate(translationKey, args) + GuiBase.TXT_RST;
            printActionbarMessage(msg);
        }
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open. Otherwise adds the message to the in-game message handler.
     * @param type
     * @param translationKey
     * @param args
     */
    public static void showGuiOrInGameMessage(MessageType type, String translationKey, Object... args)
    {
        showGuiOrInGameMessage(type, 5000, translationKey, args);
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open. Otherwise adds the message to the in-game message handler.
     * @param type
     * @param lifeTime
     * @param translationKey
     * @param args
     */
    public static void showGuiOrInGameMessage(MessageType type, int lifeTime, String translationKey, Object... args)
    {
        if (GuiUtils.getCurrentScreen() instanceof IMessageConsumer)
        {
            ((IMessageConsumer) GuiUtils.getCurrentScreen()).addMessage(type, lifeTime, translationKey, args);
        }
        else
        {
            showInGameMessage(type, lifeTime, translationKey, args);
        }
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open.
     * Also shows the message in the in-game message box.
     * @param type
     * @param translationKey
     * @param args
     */
    public static void showGuiAndInGameMessage(MessageType type, String translationKey, Object... args)
    {
        showGuiAndInGameMessage(type, 5000, translationKey, args);
    }

    /**
     * Adds the message to the current GUI's message handler, if there is currently
     * an IMessageConsumer GUI open.
     * Also shows the message in the in-game message box.
     * @param type
     * @param lifeTime
     * @param translationKey
     * @param args
     */
    public static void showGuiAndInGameMessage(MessageType type, int lifeTime, String translationKey, Object... args)
    {
        showGuiMessage(type, lifeTime, translationKey, args);
        showInGameMessage(type, lifeTime, translationKey, args);
    }

    public static void printActionbarMessage(String key, Object... args)
    {
        Minecraft.getInstance().ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentTranslation(key, args));
    }

    /**
     * Adds the message to the in-game message handler
     * @param type
     * @param translationKey
     * @param args
     */
    public static void showInGameMessage(MessageType type, String translationKey, Object... args)
    {
        showInGameMessage(type, 5000, translationKey, args);
    }

    /**
     * Adds the message to the in-game message handler
     * @param type
     * @param lifeTime
     * @param translationKey
     * @param args
     */
    public static void showInGameMessage(MessageType type, int lifeTime, String translationKey, Object... args)
    {
        synchronized (IN_GAME_MESSAGES)
        {
            IN_GAME_MESSAGES.addMessage(type, lifeTime, translationKey, args);
        }
    }

    public static void printBooleanConfigToggleMessage(String prettyName, boolean newValue)
    {
        String pre = newValue ? GuiBase.TXT_GREEN : GuiBase.TXT_RED;
        String status = StringUtils.translate("malilib.message.value." + (newValue ? "on" : "off"));
        String message = StringUtils.translate("malilib.message.toggled", prettyName, pre + status + GuiBase.TXT_RST);

        printActionbarMessage(message);
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public static void renderInGameMessages()
    {
        int x = GuiUtils.getScaledWindowWidth() / 2;
        int y = GuiUtils.getScaledWindowHeight() - 76;

        synchronized (IN_GAME_MESSAGES)
        {
            IN_GAME_MESSAGES.drawMessages(x, y);
        }
    }

    public static class InfoMessageConsumer implements IStringConsumer
    {
        @Override
        public void setString(String string)
        {
            TextComponentTranslation message = new TextComponentTranslation(string);
            Minecraft.getInstance().ingameGUI.addChatMessage(ChatType.GAME_INFO, message);
        }
    }

    public static class InGameMessageConsumer implements IMessageConsumer
    {
        @Override
        public void addMessage(MessageType type, int lifeTime, String translationKey, Object... args)
        {
            InfoUtils.showInGameMessage(type, lifeTime, translationKey, args);
        }
    }
}
