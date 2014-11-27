// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net.message.handler;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.blay09.mods.eirairc.net.PacketHandler;
import net.blay09.mods.eirairc.net.message.MessageRecLiveState.SMessageRecLiveState;
import net.blay09.mods.eirairc.net.message.MessageRecLiveState.CMessageRecLiveState;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CHandlerRecLiveState implements IMessageHandler<CMessageRecLiveState, IMessage> {
	
	@Override
	public IMessage onMessage(CMessageRecLiveState message, MessageContext ctx) {
		EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
		if(!entityPlayer.getName().equals(message.getUsername())) {
			return null;
		}
		EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(message.getUsername());
		if(playerInfo.isLive != message.getLiveState()) {
			EiraIRC.proxy.publishNotification(NotificationType.UserLive, message.getLiveState() ? Utils.getLocalizedMessage("irc.notify.liveTrue", message.getUsername()) : Utils.getLocalizedMessage("irc.notify.liveFalse", message.getUsername()));
		}
		if(playerInfo.isRecording != message.getRecState()) {
			EiraIRC.proxy.publishNotification(NotificationType.UserRecording, message.getRecState() ? Utils.getLocalizedMessage("irc.notify.recordingTrue", message.getUsername()) : Utils.getLocalizedMessage("irc.notify.recordingFalse", message.getUsername()));
		}
		playerInfo.isLive = message.getLiveState();
		playerInfo.isRecording = message.getRecState();
		PacketHandler.INSTANCE.sendToAll(new SMessageRecLiveState(message.getUsername(), message.getRecState(), message.getLiveState()));
		return null;
	}

}
