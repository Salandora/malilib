package fi.dy.masa.malilib.util;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;

public enum InfoType implements IConfigOptionListEntry
{
    MESSAGE_OVERLAY ( "message_overlay", "malilib.label.infotype.message_overlay"),
    INGAME_MESSAGE  ("ingame_message", "malilib.label.infotype.ingame_message"),
    ACTION_BAR      ( "action_bar", "malilib.label.infotype.action_bar");

    private final String configString;
    private final String unlocName;

    private InfoType(String configString, String unlocName)
    {
        this.configString = configString;
        this.unlocName = unlocName;
    }

    @Override
    public String getStringValue()
    {
        return this.configString;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.unlocName);
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward)
    {
        int id = this.ordinal();

        if (forward)
        {
            if (++id >= values().length)
            {
                id = 0;
            }
        }
        else
        {
            if (--id < 0)
            {
                id = values().length - 1;
            }
        }

        return values()[id % values().length];
    }

    @Override
    public InfoType fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static InfoType fromStringStatic(String name)
    {
        for (InfoType infoType : InfoType.values())
        {
            if (infoType.configString.equalsIgnoreCase(name))
            {
                return infoType;
            }
        }

        return InfoType.MESSAGE_OVERLAY;
    }
}
