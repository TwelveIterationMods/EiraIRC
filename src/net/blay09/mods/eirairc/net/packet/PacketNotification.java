// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.blay09.mods.eirairc.util.NotificationType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.network.Player;

public class PacketNotification extends EiraPacket {

	private int typeId;
	private String text;
	
	public PacketNotification() {
		super(PacketType.Notification);
	}
	
	public PacketNotification(NotificationType type, String text) {
		this();
		this.typeId = type.ordinal();
		this.text = text;
	}
	
	@Override
	public void read(DataInputStream in) throws IOException {
		typeId = in.readByte();
		text = in.readUTF();
	}

	@Override
	public void write(DataOutputStream out) throws IOException {
		out.writeByte(typeId);
		out.writeUTF(text);
	}

	@Override
	public void executeClient(INetworkManager manager, EntityPlayer player) {
		EiraIRC.proxy.publishNotification(NotificationType.fromId(typeId), text);
	}
}
