// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.net.packet;

public enum PacketType {
	Hello(0, PacketHello.class),
	RecLiveState(1, PacketRecLiveState.class), 
	Notification(2, PacketNotification.class);

	private static PacketType[] packetRegister;
	
	public static void registerPacket(int id, PacketType packetType) {
		if(packetRegister == null) {
			packetRegister = new PacketType[10];
		}
		packetRegister[id] = packetType;
	}
	
	public static PacketType getPacketType(int id) {
		return packetRegister[id];
	}
	
	private int id;
	private Class<? extends EiraPacket> clazz;
	
	private PacketType(int id, Class<? extends EiraPacket> clazz) {
		this.id = id;
		this.clazz = clazz;
		
		registerPacket(id, this);
	}
	
	public Class<? extends EiraPacket> getPacketClass() {
		return clazz;
	}
	
	public EiraPacket newInstance() {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getId() {
		return id;
	}
	
}
