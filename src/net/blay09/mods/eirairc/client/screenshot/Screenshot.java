// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.screenshot;

import java.io.File;

import net.minecraft.client.Minecraft;

public class Screenshot {

	private File file;
	private String uploadURL;
	private ScreenshotThumbnail thumbnail;
	
	public Screenshot(File file) {
		this.file = file;
		loadThumbnail();
	}

	public String getName() {
		return file.getName().substring(0, file.getName().length() - 4);
	}

	public boolean isUploaded() {
		return uploadURL != null;
	}

	public void setURL(String url) {
		this.uploadURL = url;
	}

	public String getUploadURL() {
		return uploadURL;
	}

	public File getFile() {
		return file;
	}
	
	public ScreenshotThumbnail getThumbnail() {
		return thumbnail;
	}
	
	public void loadThumbnail() {
		thumbnail = new ScreenshotThumbnail(file);
		Minecraft.getMinecraft().getTextureManager().loadTexture(thumbnail.getResourceLocation(), thumbnail);
	}
}
