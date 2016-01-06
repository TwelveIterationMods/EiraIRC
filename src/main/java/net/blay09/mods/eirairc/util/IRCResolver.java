// Copyright (c) 2015, Christopher "BlayTheNinth" Baker

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
		return !Character.isAlphabetic(path.charAt(0));
	}
}
