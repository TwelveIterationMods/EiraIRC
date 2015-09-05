// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.irc;

public enum IRCChannelUserMode {
	VOICE('v'), // RFC1459
	HALFOP('h'), // RFC2811
	OPER('o'), // RFC1459
	CREATOR('O'), // RFC2811
	FOUNDER('u'), // tr-ircd
	CHANNEL_OWNER('q'), // Unreal
	CHANNEL_PROTECTION('a'), // Unreal
	SERVICE('!'); // KineIRCd

	private static final IRCChannelUserMode[] values = values();

	public final char modeChar;

	IRCChannelUserMode(char modeChar) {
		this.modeChar = modeChar;
	}

	public static IRCChannelUserMode fromChar(char c) {
		for(IRCChannelUserMode value : values) {
			if (value.modeChar == c) {
				return value;
			}
		}
		return null;
	}
}
