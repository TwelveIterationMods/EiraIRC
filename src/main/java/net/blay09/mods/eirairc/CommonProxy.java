// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc;

import net.blay09.mods.eirairc.api.config.IConfigManager;
import net.blay09.mods.eirairc.api.event.IRCChannelMessageEvent;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.config.LocalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.net.NetworkHandler;
import net.blay09.mods.eirairc.net.message.MessageNotification;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.crash.CrashReport;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ReportedException;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommonProxy {

    public void init() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void postInit() {
    }

    public void publishNotification(NotificationType type, String text) {
        NetworkHandler.instance.sendToAll(new MessageNotification(type, text));
    }

    public String getUsername() {
        return null;
    }

    public void loadConfig(File configDir, boolean reloadFile) {
        SharedGlobalConfig.load(configDir, reloadFile);
        LocalConfig.load(configDir, reloadFile);
    }

    public void handleRedirect(ServerConfig serverConfig) {
    }

    public boolean handleConfigCommand(ICommandSender sender, String key, String value) {
        return SharedGlobalConfig.handleConfigCommand(sender, key, value);
    }

    public String handleConfigCommand(ICommandSender sender, String key) {
        return SharedGlobalConfig.handleConfigCommand(sender, key);
    }

    public void addConfigOptionsToList(List<String> list, String option, boolean autoCompleteOption) {
        SharedGlobalConfig.addOptionsToList(list, option, autoCompleteOption);
    }

    public boolean checkClientBridge(IRCChannelMessageEvent event) {
        return false;
    }

    public void saveConfig() {
        if (SharedGlobalConfig.thisConfig.hasChanged()) {
            SharedGlobalConfig.thisConfig.save();
        }
        if (LocalConfig.thisConfig.hasChanged()) {
            LocalConfig.thisConfig.save();
        }
    }

    public void handleException(IRCConnection connection, Exception e) {
        EiraIRC.logger.error("Encountered an unexpected exception", e);
        CrashReport report = MinecraftServer.getServer().addServerInfoToCrashReport(new CrashReport("Exception in IRC Connection " + connection.getHost(), e));
        File file1 = new File(new File(MinecraftServer.getServer().getDataDirectory(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");
        if (report.saveToFile(file1)) {
            EiraIRC.logger.error("This crash report has been saved to: " + file1.getAbsolutePath());
        } else {
            EiraIRC.logger.error("We were unable to save this crash report to disk.");
        }
        MinecraftServer.getServer().stopServer();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        ConnectionManager.tickConnections();
    }

    public IConfigManager getClientGlobalConfig() {
        return null;
    }

    public void addScheduledTask(Runnable runnable) {
        MinecraftServer.getServer().addScheduledTask(runnable);
    }
}
