// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc;

import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.net.PacketHandler;
import net.blay09.mods.eirairc.net.message.MessageNotification;
import net.blay09.mods.eirairc.util.NotificationType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class CommonProxy {

	public void setupClient() {
	}

	public void sendNotification(EntityPlayerMP entityPlayer, NotificationType type, String text) {
		PacketHandler.INSTANCE.sendTo(new MessageNotification(type, text), entityPlayer);
	}
	
	public void publishNotification(NotificationType type, String text) {
		PacketHandler.INSTANCE.sendToAll(new MessageNotification(type, text));
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
}
