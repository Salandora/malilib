package fi.dy.masa.malilib.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import fi.dy.masa.malilib.hotkeys.IKeyboardInputHandler;
import fi.dy.masa.malilib.hotkeys.IMouseInputHandler;
import fi.dy.masa.malilib.hotkeys.KeybindMulti;
import fi.dy.masa.malilib.util.KeyCodes;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.client.resources.I18n;

public class InputEventHandler implements IKeybindManager
{
    private static final InputEventHandler INSTANCE = new InputEventHandler();

    private final Multimap<Integer, IKeybind> hotkeyMap = ArrayListMultimap.create();
    private final List<KeybindCategory> allKeybinds = new ArrayList<>();
    private final IntOpenHashSet modifierKeys = new IntOpenHashSet();
    private final List<IKeybindProvider> keybindProviders = new ArrayList<>();
    private final List<IKeyboardInputHandler> keyboardHandlers = new ArrayList<>();
    private final List<IMouseInputHandler> mouseHandlers = new ArrayList<>();
    private boolean cancelKeyInput;

    private InputEventHandler()
    {
        this.modifierKeys.add(KeyCodes.KEY_LEFT_SHIFT);
        this.modifierKeys.add(KeyCodes.KEY_RIGHT_SHIFT);
        this.modifierKeys.add(KeyCodes.KEY_LEFT_CONTROL);
        this.modifierKeys.add(KeyCodes.KEY_RIGHT_CONTROL);
        this.modifierKeys.add(KeyCodes.KEY_LEFT_ALT);
        this.modifierKeys.add(KeyCodes.KEY_RIGHT_ALT);
    }

    public static InputEventHandler getInstance()
    {
        return INSTANCE;
    }

    public void registerKeybindProvider(IKeybindProvider provider)
    {
        if (this.keybindProviders.contains(provider) == false)
        {
            this.keybindProviders.add(provider);
        }

        provider.addHotkeys(this);
    }

    public void unregisterKeybindProvider(IKeybindProvider provider)
    {
        this.keybindProviders.remove(provider);
    }

    public void registerKeyboardInputHandler(IKeyboardInputHandler handler)
    {
        if (this.keyboardHandlers.contains(handler) == false)
        {
            this.keyboardHandlers.add(handler);
        }
    }

    public void unregisterKeyboardInputHandler(IKeyboardInputHandler handler)
    {
        this.keyboardHandlers.remove(handler);
    }

    public void registerMouseInputHandler(IMouseInputHandler handler)
    {
        if (this.mouseHandlers.contains(handler) == false)
        {
            this.mouseHandlers.add(handler);
        }
    }

    public void unregisterMouseInputHandler(IMouseInputHandler handler)
    {
        this.mouseHandlers.remove(handler);
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void updateUsedKeys()
    {
        this.hotkeyMap.clear();

        for (IKeybindProvider handler : this.keybindProviders)
        {
            handler.addKeysToMap(this);
        }
    }

    public List<KeybindCategory> getKeybindCategories()
    {
        return this.allKeybinds;
    }

    private boolean isModifierKey(int eventKey)
    {
        return this.modifierKeys.contains(eventKey);
    }

    @Override
    public void addKeybindToMap(IKeybind keybind)
    {
        Collection<Integer> keys = keybind.getKeys();

        for (int key : keys)
        {
            this.hotkeyMap.put(key, keybind);
        }
    }

    @Override
    public void addHotkeysForCategory(String modName, String keyCategory, List<? extends IHotkey> hotkeys)
    {
        KeybindCategory cat = new KeybindCategory(modName, keyCategory, hotkeys);

        // Remove a previous entry, if any (matched based on the modName and keyCategory only!)
        this.allKeybinds.remove(cat);
        this.allKeybinds.add(cat);
    }

    private boolean checkKeyBindsForChanges(int eventKey)
    {
        boolean cancel = false;
        Collection<IKeybind> keybinds = this.hotkeyMap.get(eventKey);

        if (keybinds.isEmpty() == false)
        {
            for (IKeybind keybind : keybinds)
            {
                // Note: isPressed() has to get called for key releases too, to reset the state
                cancel |= keybind.updateIsPressed();
            }
        }

        return cancel;
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void tickKeybinds()
    {
        /*
        for (IKeybind keybind : this.hotkeyMap.values())
        {
            keybind.tick();
        }
        */
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public boolean onKeyInput(int keyCode, int scanCode, boolean eventKeyState, boolean isGui)
    {
        boolean cancel = false;

        // Update the cached pressed keys status
        KeybindMulti.onKeyInputPre(keyCode, scanCode, eventKeyState);

        cancel = this.checkKeyBindsForChanges(keyCode);
        this.cancelKeyInput |= isGui && cancel;

        if (this.keyboardHandlers.isEmpty() == false)
        {
            for (IKeyboardInputHandler handler : this.keyboardHandlers)
            {
                if (handler.onKeyInput(keyCode, eventKeyState))
                {
                    this.cancelKeyInput |= isGui;
                    return true;
                }
            }
        }

        boolean overrideCancel = this.cancelKeyInput && isGui == false;

        // This hacky state indicates that a mouse event was cancelled in the GUI mouse handler,
        // which would then cause a key press to not get handled in the GuiScreen keyboard handling code,
        // which would let it bleed through to the non-GUI handling code (Minecraft#runTick()).
        if (isGui == false)
        {
            this.cancelKeyInput = false;
        }

        // Somewhat hacky fix to prevent eating the modifier keys... >_>
        // A proper fix would likely require adding a context for the keys,
        // and only cancel if the context is currently active/valid.
        return this.isModifierKey(keyCode) == false && (overrideCancel || cancel);
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public boolean onMouseClick(int mouseX, int mouseY, int eventButton, boolean eventButtonState, boolean isGui)
    {
        boolean cancel = false;

        if (eventButton != -1)
        {
            // Update the cached pressed keys status
            KeybindMulti.onKeyInputPre(eventButton - 100, 0, eventButtonState);

            cancel = this.checkKeyBindsForChanges(eventButton - 100);

            if (this.mouseHandlers.isEmpty() == false)
            {
                for (IMouseInputHandler handler : this.mouseHandlers)
                {
                    if (handler.onMouseClick(mouseX, mouseY, eventButton, eventButtonState))
                    {
                        this.cancelKeyInput |= isGui;
                        return true;
                    }
                }
            }
        }

        this.cancelKeyInput = isGui && cancel;
        return cancel;
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public boolean onMouseScroll(final int mouseX, final int mouseY, final double amount, boolean isGui)
    {
        boolean cancel = false;

        if (amount != 0)
        {
            if (this.mouseHandlers.isEmpty() == false)
            {
                for (IMouseInputHandler handler : this.mouseHandlers)
                {
                    if (handler.onMouseScroll(mouseX, mouseY, amount))
                    {
                        this.cancelKeyInput |= isGui;
                        return true;
                    }
                }
            }
        }

        this.cancelKeyInput = isGui && cancel;
        return cancel;
    }

    /**
     * NOT PUBLIC API - DO NOT CALL
     */
    public void onMouseMove(final int mouseX, final int mouseY)
    {
        if (this.mouseHandlers.isEmpty() == false)
        {
            for (IMouseInputHandler handler : this.mouseHandlers)
            {
                handler.onMouseMove(mouseX, mouseY);
            }
        }
    }

    public static class KeybindCategory implements Comparable<KeybindCategory>
    {
        private final String modName;
        private final String category;
        private final List<? extends IHotkey> hotkeys;

        public KeybindCategory(String modName, String category, List<? extends IHotkey> hotkeys)
        {
            this.modName = modName;
            this.category = category;
            this.hotkeys = hotkeys;
        }

        public String getModName()
        {
            return this.modName;
        }

        public String getCategory()
        {
            return I18n.format(this.category);
        }

        public List<? extends IHotkey> getHotkeys()
        {
            return this.hotkeys;
        }

        @Override
        public int compareTo(KeybindCategory other)
        {
            int val = this.modName.compareTo(other.modName);

            if (val != 0)
            {
                return val;
            }

            return this.category.compareTo(other.category);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            KeybindCategory other = (KeybindCategory) obj;
            if (category == null)
            {
                if (other.category != null)
                    return false;
            }
            else if (!category.equals(other.category))
                return false;
            if (modName == null)
            {
                if (other.modName != null)
                    return false;
            }
            else if (!modName.equals(other.modName))
                return false;
            return true;
        }
    }
}
