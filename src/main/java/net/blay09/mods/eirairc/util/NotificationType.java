// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.util;

public enum NotificationType {
	FriendJoined,
	PlayerMentioned,
	PrivateMessage;

	private static NotificationType[] values = values();
	public static NotificationType fromId(int id) {
		return values[id];
	}

}
