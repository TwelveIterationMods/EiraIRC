// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.net.message;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class MessageRedirect implements IMessage {

	private String redirectConfig;

	public MessageRedirect() {
	}

	public MessageRedirect(String redirectConfig) {
		this.redirectConfig = redirectConfig;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		redirectConfig = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, redirectConfig);
	}

	public String getRedirectConfig() {
		return redirectConfig;
	}

}
