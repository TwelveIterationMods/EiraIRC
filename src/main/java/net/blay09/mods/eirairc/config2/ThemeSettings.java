package net.blay09.mods.eirairc.config2;

import net.minecraftforge.common.config.Configuration;

/**
 * Created by Blay09 on 29.09.2014.
 */
public class ThemeSettings {

	private static final String[] VALID_COLORS = new String[] {
			"black",
			"darkblue",
			"green",
			"cyan",
			"darkred",
			"purple",
			"gold",
			"gray",
			"darkgray",
			"blue",
			"lime",
			"lightblue",
			"red",
			"magenta",
			"yellow",
			"white"
	};


	public static String mcNameColor = "white";
	public static String mcOpNameColor = "red";
	public static String ircNameColor = "gray";
	public static String ircOpNameColor = "gold";
	public static String ircVoiceNameColor = "gray";
	public static String ircPrivateTextColor = "gray";
	public static String ircNoticeTextColor = "red";
	public static String emoteTextColor = "gold";

	public void load(Configuration config, String category) {
		mcNameColor = config.getString("mcNameColor", category, mcNameColor, "", VALID_COLORS);
		mcOpNameColor = config.getString("mcOpNameColor", category, mcOpNameColor, "", VALID_COLORS);
		ircNameColor = config.getString("ircNameColor", category, ircNameColor, "", VALID_COLORS);
		ircOpNameColor = config.getString("ircOpNameColor", category, ircOpNameColor, "", VALID_COLORS);
		ircVoiceNameColor = config.getString("ircVoiceNameColor", category, ircVoiceNameColor, "", VALID_COLORS);
		ircPrivateTextColor = config.getString("ircPrivateTextColor", category, ircPrivateTextColor, "", VALID_COLORS);
		ircNoticeTextColor = config.getString("ircNoticeTextColor", category, ircNoticeTextColor, "", VALID_COLORS);
		emoteTextColor = config.getString("emoteTextColor", category, emoteTextColor, "", VALID_COLORS);
	}

	public void save(Configuration config, String category) {
		config.get(category, "mcNameColor", "").set(mcNameColor);
		config.get(category, "mcOpNameColor", "").set(mcOpNameColor);
		config.get(category, "ircNameColor", "").set(ircNameColor);
		config.get(category, "ircOpNameColor", "").set(ircOpNameColor);
		config.get(category, "ircVoiceNameColor", "").set(ircVoiceNameColor);
		config.get(category, "ircPrivateTextColor", "").set(ircPrivateTextColor);
		config.get(category, "ircNoticeTextColor", "").set(ircNoticeTextColor);
		config.get(category, "emoteTextColor", "").set(emoteTextColor);
	}

	public void loadLegacy(Configuration legacyConfig) {
		emoteTextColor = legacyConfig.get("display", "emoteColor", emoteTextColor).getString();
		mcNameColor = legacyConfig.get("display", "defaultColor", mcNameColor).getString();
		mcOpNameColor = legacyConfig.get("display", "opColor", mcOpNameColor).getString();
		ircNameColor = legacyConfig.get("display", "ircColor", ircNameColor).getString();
		ircPrivateTextColor = legacyConfig.get("display", "ircPrivateColor", ircPrivateTextColor).getString();
		ircVoiceNameColor = legacyConfig.get("display", "ircVoiceColor", ircVoiceNameColor).getString();
		ircOpNameColor = legacyConfig.get("display", "ircOpColor", ircOpNameColor).getString();
		ircNoticeTextColor = legacyConfig.get("display", "ircNoticeColor", ircNoticeTextColor).getString();
	}
}
