// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.screenshot;

import java.io.File;

import com.google.gson.JsonObject;
import net.blay09.mods.eirairc.api.upload.UploadedFile;
import net.minecraft.client.Minecraft;

public class Screenshot {

	private static final String METADATA_ORIGINALNAME = "originalName";
	private static final String METADATA_UPLOADURL = "uploadURL";
	private static final String METADATA_DELETEURL = "deleteURL";
	private static final String METADATA_FAVORITE = "favorite";
	private static final String METADATA_TIMESTAP = "timestamp";

	private final File file;
	private final JsonObject metadata;

	public Screenshot(File file, JsonObject metadata) {
		this.file = file;
		this.metadata = metadata != null ? metadata : new JsonObject();
		if(metadata == null) {
			this.metadata.addProperty(METADATA_ORIGINALNAME, file.getName().substring(0, file.getName().length() - 4));
		}
	}

	public String getName() {
		return file.getName().substring(0, file.getName().length() - 4);
	}

	public boolean isUploaded() {
		return metadata.has(METADATA_UPLOADURL);
	}

	public File getFile() {
		return file;
	}

	public JsonObject getMetadata() {
		return metadata;
	}

	public String getUploadURL() {
		return metadata.get(METADATA_UPLOADURL).getAsString();
	}

	public String getOriginalName() {
		return metadata.get(METADATA_ORIGINALNAME).getAsString();
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		metadata.addProperty(METADATA_UPLOADURL, uploadedFile.url);
		metadata.addProperty(METADATA_DELETEURL, uploadedFile.deleteURL);
	}

	public boolean isFavorited() {
		return (metadata.has(METADATA_FAVORITE) && metadata.get(METADATA_FAVORITE).getAsBoolean());
	}

	public void setFavorited(boolean favorited) {
		metadata.addProperty(METADATA_FAVORITE, favorited);
	}
}
