// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.EiraIRC;

public class Globals {

	public static final String MOD_ID = EiraIRC.MOD_ID;

	public static final String UPDATE_URL = "https://raw.githubusercontent.com/blay09/EiraIRC/master/version.json";
	public static final String TWITCH_SERVER = "irc.twitch.tv";
	public static final String TWITCH_OAUTH = "http://twitchapps.com/tmi/";

	public static final String[] DEFAULT_COLOR_BLACKLIST = new String[] { "black", "darkgray" };
	public static final int TEXT_COLOR = 16777215;

	public static final int CHAT_MAX_LENGTH = 100;

	public static final String DEFAULT_NICK = "%USERNAME%";
	public static final String DEFAULT_CHARSET = "UTF-8";

	public static final String NBT_EIRAIRC = "EiraIRC";
	public static final String NBT_NAMECOLOR_LEGACY = "NameColor";
	public static final String NBT_NAMECOLOR = "NameColorID";

}
