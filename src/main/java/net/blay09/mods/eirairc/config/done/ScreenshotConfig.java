// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config.done;

import java.util.List;

import net.blay09.mods.eirairc.api.upload.UploadManager;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

public class ScreenshotConfig {
	
	private static final String CATEGORY = ConfigurationHandler.CATEGORY_CLIENTONLY;
	
	public static final int VALUE_NONE = 0;
	public static final int VALUE_UPLOAD = 1;
	public static final int VALUE_UPLOADSHARE = 2;
	public static final int VALUE_UPLOADCLIPBOARD = 3;
	
	public static int screenshotAction = 0; // client
	public static String uploadHoster = "imgur"; // client
	public static int uploadBufferSize = 1024; // client
	
	public static void load(Configuration config) {
		screenshotAction = config.get(CATEGORY, "screenshotAction", screenshotAction).getInt();
		config.get(CATEGORY, "screenshotAction", screenshotAction).comment = "0: None / 1: Upload / 2: Upload & Share / 3: Upload & Clipboard";
		uploadHoster = Utils.unquote(config.get(CATEGORY, "uploadHoster", uploadHoster).getString());
		config.get(CATEGORY, "uploadHoster", uploadHoster).comment = "Available Options: DirectUpload, imgur";
	}
	
	public static void save(Configuration config) {
		config.get(CATEGORY, "screenshotAction", screenshotAction).set(screenshotAction);
		config.get(CATEGORY, "uploadHoster", uploadHoster).set(Utils.quote(uploadHoster));
	}
	
	public static void addOptionsToList(List<String> list) {
		list.add("screenshotAction");
		list.add("uploadHoster");
	}
	
	public static void addValuesToList(List<String> list, String option) {
		if(option.equals("uploadHoster")) {
			for(int i = 0; i < UploadManager.getAvailableHosters().length; i++) {
				list.add(UploadManager.getAvailableHosters()[i]);
			}
		} else if(option.equals("screenshotAction")) {
			list.add("none");
			list.add("upload");
			list.add("share");
			list.add("clipboard");
		}
	}
	
	public static String handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(key.equals("uploadHoster")) {
		 	value = uploadHoster;
		} else if(key.equals("screenshotAction")) {
			switch(screenshotAction) {
				case VALUE_NONE: value = "none"; break;
				case VALUE_UPLOAD: value = "upload"; break;
				case VALUE_UPLOADSHARE: value = "share"; break;
				case VALUE_UPLOADCLIPBOARD: value = "clipboard"; break;
			}
		}
		return value;
	}
	
	public static boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		if(key.equals("uploadHoster")) {
			uploadHoster = value;
		} else if(key.equals("screenshotAction")) {
			if(value.equals("none")) {
				screenshotAction = VALUE_NONE;
			} else if(value.equals("upload")) {
				screenshotAction = VALUE_UPLOAD;
			} else if(value.equals("share")) {
				screenshotAction = VALUE_UPLOADSHARE;
			} else if(value.equals("clipboard")) {
				screenshotAction = VALUE_UPLOADCLIPBOARD;
			}
		} else {
			return false;
		}
		return true;
	}
	
}
