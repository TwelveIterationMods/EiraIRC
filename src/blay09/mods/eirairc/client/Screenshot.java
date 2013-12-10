// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client;

import java.io.File;

public class Screenshot {

	private File file;
	private String uploadURL;
	
	public Screenshot(File file) {
		this.file = file;
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
	
}
