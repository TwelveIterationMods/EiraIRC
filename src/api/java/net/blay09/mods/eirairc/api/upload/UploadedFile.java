// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.api.upload;

public class UploadedFile {

	public final String url;
	public final String directURL;
	public final String deleteURL;

	/**
	 * @param url the default URL to the uploaded screenshot
	 * @param directURL the direct URL to the uploaded screenshot (in image format)
	 * @param deleteURL the URL to delete this screenshot or null if deleting is not possible
	 */
	public UploadedFile(String url, String directURL, String deleteURL) {
		this.url = url;
		this.directURL = directURL;
		this.deleteURL = deleteURL;
	}
	
}
