package blay09.mods.eirairc.net;

import java.util.HashMap;
import java.util.Map;

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
	
}
