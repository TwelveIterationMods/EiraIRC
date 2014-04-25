// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net;

import java.util.HashMap;
import java.util.Map;

import net.blay09.mods.eirairc.net.packet.PacketRecLiveState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;
import cpw.mods.fml.common.IPlayerTracker;

public class EiraNetHandler implements IPlayerTracker {

	private final Map<String, EiraPlayerInfo> playerInfoMap = new HashMap<String, EiraPlayerInfo>();
	
	public EiraPlayerInfo getPlayerInfo(String username) {
		EiraPlayerInfo playerInfo = playerInfoMap.get(username);
		if(playerInfo == null) {
			playerInfo = new EiraPlayerInfo(username);
			playerInfoMap.put(username, playerInfo);
		}
		return playerInfo;
	}

	@Override
	public void onPlayerLogin(EntityPlayer player) {
		EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
		for(EiraPlayerInfo playerInfo : playerInfoMap.values()) {
			Packet packet = new PacketRecLiveState(playerInfo.getUsername(), playerInfo.isRecording, playerInfo.isLive).createPacket();
			if(packet != null) {
				entityPlayerMP.playerNetServerHandler.sendPacketToPlayer(packet);
			}
		}
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		playerInfoMap.remove(player.username);
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
	}
	
}
