package net.blay09.mods.eirairc.config;

/**
 * Created by Blay09 on 02.10.2014.
 */
public enum ScreenshotAction {
	None,
	Upload,
	UploadShare,
	UploadClipboard;

	public static final ScreenshotAction[] values = values();
	public static final int MAX = values.length - 1;
}
