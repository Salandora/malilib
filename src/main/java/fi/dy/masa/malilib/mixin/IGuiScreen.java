package fi.dy.masa.malilib.mixin;

import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiScreen.class)
public interface IGuiScreen
{
    @Invoker("initGui")
    void newInitGui();
}
