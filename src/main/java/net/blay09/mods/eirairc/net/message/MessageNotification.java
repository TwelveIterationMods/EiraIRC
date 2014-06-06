// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.util.NotificationType;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageNotification implements IMessage {

	private byte typeId;
	private String text;
	
	public MessageNotification() {
	}
	
	public MessageNotification(NotificationType type, String text) {
		this.typeId = (byte) type.ordinal();
		this.text = text;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		typeId = buf.readByte();
		text = Utils.readString(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(typeId);
		Utils.writeString(buf, text);
	}

	public byte getNotificationType() {
		return typeId;
	}
	
	public String getText() {
		return text;
	}
}
