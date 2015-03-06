package net.blay09.mods.eirairc.irc;

/**
 * Created by Blay09 on 18.02.2015.
 */
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

	private IRCChannelUserMode(char modeChar) {
		this.modeChar = modeChar;
	}

	public static IRCChannelUserMode fromChar(char c) {
		for(int i = 0; i < values.length; i++) {
			if(values[i].modeChar == c) {
				return values[i];
			}
		}
		return null;
	}
}
