// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.blay09.mods.eirairc.net.PacketHandler;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.entity.player.EntityPlayer;

public abstract class MessageRecLiveState implements IMessage {

	public static class CMessageRecLiveState extends MessageRecLiveState {
		public CMessageRecLiveState() { }
		public CMessageRecLiveState(String username, boolean recState, boolean liveState) {
			super(username, recState, liveState);
		}
	}
	public static class SMessageRecLiveState extends MessageRecLiveState {
		public SMessageRecLiveState() { }
		public SMessageRecLiveState(String username, boolean recState, boolean liveState) {
			super(username, recState, liveState);
		}
	}
	
	private String username;
	private boolean recState;
	private boolean liveState;
	
	public MessageRecLiveState() {
	}
	
	protected MessageRecLiveState(String username, boolean recState, boolean liveState) {
		this.username = username;
		this.recState = recState;
		this.liveState = liveState;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		username = Utils.readString(buf);
		recState = buf.readBoolean();
		liveState = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		Utils.writeString(buf, username);
		buf.writeBoolean(recState);
		buf.writeBoolean(liveState);
	}

	public String getUsername() {
		return username;
	}
	
	public boolean getRecState() {
		return recState;
	}
	
	public boolean getLiveState() {
		return liveState;
	}
}
