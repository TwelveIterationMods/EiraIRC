// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.config;

import java.util.List;

import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;

public class NotificationConfig {

	private static final String CATEGORY = ConfigurationHandler.CATEGORY_NOTIFICATIONS;
	
	private static final String PREFIX_OPTION = "notify";
	private static final String OPTION_FRIENDJOIN = "notifyFriendJoined";
	private static final String OPTION_MENTIONED = "notifyNameMentioned";
	private static final String OPTION_RECORDING = "notifyUserRecording";
	private static final String OPTION_PRIVATEMESSAGE = "notifyPrivateMessage";
	
	private static final String OPTION_SOUNDNAME = "sound";
	private static final String OPTION_SOUNDVOLUME = "soundVolume";
	private static final String OPTION_SOUNDPITCH = "soundPitch";
	
	private static final String SVALUE_NONE = "none";
	private static final String SVALUE_SOUNDONLY = "soundonly";
	private static final String SVALUE_TEXTONLY = "textonly";
	private static final String SVALUE_TEXTSOUND = "textsound";
	
	private static final String DEFAULT_SOUNDNAME = "note.harp";
	
	public static final int VALUE_NONE = 0;
	public static final int VALUE_TEXTONLY = 1;
	public static final int VALUE_SOUNDONLY = 2;
	public static final int VALUE_TEXTANDSOUND = 3;
	
	public static int friendJoined = VALUE_TEXTONLY;
	public static int nameMentioned = VALUE_TEXTANDSOUND;
	public static int userRecording = VALUE_TEXTANDSOUND;
	public static int privateMessage = VALUE_TEXTONLY;
	
	public static String notificationSound = DEFAULT_SOUNDNAME;
	public static float soundVolume = 1f;
	public static float soundPitch = 1f;
	
	public static void load(Configuration config) {
		friendJoined = config.get(CATEGORY, OPTION_FRIENDJOIN, friendJoined).getInt();
		nameMentioned = config.get(CATEGORY, OPTION_MENTIONED, nameMentioned).getInt();
		userRecording = config.get(CATEGORY, OPTION_RECORDING, userRecording).getInt();
		privateMessage = config.get(CATEGORY, OPTION_PRIVATEMESSAGE, privateMessage).getInt();
		notificationSound = config.get(CATEGORY, OPTION_SOUNDNAME, notificationSound).getString();
		soundVolume = (float) config.get(CATEGORY, OPTION_SOUNDVOLUME, soundVolume).getDouble(soundVolume);
		soundPitch = (float) config.get(CATEGORY, OPTION_SOUNDPITCH, soundPitch).getDouble(soundPitch);
	}

	public static void save(Configuration config) {
		config.get(CATEGORY, OPTION_FRIENDJOIN, friendJoined).set(friendJoined);
		config.get(CATEGORY, OPTION_MENTIONED, nameMentioned).set(nameMentioned);
		config.get(CATEGORY, OPTION_RECORDING, userRecording).set(userRecording);
		config.get(CATEGORY, OPTION_PRIVATEMESSAGE, privateMessage).set(privateMessage);
		config.get(CATEGORY, OPTION_SOUNDNAME, notificationSound).set(notificationSound);
		config.get(CATEGORY, OPTION_SOUNDVOLUME, soundVolume).set(soundVolume);
		config.get(CATEGORY, OPTION_SOUNDPITCH, soundPitch).set(soundPitch);
	}
	
	public static void addOptionsToList(List<String> list) {
		list.add(OPTION_FRIENDJOIN);
		list.add(OPTION_MENTIONED);
		list.add(OPTION_RECORDING);
		list.add(OPTION_PRIVATEMESSAGE);
		list.add(OPTION_SOUNDNAME);
		list.add(OPTION_SOUNDVOLUME);
		list.add(OPTION_SOUNDPITCH);
	}
	
	public static void addValuesToList(List<String> list, String option) {
		if(option.equals(OPTION_SOUNDNAME)) {
			list.add(DEFAULT_SOUNDNAME);
		} else if(option.equals(OPTION_SOUNDVOLUME) || option.equals(OPTION_SOUNDPITCH)) {
			list.add("1.0");
		} else if(option.startsWith(PREFIX_OPTION)) {
			list.add(SVALUE_NONE);
			list.add(SVALUE_TEXTONLY);
			list.add(SVALUE_SOUNDONLY);
			list.add(SVALUE_TEXTSOUND);
		}
	}
	
	public static String handleConfigCommand(ICommandSender sender, String key) {
		String value = null;
		if(key.equals(OPTION_SOUNDNAME)) value = notificationSound;
		else if(key.equals(OPTION_SOUNDVOLUME)) value = String.valueOf(soundVolume);
		else if(key.equals(OPTION_SOUNDPITCH)) value = String.valueOf(soundPitch);
		else {
			int action = 0;
			if(key.equals(OPTION_FRIENDJOIN)) {
				action = friendJoined;
			} else if(key.equals(OPTION_MENTIONED)) {
				action = nameMentioned;
			} else if(key.equals(OPTION_RECORDING)) {
				action = userRecording;
			} else if(key.equals(OPTION_PRIVATEMESSAGE)) {
				action = privateMessage;
			}
			switch(action) {
			case VALUE_NONE: value = SVALUE_NONE; break;
			case VALUE_TEXTONLY: value = SVALUE_TEXTONLY; break;
			case VALUE_SOUNDONLY: value = SVALUE_SOUNDONLY; break;
			case VALUE_TEXTANDSOUND: value = SVALUE_TEXTSOUND; break;
			}
		}
		return value;
	}
	
	public static boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		if(key.equals(OPTION_SOUNDNAME)) {
			notificationSound = value;
		} else if(key.equals(OPTION_SOUNDVOLUME)) {
			soundVolume = Float.parseFloat(value);
		} else if(key.equals(OPTION_SOUNDPITCH)) {
			soundPitch = Float.parseFloat(value);
		} else {
			int action = 0;
			if(value.equals(SVALUE_TEXTONLY)) {
				action = VALUE_TEXTONLY;
			} else if(value.equals(SVALUE_SOUNDONLY)) {
				action = VALUE_SOUNDONLY;
			} else if(value.equals(SVALUE_TEXTSOUND)) {
				action = VALUE_TEXTANDSOUND;
			}
			if(key.equals(OPTION_FRIENDJOIN)) {
				friendJoined = action;
			} else if(key.equals(OPTION_MENTIONED)) {
				nameMentioned = action;
			} else if(key.equals(OPTION_RECORDING)) {
				userRecording = action;
			} else if(key.equals(OPTION_PRIVATEMESSAGE)) {
				privateMessage = action;
			} else {
				return false;
			}
		}
		return true;
	}
	
}
