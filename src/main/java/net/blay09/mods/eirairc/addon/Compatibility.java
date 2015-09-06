package net.blay09.mods.eirairc.addon;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.event.InitConfigEvent;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class Compatibility {

    private static boolean isEiraMoticonsInstalled;
    private static boolean isTabbyChat2Installed;

    public static boolean isEiraMoticonsInstalled() {
        return isEiraMoticonsInstalled;
    }

    public static boolean isTabbyChat2Installed() {
        return isTabbyChat2Installed;
    }

    public static void postInit(FMLPostInitializationEvent event) {
        event.buildSoftDependProxy("Dynmap", "net.blay09.mods.eirairc.addon.DynmapWebChatAddon");

        if (event.getSide() == Side.CLIENT) {
            event.buildSoftDependProxy("TabbyChat2", "net.blay09.mods.eirairc.addon.TabbyChat2Addon");
            event.buildSoftDependProxy("eiramoticons", "net.blay09.mods.eirairc.addon.EiraMoticonsAddon");
            new FancyOverlay();
        }

        MinecraftForge.EVENT_BUS.post(new InitConfigEvent.SharedGlobalSettings(SharedGlobalConfig.manager));
        if(EiraIRC.proxy.getClientGlobalConfig() != null) {
            MinecraftForge.EVENT_BUS.post(new InitConfigEvent.ClientGlobalSettings(EiraIRC.proxy.getClientGlobalConfig()));
        }
        MinecraftForge.EVENT_BUS.post(new InitConfigEvent.GeneralSettings(SharedGlobalConfig.generalSettings.manager));
        MinecraftForge.EVENT_BUS.post(new InitConfigEvent.BotSettings(SharedGlobalConfig.botSettings.manager));
        MinecraftForge.EVENT_BUS.post(new InitConfigEvent.ThemeSettings(SharedGlobalConfig.theme.manager));

        for (ServerConfig serverConfig : ConfigurationHandler.getServerConfigs()) {
            MinecraftForge.EVENT_BUS.post(new InitConfigEvent.GeneralSettings(serverConfig.getGeneralSettings().manager));
            MinecraftForge.EVENT_BUS.post(new InitConfigEvent.BotSettings(serverConfig.getBotSettings().manager));
            MinecraftForge.EVENT_BUS.post(new InitConfigEvent.ThemeSettings(serverConfig.getTheme().manager));
            for (ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
                MinecraftForge.EVENT_BUS.post(new InitConfigEvent.GeneralSettings(channelConfig.getGeneralSettings().manager));
                MinecraftForge.EVENT_BUS.post(new InitConfigEvent.BotSettings(channelConfig.getBotSettings().manager));
                MinecraftForge.EVENT_BUS.post(new InitConfigEvent.ThemeSettings(channelConfig.getTheme().manager));
            }
        }

        isEiraMoticonsInstalled = Loader.isModLoaded("eiramoticons");
        isTabbyChat2Installed = Loader.isModLoaded("TabbyChat2");
    }
}
