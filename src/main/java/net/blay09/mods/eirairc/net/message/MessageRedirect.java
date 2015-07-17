// Copyright (c) 2015 Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.net.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.blay09.mods.eirairc.util.Utils;

public class MessageRedirect implements IMessage {

	private String redirectConfig;

	public MessageRedirect() {
	}

	public MessageRedirect(String redirectConfig) {
		this.redirectConfig = redirectConfig;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		redirectConfig = Utils.readString(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		Utils.writeString(buf, redirectConfig);
	}

	public String getRedirectConfig() {
		return redirectConfig;
	}

}
