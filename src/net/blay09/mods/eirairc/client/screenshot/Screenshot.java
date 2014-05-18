// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.screenshot;

import java.io.File;

import net.minecraft.client.Minecraft;

public class Screenshot {

	private File file;
	private String uploadURL;
	private String deleteURL;
	private String hoster;
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
	
	public void setDeleteURL(String url) {
		this.deleteURL = url;
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

	public String getDeleteURL() {
		return deleteURL;
	}

	public String getHoster() {
		return hoster;
	}
	
	public void setHoster(String hoster) {
		this.hoster = hoster;
	}
}
