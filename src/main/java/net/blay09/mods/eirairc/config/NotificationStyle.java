package net.blay09.mods.eirairc.config;

/**
* Created by Blay09 on 02.10.2014.
*/ // Notifications
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
