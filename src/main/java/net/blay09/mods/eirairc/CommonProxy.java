// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc;

import net.blay09.mods.eirairc.irc.IRCChannel;
import net.blay09.mods.eirairc.net.packet.PacketNotification;
import net.blay09.mods.eirairc.util.NotificationType;
import net.minecraft.entity.player.EntityPlayerMP;

public class CommonProxy {

	public void setupClient() {
	}

	public void sendNotification(EntityPlayerMP entityPlayer, NotificationType type, String text) {
		EiraIRC.instance.packetPipeline.sendTo(new PacketNotification(type, text), entityPlayer);
	}
	
	public void publishNotification(NotificationType type, String text) {
		EiraIRC.instance.packetPipeline.sendToAll(new PacketNotification(type, text));
	}
	
	public String getUsername() {
		return null;
	}

	public boolean isIngame() {
		return true;
	}
	
	public void renderTick(float delta) {
	}

	public void onChannelJoined(IRCChannel channel) {
	}

}
