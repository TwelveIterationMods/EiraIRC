// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.util;

public enum NotificationType {
FriendJoined,
PlayerMentioned,
UserRecording,
PrivateMessage, 
UserLive;

	private static NotificationType[] values = values();
	public static NotificationType fromId(int id) {
		return values[id];
	}

}
