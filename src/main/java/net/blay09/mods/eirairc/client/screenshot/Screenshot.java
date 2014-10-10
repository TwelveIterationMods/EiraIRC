// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.screenshot;

import java.io.File;

import com.google.gson.JsonObject;
import net.blay09.mods.eirairc.api.upload.UploadedFile;
import net.minecraft.client.Minecraft;

public class Screenshot {

	private final File file;
	private final JsonObject metadata;

	public Screenshot(File file, JsonObject metadata) {
		this.file = file;
		this.metadata = metadata != null ? metadata : new JsonObject();
	}

	public String getName() {
		return file.getName().substring(0, file.getName().length() - 4);
	}

	public boolean isUploaded() {
		return metadata.has("uploadURL");
	}

	public File getFile() {
		return file;
	}

	public JsonObject getMetadata() {
		return metadata;
	}

	public String getUploadURL() {
		return metadata.get("uploadURL").getAsString();
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		metadata.addProperty("uploadURL", uploadedFile.url);
		metadata.addProperty("deleteURL", uploadedFile.deleteURL);
	}
}
