// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc;

import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import blay09.mods.eirairc.net.packet.PacketNotification;
import blay09.mods.eirairc.util.NotificationType;

public class CommonProxy {

	public void setupClient() {
	}

	public void publishNotification(NotificationType type, String text) {
		Packet packet = new PacketNotification(type, text).createPacket();
		if(packet != null) {
			MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(packet);
		}
	}
	
	public String getUsername() {
		return null;
	}

	public boolean isIngame() {
		return true;
	}
	
	public void renderTick(float delta) {
	}
}
