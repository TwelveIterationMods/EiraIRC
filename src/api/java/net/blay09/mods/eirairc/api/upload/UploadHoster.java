package net.blay09.mods.eirairc.api.upload;

import java.io.File;

public interface UploadHoster {

	public String getName();
	public UploadedFile uploadFile(File file, int uploadBufferSize);

}
