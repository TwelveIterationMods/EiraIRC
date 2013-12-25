package blay09.mods.eirairc.net;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import blay09.mods.eirairc.net.packet.EiraPacket;
import blay09.mods.eirairc.net.packet.PacketType;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public abstract class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		ByteArrayInputStream bin = new ByteArrayInputStream(packet.data);
		DataInputStream in = new DataInputStream(bin);
		try {
			byte msgType = in.readByte();
			PacketType packetType = PacketType.getPacketType(msgType);
			if(packetType != null) {
				EiraPacket subPacket = packetType.newInstance();
				if(subPacket != null) {
					subPacket.read(in);
				}
				execute(subPacket, manager, player);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract void execute(EiraPacket packet, INetworkManager manager, Player player);
}
