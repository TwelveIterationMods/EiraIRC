// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.api.upload;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UploadManager {

	private static Map<String, IUploadHoster> uploadHosters = new HashMap<String, IUploadHoster>();
	private static String[] availableHosters;
	
	public static IUploadHoster getUploadHoster(String name) {
		return uploadHosters.get(name);
	}
	
	public static void registerUploadHoster(IUploadHoster hoster) {
		uploadHosters.put(hoster.getName(), hoster);
	}

	public static Collection<IUploadHoster> getUploadHosters() {
		return uploadHosters.values();
	}

	public static String[] getAvailableHosters() {
		if(availableHosters == null) {
			availableHosters = uploadHosters.keySet().toArray(new String[uploadHosters.size()]);
		}
		return availableHosters;
	}

	public static boolean isValidHoster(String name) {
		return uploadHosters.containsKey(name);
	}
}
