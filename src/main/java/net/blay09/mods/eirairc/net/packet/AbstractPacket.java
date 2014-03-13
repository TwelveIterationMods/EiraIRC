// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import net.minecraft.entity.player.EntityPlayer;

public abstract class AbstractPacket {

	private static final String ENCODING = "UTF-8";
	
	public abstract void encodeInto(ChannelHandlerContext context, ByteBuf buffer);
	public abstract void decodeInto(ChannelHandlerContext context, ByteBuf buffer);
	public abstract void handleClientSide(EntityPlayer player);
	public abstract void handleServerSide(EntityPlayer player);
	
	public void putString(ByteBuf buffer, String s) {
		try {
			byte[] b = s.getBytes(ENCODING);
			buffer.writeShort(b.length);
			buffer.writeBytes(b);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public String getString(ByteBuf buffer) {
		short len = buffer.readShort();
		byte[] b = new byte[len];
		buffer.readBytes(b);
		try {
			return new String(b, ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
