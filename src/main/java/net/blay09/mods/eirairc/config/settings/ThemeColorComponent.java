package net.blay09.mods.eirairc.config.settings;

import net.minecraft.util.EnumChatFormatting;

/**
* Created by Blay09 on 02.10.2014.
*/
public enum ThemeColorComponent {
	mcNameColor("mcNameColor", EnumChatFormatting.WHITE, "eirairc:config.property.mcNameColor"),
	mcOpNameColor("mcOpNameColor", EnumChatFormatting.RED, "eirairc:config.property.mcOpNameColor"),
	ircNameColor("ircNameColor", EnumChatFormatting.GRAY, "eirairc:config.property.ircNameColor"),
	ircOpNameColor("ircOpNameColor", EnumChatFormatting.GOLD, "eirairc:config.property.ircOpNameColor"),
	ircVoiceNameColor("ircVoiceNameColor", EnumChatFormatting.GRAY, "eirairc:config.property.ircVoiceNameColor"),
	ircPrivateNameColor("ircPrivateNameColor", EnumChatFormatting.GRAY ,"eirairc:config.property.ircPrivateNameColor"),
	ircNoticeTextColor("ircNoticeTextColor", EnumChatFormatting.RED, "eirairc:config.property.ircNoticeTextColor"),
	emoteTextColor("emoteTextColor", EnumChatFormatting.GOLD, "eirairc:config.property.emoteTextColor");

	public static final ThemeColorComponent[] values = values();

	public final String name;
	public final EnumChatFormatting defaultValue;
	public final String langKey;

	ThemeColorComponent(String name, EnumChatFormatting defaultValue, String langKey) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.langKey = langKey;
	}

}
