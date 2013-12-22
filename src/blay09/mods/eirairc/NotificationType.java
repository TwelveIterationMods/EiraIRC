package blay09.mods.eirairc;

public enum NotificationType {
FriendJoined,
PlayerMentioned,
UserRecording,
PrivateMessage;

	private static NotificationType[] values = values();
	public static NotificationType fromId(int id) {
		return values[id];
	}

}
