// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.entity.player.EntityPlayer;

public class MessageHello implements IMessage {

	private String version;
	
	public MessageHello() {
	}
	
	public MessageHello(String version) {
		this.version = version;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		version = Utils.readString(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		Utils.writeString(buf, version);
	}

	public String getVersion() {
		return version;
	}

}
