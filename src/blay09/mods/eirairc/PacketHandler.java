package blay09.mods.eirairc;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	private static final byte MSG_HELLO = 0;
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		ByteArrayInputStream bin = new ByteArrayInputStream(packet.data);
		DataInputStream in = new DataInputStream(bin);
		try {
			byte msgType = in.readByte();
			switch(msgType) {
			case MSG_HELLO: break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
