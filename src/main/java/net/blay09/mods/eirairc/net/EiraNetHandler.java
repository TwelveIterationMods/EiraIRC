// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net;

import java.util.HashMap;
import java.util.Map;

import net.blay09.mods.eirairc.net.message.MessageRecLiveState.SMessageRecLiveState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class EiraNetHandler {

	private final Map<String, EiraPlayerInfo> playerInfoMap = new HashMap<String, EiraPlayerInfo>();
	
	public EiraPlayerInfo getPlayerInfo(String username) {
		synchronized(playerInfoMap) {
			EiraPlayerInfo playerInfo = playerInfoMap.get(username);
			if(playerInfo == null) {
				playerInfo = new EiraPlayerInfo(username);
				playerInfoMap.put(username, playerInfo);
			}
			return playerInfo;
		}
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		EntityPlayerMP entityPlayerMP = (EntityPlayerMP) event.player;
		synchronized(playerInfoMap) {
			for(EiraPlayerInfo playerInfo : playerInfoMap.values()) {
				PacketHandler.INSTANCE.sendTo(new SMessageRecLiveState(playerInfo.getUsername(), playerInfo.isRecording, playerInfo.isLive), entityPlayerMP);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLogout(PlayerLoggedOutEvent event) {
		synchronized(playerInfoMap) {
			playerInfoMap.remove(event.player.getName());
		}
	}
	
}
