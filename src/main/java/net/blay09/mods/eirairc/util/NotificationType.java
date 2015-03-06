// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

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
