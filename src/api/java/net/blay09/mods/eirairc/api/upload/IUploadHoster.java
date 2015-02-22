package net.blay09.mods.eirairc.api.upload;

import java.io.File;

public interface IUploadHoster {

	public String getName();
	public abstract UploadedFile uploadFile(File file);
	public abstract boolean isCustomizable();
	
}
