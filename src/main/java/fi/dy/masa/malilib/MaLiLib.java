package fi.dy.masa.malilib;

import fi.dy.masa.malilib.event.TickHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.rift.listener.client.ClientTickable;
import org.dimdev.rift.listener.client.OverlayRenderer;
import org.dimdev.riftloader.listener.InitializationListener;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.MaLiLibConfigs;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import fi.dy.masa.malilib.reference.MaLiLibReference;
import net.minecraft.client.Minecraft;

public class MaLiLib implements InitializationListener, OverlayRenderer, ClientTickable
{
    public static final Logger logger = LogManager.getLogger(MaLiLibReference.MOD_ID);

    @Override
    public void onInitialization()
    {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.malilib.json");

        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
    }

    @Override
    public void renderOverlay()
    {
        RenderEventHandler.getInstance().onRenderGameOverlayPost(Minecraft.getInstance().getRenderPartialTicks());
    }

    @Override
    public void clientTick(Minecraft mc)
    {
        TickHandler.getInstance().onClientTick(mc);
    }

    private static class InitHandler implements IInitializationHandler
    {
        @Override
        public void registerModHandlers()
        {
            ConfigManager.getInstance().registerConfigHandler(MaLiLibReference.MOD_ID, new MaLiLibConfigs());
        }
    }
}
