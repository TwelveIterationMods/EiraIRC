package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.api.irc.IRCChannel;
import net.blay09.mods.eirairc.api.irc.IRCUser;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.config.settings.ThemeColorComponent;
import net.blay09.mods.eirairc.config.settings.ThemeSettings;
import net.blay09.mods.eirairc.irc.IRCUserImpl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
	private static final EnumChatFormatting[] mcChatFormatting = EnumChatFormatting.values();

	private final String ircCode;
	private final String mcCode;

	IRCFormatting(String ircCode, String mcCode) {
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

	public static void addValidColorsToList(List<String> list) {
		for(EnumChatFormatting mcFormatting : mcChatFormatting) {
			list.add(mcFormatting.name().toLowerCase());
		}
	}

	@Deprecated
	public static boolean isValidColorLegacy(String colorName) {
		EnumChatFormatting colorFormatting = getColorFormattingLegacy(colorName);
		return colorFormatting != null && colorFormatting.isColor();
	}

	@Deprecated
	public static EnumChatFormatting getColorFormattingLegacy(String colorName) {
		if(colorName == null || colorName.isEmpty()) {
			return null;
		}
		colorName = colorName.toLowerCase();
		EnumChatFormatting colorFormatting = null;
		if(colorName.equals("black")) {
			colorFormatting = EnumChatFormatting.BLACK;
		} else if(colorName.equals("darkblue") || colorName.equals("dark blue")) {
			colorFormatting = EnumChatFormatting.DARK_BLUE;
		} else if(colorName.equals("green")) {
			colorFormatting = EnumChatFormatting.DARK_GREEN;
		} else if(colorName.equals("cyan")) {
			colorFormatting = EnumChatFormatting.DARK_AQUA;
		} else if(colorName.equals("darkred") || colorName.equals("dark red")) {
			colorFormatting = EnumChatFormatting.DARK_RED;
		} else if(colorName.equals("purple")) {
			colorFormatting = EnumChatFormatting.DARK_PURPLE;
		} else if(colorName.equals("gold") || colorName.equals("orange")) {
			colorFormatting = EnumChatFormatting.GOLD;
		} else if(colorName.equals("gray") || colorName.equals("grey")) {
			colorFormatting = EnumChatFormatting.GRAY;
		} else if(colorName.equals("darkgray") || colorName.equals("darkgrey") || colorName.equals("dark gray") || colorName.equals("dark grey")) {
			colorFormatting = EnumChatFormatting.DARK_GRAY;
		} else if(colorName.equals("blue")) {
			colorFormatting = EnumChatFormatting.BLUE;
		} else if(colorName.equals("lime")) {
			colorFormatting = EnumChatFormatting.GREEN;
		} else if(colorName.equals("lightblue") || colorName.equals("light blue")) {
			colorFormatting = EnumChatFormatting.AQUA;
		} else if(colorName.equals("red")) {
			colorFormatting = EnumChatFormatting.RED;
		} else if(colorName.equals("magenta") || colorName.equals("pink")) {
			colorFormatting = EnumChatFormatting.LIGHT_PURPLE;
		} else if(colorName.equals("yellow")) {
			colorFormatting = EnumChatFormatting.YELLOW;
		} else if(colorName.equals("white")) {
			colorFormatting = EnumChatFormatting.WHITE;
		}
		return colorFormatting;
	}

	public static EnumChatFormatting getColorFormattingForPlayer(EntityPlayer player) {
		NBTTagCompound tagCompound = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getCompoundTag(Globals.NBT_EIRAIRC);
		ThemeSettings theme = SharedGlobalConfig.theme;
		int nameColorId = -1;
		if(SharedGlobalConfig.enablePlayerColors) {
			if (tagCompound.hasKey(Globals.NBT_NAMECOLOR)) {
				nameColorId = tagCompound.getInteger(Globals.NBT_NAMECOLOR);
			} else if (tagCompound.hasKey(Globals.NBT_NAMECOLOR_DEPRECATED)) {
				String colorName = tagCompound.getString(Globals.NBT_NAMECOLOR_DEPRECATED);
				EnumChatFormatting color = getColorFormattingLegacy(colorName);
				if (color != null) {
					nameColorId = color.ordinal();
				}
			}
		}
		if(nameColorId != -1) {
			return mcChatFormatting[nameColorId];
		} else if(Utils.isOP(player)) {
			if(theme.hasColor(ThemeColorComponent.mcOpNameColor)) {
				return theme.getColor(ThemeColorComponent.mcOpNameColor);
			}
		}
		return theme.getColor(ThemeColorComponent.mcNameColor);
	}

	public static EnumChatFormatting getColorFormattingForUser(IRCChannel channel, IRCUser user) {
		EnumChatFormatting nameColor = ((IRCUserImpl) user).getNameColor();
		if(nameColor != null && SharedGlobalConfig.twitchNameColors) {
			return nameColor;
		}
		ThemeSettings theme = ConfigHelper.getTheme(channel);
		if(channel == null) {
			return theme.getColor(ThemeColorComponent.ircPrivateNameColor);
		}
		if(user.isOperator(channel)) {
			if(theme.hasColor(ThemeColorComponent.ircOpNameColor)) {
				return theme.getColor(ThemeColorComponent.ircOpNameColor);
			}
		} else if(user.hasVoice(channel)) {
			if(theme.hasColor(ThemeColorComponent.ircVoiceNameColor)) {
				return theme.getColor(ThemeColorComponent.ircVoiceNameColor);
			}
		}
		return theme.getColor(ThemeColorComponent.ircNameColor);
	}

	public static boolean isValidColor(String colorName) {
		try {
			EnumChatFormatting formatting = EnumChatFormatting.valueOf(colorName.toUpperCase());
			return formatting.isColor();
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public static EnumChatFormatting getColorFromName(String colorName) {
		try {
			return EnumChatFormatting.valueOf(colorName.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}
