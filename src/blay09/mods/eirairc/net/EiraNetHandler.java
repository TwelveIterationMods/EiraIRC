package blay09.mods.eirairc.net;

import java.util.Map;

public class EiraNetHandler {

	private Map<String, EiraPlayerInfo> playerInfoMap;
	
	public EiraPlayerInfo getPlayerInfo(String username) {
		EiraPlayerInfo playerInfo = playerInfoMap.get(username);
		if(playerInfo == null) {
			playerInfoMap.put(username, new EiraPlayerInfo(username));
		}
		return playerInfo;
	}
	
}
