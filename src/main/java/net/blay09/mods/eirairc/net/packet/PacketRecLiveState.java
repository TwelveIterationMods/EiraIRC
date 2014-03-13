// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;

public class PacketRecLiveState extends AbstractPacket {

	private String username;
	private boolean recState;
	private boolean liveState;
	
	public PacketRecLiveState() {
	}
	
	public PacketRecLiveState(String username, boolean recState, boolean liveState) {
		this.username = username;
		this.recState = recState;
		this.liveState = liveState;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext context, ByteBuf buffer) {
		putString(buffer, username);
		buffer.writeBoolean(recState);
		buffer.writeBoolean(liveState);
	}

	@Override
	public void decodeInto(ChannelHandlerContext context, ByteBuf buffer) {
		username = getString(buffer);
		recState = buffer.readBoolean();
		liveState = buffer.readBoolean();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(username);
		playerInfo.isLive = liveState;
		playerInfo.isRecording = recState;		
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		if(!player.getCommandSenderName().equals(username)) {
			return;
		}
		EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(username);
		if(playerInfo.isLive != liveState) {
			EiraIRC.proxy.publishNotification(NotificationType.UserLive, liveState ? Utils.getLocalizedMessage("irc.notify.liveTrue", username) : Utils.getLocalizedMessage("irc.notify.liveFalse", username));
		}
		if(playerInfo.isRecording != recState) {
			EiraIRC.proxy.publishNotification(NotificationType.UserRecording, recState ? Utils.getLocalizedMessage("irc.notify.recordingTrue", username) : Utils.getLocalizedMessage("irc.notify.recordingFalse", username));
		}
		playerInfo.isLive = liveState;
		playerInfo.isRecording = recState;
		EiraIRC.instance.packetPipeline.sendToAll(this);
	}

}
