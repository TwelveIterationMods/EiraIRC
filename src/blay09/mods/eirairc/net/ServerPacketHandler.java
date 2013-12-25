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

public class ServerPacketHandler extends PacketHandler {

	@Override
	public void execute(EiraPacket packet, INetworkManager manager, Player player) {
		packet.executeServer(manager, (EntityPlayer) player);
	}
	
}
