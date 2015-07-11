// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc;

import net.blay09.mods.eirairc.api.event.IRCChannelChatOrCTCPEvent;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.net.PacketHandler;
import net.blay09.mods.eirairc.net.message.MessageNotification;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.List;

public class CommonProxy {

	public void init() {}

	public void postInit() {}

	public void sendNotification(EntityPlayerMP entityPlayer, NotificationType type, String text) {
		PacketHandler.instance.sendTo(new MessageNotification(type, text), entityPlayer);
	}
	
	public void publishNotification(NotificationType type, String text) {
		PacketHandler.instance.sendToAll(new MessageNotification(type, text));
	}
	
	public String getUsername() {
		return null;
	}

	public boolean isIngame() {
		return true;
	}
	
	public void loadConfig(File configDir, boolean reloadFile) {
		SharedGlobalConfig.load(configDir, reloadFile);
	}

	public void loadLegacyConfig(File configDir, Configuration legacyConfig) {
		SharedGlobalConfig.loadLegacy(configDir, legacyConfig);
	}

	public void handleRedirect(ServerConfig serverConfig) {

	}

	public boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		return SharedGlobalConfig.handleConfigCommand(sender, key, value);
	}

	public String handleConfigCommand(ICommandSender sender, String key) {
		return SharedGlobalConfig.handleConfigCommand(sender, key);
	}

	public void addConfigOptionsToList(List<String> list, String option) {
		SharedGlobalConfig.addOptionsToList(list, option);
	}

	public boolean checkClientBridge(IRCChannelChatOrCTCPEvent event) {
		return false;
	}

	public void saveConfig() {
		if (SharedGlobalConfig.thisConfig.hasChanged()) {
			SharedGlobalConfig.thisConfig.save();
		}
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

}
