// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net;

import java.util.HashMap;
import java.util.Map;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.net.packet.AbstractPacket;
import net.blay09.mods.eirairc.net.packet.PacketRecLiveState;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class EiraNetHandler {

	private final Map<String, EiraPlayerInfo> playerInfoMap = new HashMap<String, EiraPlayerInfo>();
	
	public EiraPlayerInfo getPlayerInfo(String username) {
		EiraPlayerInfo playerInfo = playerInfoMap.get(username);
		if(playerInfo == null) {
			playerInfo = new EiraPlayerInfo(username);
			playerInfoMap.put(username, playerInfo);
		}
		return playerInfo;
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		EntityPlayerMP entityPlayerMP = (EntityPlayerMP) event.player;
		for(EiraPlayerInfo playerInfo : playerInfoMap.values()) {
			AbstractPacket packet = new PacketRecLiveState(playerInfo.getUsername(), playerInfo.isRecording, playerInfo.isLive);
			EiraIRC.instance.packetPipeline.sendTo(packet, entityPlayerMP);
		}
	}

	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent event) {
		playerInfoMap.remove(event.player.getCommandSenderName());
	}
	
}
