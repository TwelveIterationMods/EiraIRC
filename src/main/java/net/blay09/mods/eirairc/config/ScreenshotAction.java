// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config;

public enum ScreenshotAction {
	None,
	Upload,
	UploadShare,
	UploadClipboard;

	public static final ScreenshotAction[] values = values();
	public static final int MAX = values.length - 1;

	public static final String[] NAMES = new String[] {
		None.name(),
		Upload.name(),
		UploadShare.name(),
		UploadClipboard.name()
	};
}
