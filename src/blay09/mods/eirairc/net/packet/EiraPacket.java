// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.net.packet;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import blay09.mods.eirairc.util.Globals;

public abstract class EiraPacket {

	protected PacketType packetType;
	protected boolean isChunkDataPacket;
	
	public EiraPacket(PacketType packetType) {
		this.packetType = packetType;
	}

	public abstract void read(DataInputStream in) throws IOException;

	public abstract void write(DataOutputStream out) throws IOException;

	public void execute(INetworkManager manager, EntityPlayer player) {
	}

	public void executeClient(INetworkManager manager, EntityPlayer player) {
		execute(manager, player);
	}

	public void executeServer(INetworkManager manager, EntityPlayer player) {
		execute(manager, player);
	}

	public Packet createPacket() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bos);
		try {
			out.writeByte(packetType.getId());
			write(out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		byte[] data = bos.toByteArray();
		Packet250CustomPayload packet250 = new Packet250CustomPayload();
		packet250.channel = Globals.MOD_ID;
		packet250.data = data;
		packet250.length = data.length;
		packet250.isChunkDataPacket = isChunkDataPacket;
		return packet250;
	}
}
