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

	public static class RGB {
		private float r;
		private float g;
		private float b;

		public RGB(float r, float g, float b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}

	private static final Pattern ircColorPattern = Pattern.compile("\u0003([0-9][0-9]?)(?:[,][0-9][0-9]?)?");
	private static final Pattern mcColorPattern = Pattern.compile("\u00a7([0-9a-f])");
	private static final IRCFormatting[] values = values();
	private static final EnumChatFormatting[] mcChatFormatting = EnumChatFormatting.values();
	private static final RGB[] mcColorValues = new RGB[16];
	private static final Map<String, EnumChatFormatting> twitchColorCache = new HashMap<String, EnumChatFormatting>();
	static {
		mcColorValues[EnumChatFormatting.BLACK.ordinal()] = new RGB(0f, 0f, 0f);
		mcColorValues[EnumChatFormatting.DARK_BLUE.ordinal()] = new RGB(0f, 0f, 0.66f);
		mcColorValues[EnumChatFormatting.DARK_GREEN.ordinal()] = new RGB(0f, 0.66f, 0f);
		mcColorValues[EnumChatFormatting.DARK_AQUA.ordinal()] = new RGB(0f, 0.66f, 0.66f);
		mcColorValues[EnumChatFormatting.DARK_RED.ordinal()] = new RGB(0.66f, 0f, 0f);
		mcColorValues[EnumChatFormatting.DARK_PURPLE.ordinal()] = new RGB(0.66f, 0f, 0.66f);
		mcColorValues[EnumChatFormatting.GOLD.ordinal()] = new RGB(1f, 0.66f, 0f);
		mcColorValues[EnumChatFormatting.GRAY.ordinal()] = new RGB(0.66f, 0.66f, 0.66f);
		mcColorValues[EnumChatFormatting.DARK_GRAY.ordinal()] = new RGB(0.33f, 0.33f, 0.33f);
		mcColorValues[EnumChatFormatting.BLUE.ordinal()] = new RGB(0.33f, 0.33f, 1f);
		mcColorValues[EnumChatFormatting.GREEN.ordinal()] = new RGB(0.33f, 1f, 0.33f);
		mcColorValues[EnumChatFormatting.AQUA.ordinal()] = new RGB(0.33f, 1f, 1f);
		mcColorValues[EnumChatFormatting.RED.ordinal()] = new RGB(1f, 0.33f, 0.33f);
		mcColorValues[EnumChatFormatting.LIGHT_PURPLE.ordinal()] = new RGB(1f, 0.33f, 1f);
		mcColorValues[EnumChatFormatting.YELLOW.ordinal()] = new RGB(1f, 1f, 0.33f);
		mcColorValues[EnumChatFormatting.WHITE.ordinal()] = new RGB(1f, 1f, 1f);

		twitchColorCache.put("#008000", EnumChatFormatting.DARK_GREEN);
		twitchColorCache.put("#0000FF", EnumChatFormatting.BLUE);
		twitchColorCache.put("#1E90FF", EnumChatFormatting.BLUE);
		twitchColorCache.put("#FF0000", EnumChatFormatting.RED);
		twitchColorCache.put("#B22222", EnumChatFormatting.DARK_RED);
		twitchColorCache.put("#FF7F50", EnumChatFormatting.GOLD);
		twitchColorCache.put("#9ACD32", EnumChatFormatting.GREEN);
		twitchColorCache.put("#FF4500", EnumChatFormatting.GOLD);
		twitchColorCache.put("#2E8B57", EnumChatFormatting.DARK_AQUA);
		twitchColorCache.put("#DAA520", EnumChatFormatting.YELLOW);
		twitchColorCache.put("#D2691E", EnumChatFormatting.GOLD);
		twitchColorCache.put("#5F9EA0", EnumChatFormatting.AQUA);
		twitchColorCache.put("#FF69B4", EnumChatFormatting.LIGHT_PURPLE);
		twitchColorCache.put("#8A2BE2", EnumChatFormatting.LIGHT_PURPLE);
		twitchColorCache.put("#00FF7F", EnumChatFormatting.GREEN);
	}

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
				EnumChatFormatting colorFormat = IRCFormatting.getColorFromIRCColorCode(colorCode);
				if(colorFormat != null) {
					result = result.replaceFirst(Matcher.quoteReplacement(matcher.group()), MC_FORMATTING_PREFIX + colorFormat.getFormattingCode());
				} else {
					result = result.replaceFirst(Matcher.quoteReplacement(matcher.group()), "");
				}
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

	public static EnumChatFormatting getColorFromTwitch(String twitchColor) {
		EnumChatFormatting color = twitchColorCache.get(twitchColor);
		if(color == null) {
			RGB twitchRGB = hexToRGB(twitchColor);
			float minDist = Float.MAX_VALUE;
			EnumChatFormatting minColor = null;
			for(int i = 0; i < mcColorValues.length; i++) {
				float dist = (twitchRGB.r - mcColorValues[i].r) * (twitchRGB.r - mcColorValues[i].r) + (twitchRGB.g - mcColorValues[i].g) * (twitchRGB.g - mcColorValues[i].g) + (twitchRGB.b - mcColorValues[i].b) * (twitchRGB.b - mcColorValues[i].b);
				if(dist < minDist) {
					minDist = dist;
					minColor = mcChatFormatting[i];
				}
			}
			if(minColor != null) {
				twitchColorCache.put(twitchColor, minColor);
				color = minColor;
			}
		}
		return color != null ? color : EnumChatFormatting.WHITE;
	}

	private static RGB hexToRGB(String hexColor) {
		return new RGB(
				(float) Integer.valueOf(hexColor.substring(1, 3), 16) / 255f,
				(float) Integer.valueOf(hexColor.substring(3, 5), 16) / 255f,
				(float) Integer.valueOf(hexColor.substring(5, 7), 16) / 255f);
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
