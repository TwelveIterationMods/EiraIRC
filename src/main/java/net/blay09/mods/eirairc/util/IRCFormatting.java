package net.blay09.mods.eirairc.util;

import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.Map;
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

	public static final String IRC_COLOR_PREFIX = "\u0003";
	public static final String MC_FORMATTING_PREFIX = "\u00a7";

	private static final Pattern ircColorPattern = Pattern.compile("\u0003([0-9][0-9]?)(?:[,][0-9][0-9]?)?");
	private static final Pattern mcColorPattern = Pattern.compile("\u00a7([0-9a-f])");
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
		if(killFormatting) {
			Matcher matcher = mcColorPattern.matcher(result);
			while(matcher.find()) {
				result = result.replaceFirst(Matcher.quoteReplacement(matcher.group()), "");
			}
		} else {
			Matcher matcher = mcColorPattern.matcher(result);
			while(matcher.find()) {
				char mcColorCode = matcher.group(1).charAt(0);
				int ircColorCode = getIRCColorCodeFromMCColorCode(mcColorCode);
				result = result.replaceFirst(Matcher.quoteReplacement(matcher.group()), IRC_COLOR_PREFIX + ircColorCode);
			}
		}
		return result;
	}

	public static String toMC(String s, boolean killFormatting) {
		String result = s;
		for(IRCFormatting format : values) {
			result = result.replaceAll(format.ircCode, killFormatting ? "" : format.mcCode);
		}
		if(killFormatting) {
			Matcher matcher = ircColorPattern.matcher(result);
			while(matcher.find()) {
				result = result.replaceFirst(Matcher.quoteReplacement(matcher.group()), "");
			}
		} else {
			Matcher matcher = ircColorPattern.matcher(result);
			while(matcher.find()) {
				String colorMatch = matcher.group(1);
				int colorCode = Integer.parseInt(colorMatch);
				result = result.replaceFirst(Matcher.quoteReplacement(matcher.group()), MC_FORMATTING_PREFIX + IRCFormatting.getColorFromIRCColorCode(colorCode));
			}
		}
		return result;
	}

	public static char getColorFromIRCColorCode(int code) {
		switch(code) {
			case 0: return 'f'; // WHITE
			case 1: return '0'; // BLACK
			case 2: return '1'; // DARK_BLUE
			case 3: return '2'; // DARK_GREEN
			case 4: return 'c'; // RED
			case 5: return '4'; // DARK_RED
			case 6: return '5'; // DARK_PURPLE
			case 7: return '6'; // GOLD
			case 8: return 'e'; // YELLOW
			case 9: return 'a'; // GREEN
			case 10: return 'b'; // AQUA
			case 11: return '9'; // BLUE
			case 12: return '3'; // DARK_AQUA
			case 13: return 'd'; // LIGHT_PURPLE
			case 14: return '8'; // DARK_GRAY
			case 15: return '7'; // GRAY
		}
		return 'f';
	}

	public static EnumChatFormatting getColorFormattingFromIRCColorCode(int code) {
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

	public static int getIRCColorCodeFromMCColorCode(char colorCode) {
		switch(colorCode) {
			case '0': return 1; // black
			case '1': return 2; // dark blue
			case '2': return 3; // dark green
			case '3': return 12; // dark aqua
			case '4': return 5; // dark red
			case '5': return 6; // dark purple
			case '6': return 7; // gold
			case '7': return 15; // gray
			case '8': return 14; // dark gray
			case '9': return 11; // blue
			case 'a': return 9; // green
			case 'b': return 10; // aqua
			case 'c': return 4; // red
			case 'd': return 13; // light purple
			case 'e': return 8; // yellow
			case 'f': return 0; // white
		}
		return 1;
	}

	public static EnumChatFormatting getColorFromMCColorCode(char colorCode) {
		switch(colorCode) {
			case '0': return EnumChatFormatting.BLACK; // black
			case '1': return EnumChatFormatting.DARK_BLUE; // dark blue
			case '2': return EnumChatFormatting.DARK_GREEN; // dark green
			case '3': return EnumChatFormatting.DARK_AQUA; // dark aqua
			case '4': return EnumChatFormatting.DARK_RED; // dark red
			case '5': return EnumChatFormatting.DARK_PURPLE; // dark purple
			case '6': return EnumChatFormatting.GOLD; // gold
			case '7': return EnumChatFormatting.GRAY; // gray
			case '8': return EnumChatFormatting.DARK_GRAY; // dark gray
			case '9': return EnumChatFormatting.BLUE; // blue
			case 'a': return EnumChatFormatting.GREEN; // green
			case 'b': return EnumChatFormatting.AQUA; // aqua
			case 'c': return EnumChatFormatting.RED; // red
			case 'd': return EnumChatFormatting.LIGHT_PURPLE; // light purple
			case 'e': return EnumChatFormatting.YELLOW; // yellow
			case 'f': return EnumChatFormatting.WHITE; // white
		}
		return null;
	}

	private static final Map<String, EnumChatFormatting> twitchColorMap = new HashMap<String, EnumChatFormatting>();
	static {
		// TODO I'm not sure if this is the right approach anymore. There just keep popping up new colors! Might have to use the RGB values and pick the closest one instead?
		twitchColorMap.put("#008000", EnumChatFormatting.DARK_GREEN);
		twitchColorMap.put("#0000FF", EnumChatFormatting.BLUE);
		twitchColorMap.put("#1E90FF", EnumChatFormatting.BLUE);
		twitchColorMap.put("#FF0000", EnumChatFormatting.RED);
		twitchColorMap.put("#006666", EnumChatFormatting.AQUA);
		twitchColorMap.put("#B22222", EnumChatFormatting.DARK_RED);
		twitchColorMap.put("#FF7F50", EnumChatFormatting.GOLD);
		twitchColorMap.put("#9ACD32", EnumChatFormatting.GREEN);
		twitchColorMap.put("#FF4500", EnumChatFormatting.GOLD);
		twitchColorMap.put("#2E8B57", EnumChatFormatting.DARK_AQUA);
		twitchColorMap.put("#DAA520", EnumChatFormatting.YELLOW);
		twitchColorMap.put("#D2691E", EnumChatFormatting.GOLD);
		twitchColorMap.put("#5F9EA0", EnumChatFormatting.AQUA);
		twitchColorMap.put("#FF69B4", EnumChatFormatting.LIGHT_PURPLE);
		twitchColorMap.put("#8A2BE2", EnumChatFormatting.LIGHT_PURPLE);
		twitchColorMap.put("#00FF7F", EnumChatFormatting.GREEN);
		twitchColorMap.put("#66CC00", EnumChatFormatting.GREEN);
		twitchColorMap.put("#0099E6", EnumChatFormatting.BLUE);
		twitchColorMap.put("#000000", EnumChatFormatting.GRAY);
		twitchColorMap.put("#43005C", EnumChatFormatting.LIGHT_PURPLE);
		twitchColorMap.put("#6BD5FF", EnumChatFormatting.BLUE);
		twitchColorMap.put("#FF6BB5", EnumChatFormatting.LIGHT_PURPLE);
		twitchColorMap.put("#AF551D", EnumChatFormatting.GOLD);
	}

	public static EnumChatFormatting getColorFromTwitch(String twitchColor) {
		EnumChatFormatting color = twitchColorMap.get(twitchColor);
		if(color == null) {
			System.out.println("Unknown Twitch Name Color: " + twitchColor);
		}
		return color != null ? color : EnumChatFormatting.WHITE;
	}
}
