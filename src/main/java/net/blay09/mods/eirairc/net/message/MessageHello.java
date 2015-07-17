// Copyright (c) 2015, Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.net.message;

import io.netty.buffer.ByteBuf;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

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
