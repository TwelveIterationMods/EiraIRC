// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.net;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

import java.util.HashMap;
import java.util.Map;

@Deprecated
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
	public void onPlayerLogout(PlayerLoggedOutEvent event) {
		synchronized(playerInfoMap) {
			playerInfoMap.remove(event.player.getCommandSenderName());
		}
	}
	
}
