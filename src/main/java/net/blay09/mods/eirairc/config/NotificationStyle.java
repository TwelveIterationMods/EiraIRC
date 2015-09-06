package net.blay09.mods.eirairc.config;

 // Notifications
public enum NotificationStyle {
	None,
	TextOnly,
	SoundOnly,
	TextAndSound;

	public static final NotificationStyle[] values = values();
	public static final int MAX = values.length - 1;

	public static final String[] NAMES = new String[] {
		None.name(),
		TextOnly.name(),
		SoundOnly.name(),
		TextAndSound.name()
	};
}
