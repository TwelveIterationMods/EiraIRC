// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.util;

public class IRCResolver {

	public static String stripPath(String path) {
		int serverIdx = path.indexOf('/');
		if(serverIdx != -1) {
			return path.substring(serverIdx + 1);
		}
		return path;
	}

	public static boolean isChannel(String path) {
		path = stripPath(path);
		if(!Character.isAlphabetic(path.charAt(0))) {
			return true;
		}
		return false;
	}
}
