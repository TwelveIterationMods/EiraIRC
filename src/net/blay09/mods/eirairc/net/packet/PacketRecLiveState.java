// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;

public class PacketRecLiveState extends EiraPacket {

	private String username;
	private boolean recState;
	private boolean liveState;
	
	public PacketRecLiveState() {
		super(PacketType.RecLiveState);
	}
	
	public PacketRecLiveState(String username, boolean recState, boolean liveState) {
		this();
		this.username = username;
		this.recState = recState;
		this.liveState = liveState;
	}
	
	@Override
	public void read(DataInputStream in) throws IOException {
		username = in.readUTF();
		recState = in.readBoolean();
		liveState = in.readBoolean();
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(username);
		out.writeBoolean(recState);
		out.writeBoolean(liveState);
	}
	
	@Override
	public void executeClient(INetworkManager manager, EntityPlayer player) {
		EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(username);
		playerInfo.isLive = liveState;
		playerInfo.isRecording = recState;
	}
	
	@Override
	public void executeServer(INetworkManager manager, EntityPlayer player) {
		if(!player.username.equals(username)) {
			return;
		}
		EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(username);
		if(playerInfo.isLive != liveState) {
			EiraIRC.proxy.publishNotification(NotificationType.UserLive, liveState ? Utils.getLocalizedMessage("irc.notify.liveTrue", username) : Utils.getLocalizedMessage("irc.notify.liveFalse", username));
		}
		if(playerInfo.isRecording != recState) {
			EiraIRC.proxy.publishNotification(NotificationType.UserRecording, liveState ? Utils.getLocalizedMessage("irc.notify.recordingTrue", username) : Utils.getLocalizedMessage("irc.notify.recordingFalse", username));
		}
		playerInfo.isLive = liveState;
		playerInfo.isRecording = recState;
		Packet packet = createPacket();
		if(packet != null) {
			MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(packet);
		}
	}

}
