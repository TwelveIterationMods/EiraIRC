// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;

public class PacketHello extends EiraPacket {

	private String version;
	
	public PacketHello() {
		super(PacketType.Hello);
	}
	
	public PacketHello(String version) {
		this();
		this.version = version;
	}
	
	@Override
	public void read(DataInputStream in) throws IOException {
		version = in.readUTF();
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeUTF(version);
	}

	@Override
	public void executeServer(INetworkManager manager, EntityPlayer player) {
		EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(player.username);
		playerInfo.modVersion = version;
	}

	@Override
	public void executeClient(INetworkManager manager, EntityPlayer player) {
	}
}
