package fi.dy.masa.malilib.mixin;

import java.io.IOException;;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.malilib.command.ClientCommandHandler;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

@Mixin(GuiScreen.class)
public abstract class MixinGuiScreen extends Gui
{
    @Shadow
    protected Minecraft mc;

    @Inject(method = "renderToolTip", at = @At("RETURN"))
    private void onRenderToolTip(ItemStack stack, int x, int y, CallbackInfo ci)
    {
        RenderEventHandler.getInstance().onRenderTooltipLast(stack, x, y);
    }

    /*@Inject(method = "sendChatMessage(Ljava/lang/String;Z)V", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/EntityPlayerSP;sendChatMessage(Ljava/lang/String;)V"),
            cancellable = true)
    private void onSendMessage(String msg, boolean addToChat, CallbackInfo ci)
    {
        if (ClientCommandHandler.INSTANCE.executeCommand(this.mc.player, msg) != 0)
        {
            ci.cancel();
        }
    }*/
}
