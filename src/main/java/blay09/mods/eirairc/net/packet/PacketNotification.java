// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.net.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.util.NotificationType;

public class PacketNotification extends AbstractPacket {

	private byte typeId;
	private String text;
	
	public PacketNotification() {
	}
	
	public PacketNotification(NotificationType type, String text) {
		this.typeId = (byte) type.ordinal();
		this.text = text;
	}

	@Override
	public void encodeInto(ChannelHandlerContext context, ByteBuf buffer) {
		buffer.writeByte(typeId);
		putString(buffer, text);
	}

	@Override
	public void decodeInto(ChannelHandlerContext context, ByteBuf buffer) {
		typeId = buffer.readByte();
		text = getString(buffer);
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		EiraIRC.proxy.publishNotification(NotificationType.fromId(typeId), text);		
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
	}
}
