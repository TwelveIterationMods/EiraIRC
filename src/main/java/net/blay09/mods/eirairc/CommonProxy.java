// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc;

import net.blay09.mods.eirairc.command.CommandConnect;
import net.blay09.mods.eirairc.command.CommandDisconnect;
import net.blay09.mods.eirairc.command.CommandIRC;
import net.blay09.mods.eirairc.command.CommandJoin;
import net.blay09.mods.eirairc.command.CommandNick;
import net.blay09.mods.eirairc.command.CommandPart;
import net.blay09.mods.eirairc.command.CommandServIRC;
import net.blay09.mods.eirairc.command.CommandWho;
import net.blay09.mods.eirairc.config.GlobalConfig;
import net.blay09.mods.eirairc.irc.IRCChannel;
import net.blay09.mods.eirairc.net.packet.PacketNotification;
import net.blay09.mods.eirairc.util.NotificationType;
import net.minecraft.command.CommandHandler;
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
