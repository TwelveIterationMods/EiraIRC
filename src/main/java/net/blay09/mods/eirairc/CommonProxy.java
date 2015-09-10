// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
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
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.List;

public class CommonProxy {

	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void postInit() {}

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

	public void loadLegacyConfig(File configDir, Configuration legacyConfig) {
		SharedGlobalConfig.loadLegacy(configDir, legacyConfig);
		LocalConfig.load(configDir, false);
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

	public void saveConfig() {
		if (SharedGlobalConfig.thisConfig.hasChanged()) {
			SharedGlobalConfig.thisConfig.save();
		}
		if(LocalConfig.thisConfig.hasChanged()) {
			LocalConfig.thisConfig.save();
		}
	}

	public boolean checkClientBridge(IRCChannelMessageEvent event) {
		return false;
	}

	public void handleException(IRCConnection connection, Exception e) {
		ChatComponentText componentText = new ChatComponentText("EiraIRC encountered an unexpected error! The connection to ");
		componentText.getChatStyle().setColor(EnumChatFormatting.DARK_RED);
		componentText.getChatStyle().setBold(true);
		ChatComponentText serverText = new ChatComponentText(connection.getIdentifier());
		serverText.getChatStyle().setItalic(true);
		componentText.appendSibling(serverText);
		componentText.appendText(" was closed.");
		Utils.addMessageToChat(componentText);

		ChatComponentText logText = new ChatComponentText("See the ");
		logText.getChatStyle().setColor(EnumChatFormatting.DARK_RED);
		ChatComponentText logFileText = new ChatComponentText("log file");
		if(Utils.isServerSide()) {
			logFileText.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(e.getMessage())));
		} else {
			logFileText.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, "logs/fml-" + (Utils.isServerSide() ? "server" : "client") + "-latest.log"));
			logFileText.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to open log file.")));
			logFileText.getChatStyle().setUnderlined(true);
		}
		logFileText.getChatStyle().setBold(true);
		logFileText.getChatStyle().setColor(EnumChatFormatting.BLUE);
		logText.appendSibling(logFileText);
		logText.appendText(" for more details.");
		Utils.addMessageToChat(logText);

		e.printStackTrace();
	}

	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent event) {
		ConnectionManager.tickConnections();
	}

	public IConfigManager getClientGlobalConfig() {
		return null;
	}

	public void handleRedirect(ServerConfig serverConfig) {}
}
