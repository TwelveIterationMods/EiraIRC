// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.config;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;
import blay09.mods.eirairc.handler.ConfigurationHandler;

public class NotificationConfig {

	private static final String CATEGORY = ConfigurationHandler.CATEGORY_CLIENTONLY;
	
	public static final int VALUE_NONE = 0;
	public static final int VALUE_TEXTONLY = 1;
	public static final int VALUE_SOUNDONLY = 2;
	public static final int VALUE_TEXTANDSOUND = 3;
	
	public static int friendJoined = VALUE_TEXTONLY;
	public static int nameMentioned = VALUE_TEXTANDSOUND;
	public static int userRecording = VALUE_TEXTANDSOUND;
	public static int privateMessage = VALUE_TEXTONLY;
	
	public static String notificationSound = "note.harp";
	public static float soundVolume = 1f;
	public static float soundPitch = 1f;
	
	public static void load(Configuration config) {
		friendJoined = config.get(CATEGORY, "friendJoined", friendJoined).getInt();
		nameMentioned = config.get(CATEGORY, "nameMentioned", nameMentioned).getInt();
		userRecording = config.get(CATEGORY, "userRecording", userRecording).getInt();
		privateMessage = config.get(CATEGORY, "privateMessage", privateMessage).getInt();
		notificationSound = config.get(CATEGORY, "notificationSound", notificationSound).getString();
		soundVolume = (float) config.get(CATEGORY, "soundVolume", soundVolume).getDouble(soundVolume);
		soundPitch = (float) config.get(CATEGORY, "soundPitch", soundPitch).getDouble(soundPitch);
	}

	public static void save(Configuration config) {
		config.get(CATEGORY, "friendJoined", friendJoined).set(friendJoined);
		config.get(CATEGORY, "nameMentioned", nameMentioned).set(nameMentioned);
		config.get(CATEGORY, "userRecording", userRecording).set(userRecording);
		config.get(CATEGORY, "privateMessage", privateMessage).set(privateMessage);
		config.get(CATEGORY, "notificationSound", notificationSound).set(notificationSound);
		config.get(CATEGORY, "soundVolume", soundVolume).set(soundVolume);
		config.get(CATEGORY, "soundPitch", soundPitch).set(soundPitch);
	}
	
	public static void addOptionsToList(List<String> list) {
		list.add("friendJoined");
		list.add("nameMentioned");
		list.add("userRecording");
		list.add("privateMessage");
		list.add("notificationSound");
		list.add("soundVolume");
		list.add("soundPitch");
	}
	
	public static void addValuesToList(List<String> list, String option) {
		if(option.equals("notificationSound")) {
			list.add("note.harp");
		} else if(option.equals("soundVolume") || option.equals("soundPitch")) {
			list.add("1.0");
		} else {
			list.add("none");
			list.add("textonly");
			list.add("soundonly");
			list.add("textsound");
		}
	}
	
	public static String handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(key.equals("notificationSound")) value = notificationSound;
		else if(key.equals("soundVolume")) value = String.valueOf(soundVolume);
		else if(key.equals("soundPitch")) value = String.valueOf(soundPitch);
		else {
			int action = 0;
			if(key.equals("friendJoined")) {
				action = friendJoined;
			} else if(key.equals("nameMentioned")) {
				action = nameMentioned;
			} else if(key.equals("userRecording")) {
				action = userRecording;
			} else if(key.equals("privateMessage")) {
				action = privateMessage;
			}
			switch(action) {
			case VALUE_NONE: value = "none"; break;
			case VALUE_TEXTONLY: value = "textonly"; break;
			case VALUE_SOUNDONLY: value = "soundonly"; break;
			case VALUE_TEXTANDSOUND: value = "textsound"; break;
			}
		}
		return value;
	}
	
	public static boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		if(key.equals("notificationSound")) {
			notificationSound = value;
		} else if(key.equals("soundVolume")) {
			soundVolume = Float.parseFloat(value);
		} else if(key.equals("soundPitch")) {
			soundPitch = Float.parseFloat(value);
		} else {
			int action = 0;
			if(value.equals("textonly")) {
				action = VALUE_TEXTONLY;
			} else if(value.equals("soundonly")) {
				action = VALUE_SOUNDONLY;
			} else if(value.equals("textsound")) {
				action = VALUE_TEXTANDSOUND;
			}
			if(key.equals("friendJoined")) {
				friendJoined = action;
			} else if(key.equals("nameMentioned")) {
				nameMentioned = action;
			} else if(key.equals("userRecording")) {
				userRecording = action;
			} else if(key.equals("privateMessage")) {
				privateMessage = action;
			} else {
				return false;
			}
		}
		return true;
	}
	
}
