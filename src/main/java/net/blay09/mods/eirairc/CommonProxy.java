// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc;

import net.blay09.mods.eirairc.api.event.IRCChannelChatEvent;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.net.PacketHandler;
import net.blay09.mods.eirairc.net.message.MessageNotification;
import net.blay09.mods.eirairc.util.NotificationType;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
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
	
	public void renderTick(float delta) {
	}

	public void loadConfig(File configDir) {
		SharedGlobalConfig.load(configDir);
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

	public boolean checkClientBridge(IRCChannelChatEvent event) {
		return false;
	}

	public void saveConfig() {
		if (SharedGlobalConfig.thisConfig.hasChanged()) {
			SharedGlobalConfig.thisConfig.save();
		}
	}

}
