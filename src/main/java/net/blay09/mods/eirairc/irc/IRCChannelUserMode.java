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
<<<<<<< HEAD
		for (IRCChannelUserMode value : values) {
=======
		for(IRCChannelUserMode value : values) {
>>>>>>> d248e1685dde1dafba3323d197ad61200374c3a9
			if (value.modeChar == c) {
				return value;
			}
		}
		return null;
	}
}
