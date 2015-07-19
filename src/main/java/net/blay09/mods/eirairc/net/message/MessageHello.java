// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.net.message;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.blay09.mods.eirairc.util.Utils;

public class MessageHello implements IMessage {

	private String version;
	
	public MessageHello() {
	}
	
	public MessageHello(String version) {
		this.version = version;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		version = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, version);
	}

	public String getVersion() {
		return version;
	}

}
