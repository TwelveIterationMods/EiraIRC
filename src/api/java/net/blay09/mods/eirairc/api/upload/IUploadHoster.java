package net.blay09.mods.eirairc.api.upload;

import java.io.File;

public interface IUploadHoster {

	public String getName();
	public abstract UploadedFile uploadFile(File file, int uploadBufferSize);
	public abstract boolean isCustomizable();
	
}
