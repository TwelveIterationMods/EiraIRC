package net.blay09.mods.eirairc.util;

import net.minecraft.util.EnumChatFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Blay09 on 01.09.2014.
 */
public enum IRCFormatting {

	BOLD("\u0002", "l"),
	UNDERLINE("\u001f", "n"),
	ITALIC("\u0016", "o"),
	RESET("\u000f", "r");

	public static final String MC_FORMATTING_PREFIX = "\u00a7";

	private static final Pattern ircColorPattern = Pattern.compile("\u0003([0-9][0-9]?)(?:[,][0-9][0-9]?)?");
	private static final IRCFormatting[] values = values();

	private final String ircCode;
	private final String mcCode;

	private IRCFormatting(String ircCode, String mcCode) {
		this.ircCode = ircCode;
		this.mcCode = MC_FORMATTING_PREFIX + mcCode;
	}

	public static String toIRC(String s, boolean killFormatting) {
		String result = s;
		for(IRCFormatting format : values) {
			result = result.replaceAll(format.mcCode, killFormatting ? "" : format.ircCode);
		}
		// TODO convert colors mc -> irc
		return result;
	}

	public static String toMC(String s, boolean killFormatting) {
		String result = s;
		for(IRCFormatting format : values) {
			result = result.replaceAll(format.ircCode, killFormatting ? "" : format.mcCode);
		}
		if(killFormatting) {
			result = ircColorPattern.matcher(result).replaceAll("");
		} else {
			Matcher matcher = ircColorPattern.matcher(result);
			while(matcher.find()) {
				String colorMatch = matcher.group(1);
				int colorCode = Integer.parseInt(colorMatch);
				EnumChatFormatting colorFormat = IRCFormatting.getColorFromIRCColorCode(colorCode);
				result = result.replaceFirst(Matcher.quoteReplacement(matcher.group()), MC_FORMATTING_PREFIX + colorFormat.getFormattingCode());
			}
		}
		return result;
	}

	public static EnumChatFormatting getColorFromIRCColorCode(int code) {
		switch(code) {
			case 0: return EnumChatFormatting.WHITE;
			case 1: return EnumChatFormatting.BLACK;
			case 2: return EnumChatFormatting.DARK_BLUE;
			case 3: return EnumChatFormatting.DARK_GREEN;
			case 4: return EnumChatFormatting.RED;
			case 5: return EnumChatFormatting.DARK_RED;
			case 6: return EnumChatFormatting.DARK_PURPLE;
			case 7: return EnumChatFormatting.GOLD;
			case 8: return EnumChatFormatting.YELLOW;
			case 9: return EnumChatFormatting.GREEN;
			case 10: return EnumChatFormatting.AQUA;
			case 11: return EnumChatFormatting.BLUE;
			case 12: return EnumChatFormatting.DARK_AQUA;
			case 13: return EnumChatFormatting.LIGHT_PURPLE;
			case 14: return EnumChatFormatting.DARK_GRAY;
			case 15: return EnumChatFormatting.GRAY;
		}
		return null;
	}

}
