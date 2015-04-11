// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.
package net.blay09.mods.eirairc.api.upload;

import java.io.File;

/**
 * An interface to use for upload hosters that can be used to upload screenhots to.
 */
public interface UploadHoster {

	/**
	 * @return the name of this upload hoster
	 */
	String getName();

	/**
	 * @param file the screenshot file to be uploaded
	 * @param uploadBufferSize the buffer size to use for the upload as specified in the configuration
	 * @return a new instance of {@code UploadedFile} or null if the upload failed
	 */
	UploadedFile uploadFile(File file, int uploadBufferSize);

}
