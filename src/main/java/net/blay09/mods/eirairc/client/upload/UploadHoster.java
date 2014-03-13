// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.upload;

import java.io.File;

public abstract class UploadHoster {

	public static final String HOSTER_DIRECTUPLOAD = "DirectUpload";
	public static final String HOSTER_IMGUR = "imgur";
	
	public static final String[] availableHosters = new String[] {
		HOSTER_DIRECTUPLOAD,
		HOSTER_IMGUR
	};
	
	
	public abstract String uploadFile(File file);
	public abstract boolean isCustomizable();
	
	private static String cachedHosterName;
	private static UploadHoster cachedHoster;
	public static UploadHoster getUploadHoster(String name) {
		if(cachedHoster != null && cachedHosterName.equals(name)) {
			return cachedHoster;
		}
		try {
			Class<?> clazz = getUploadHosterClass(name);
			if(clazz != null) {
				cachedHoster = (UploadHoster) clazz.newInstance();
				cachedHosterName = name;
				return cachedHoster;
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static Class<?> getUploadHosterClass(String name) {
		if(name.equals(HOSTER_DIRECTUPLOAD)) {
			return DirectUpload.class;
		} else if(name.equals(HOSTER_IMGUR)) {
			return Imgur.class;
		}
		return null;
	}
}
