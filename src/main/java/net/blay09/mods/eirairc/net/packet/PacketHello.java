// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;

public class PacketHello extends AbstractPacket {

	private String version;
	
	public PacketHello() {
	}
	
	public PacketHello(String version) {
		this.version = version;
	}

	@Override
	public void encodeInto(ChannelHandlerContext context, ByteBuf buffer) {
//		putString(buffer, version);
	}

	@Override
	public void decodeInto(ChannelHandlerContext context, ByteBuf buffer) {
//		version = getString(buffer);
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(player.getCommandSenderName());
		playerInfo.modVersion = version;		
	}
}
