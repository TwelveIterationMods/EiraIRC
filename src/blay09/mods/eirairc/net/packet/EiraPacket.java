package blay09.mods.eirairc.net.packet;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import blay09.mods.eiratech.GlobalsET;

public abstract class EiraPacket {

	protected boolean isChunkDataPacket;
	
	public abstract void read(DataInputStream in) throws IOException;
	public abstract void write(DataOutputStream out) throws IOException;
	public void execute(INetworkManager manager, EntityPlayer player) { }
	public void executeClient(INetworkManager manager, EntityPlayer player) {
		execute(manager, player);
	}
	public void executeServer(INetworkManager manager, EntityPlayer player) {
		execute(manager, player);
	}
	
	public Packet createPacket() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bos);
		write(out);
		byte[] data = bos.toByteArray();
		out.close();
		Packet250CustomPayload packet250 = new Packet250CustomPayload();
		packet250.channel = GlobalsET.CHANNEL_NAME;
		packet250.data = data;
		packet250.length = data.length;
		packet250.isChunkDataPacket = isChunkDataPacket;
		return packet250;
	}
}
