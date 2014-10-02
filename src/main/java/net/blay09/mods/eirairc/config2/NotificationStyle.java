package net.blay09.mods.eirairc.config2;

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
}
