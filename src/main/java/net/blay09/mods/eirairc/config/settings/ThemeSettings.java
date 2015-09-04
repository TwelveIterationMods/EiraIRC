// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config.settings;

import net.blay09.mods.eirairc.config.property.ConfigProperty;
import net.blay09.mods.eirairc.util.IRCFormatting;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

public class ThemeSettings extends AbstractSettings {

	private static final String THEME = "theme";

	public final ConfigProperty<EnumChatFormatting> mcNameColor = new ConfigProperty<>(manager, category, "mcNameColor", EnumChatFormatting.WHITE);
	public final ConfigProperty<EnumChatFormatting> mcOpNameColor = new ConfigProperty<>(manager, category, "mcOpNameColor", EnumChatFormatting.RED);
	public final ConfigProperty<EnumChatFormatting> ircNameColor = new ConfigProperty<>(manager, category, "ircNameColor", EnumChatFormatting.GRAY);
	public final ConfigProperty<EnumChatFormatting> ircOpNameColor = new ConfigProperty<>(manager, category, "ircOpNameColor", EnumChatFormatting.GOLD);
	public final ConfigProperty<EnumChatFormatting> ircVoiceNameColor = new ConfigProperty<>(manager, category, "ircVoiceNameColor", EnumChatFormatting.GRAY);
	public final ConfigProperty<EnumChatFormatting> ircPrivateNameColor = new ConfigProperty<>(manager, category, "ircPrivateNameColor", EnumChatFormatting.GRAY);
	public final ConfigProperty<EnumChatFormatting> ircNoticeTextColor = new ConfigProperty<>(manager, category, "ircNoticeTextColor", EnumChatFormatting.RED);
	public final ConfigProperty<EnumChatFormatting> emoteTextColor = new ConfigProperty<>(manager, category, "emoteTextColor", EnumChatFormatting.GOLD);

	public ThemeSettings(ThemeSettings parent) {
		super(parent, THEME);
	}

	public void loadLegacy(Configuration legacyConfig, String categoryName) {
		if(categoryName != null) {
			String emoteColor = Utils.unquote(legacyConfig.get(categoryName, "emoteColor", "").getString());
			if(!emoteColor.isEmpty()) {
				emoteTextColor.set(IRCFormatting.getColorFormattingLegacy(emoteColor));
			}
			String ircColor = Utils.unquote(legacyConfig.get(categoryName, "ircColor", "").getString());
			if(!ircColor.isEmpty()) {
				ircNameColor.set(IRCFormatting.getColorFormattingLegacy(ircColor));
			}
		} else {
			emoteTextColor.set(IRCFormatting.getColorFormattingLegacy(Utils.unquote(legacyConfig.get("display", "emoteColor", "gold").getString())));
			mcNameColor.set(IRCFormatting.getColorFormattingLegacy(Utils.unquote(legacyConfig.get("display", "defaultColor", "white").getString())));
			mcOpNameColor.set(IRCFormatting.getColorFormattingLegacy(Utils.unquote(legacyConfig.get("display", "opColor", "red").getString())));
			ircNameColor.set(IRCFormatting.getColorFormattingLegacy(Utils.unquote(legacyConfig.get("display", "ircColor", "gray").getString())));
			ircPrivateNameColor.set(IRCFormatting.getColorFormattingLegacy(Utils.unquote(legacyConfig.get("display", "ircPrivateColor", "gray").getString())));
			ircVoiceNameColor.set(IRCFormatting.getColorFormattingLegacy(Utils.unquote(legacyConfig.get("display", "ircVoiceColor", "gray").getString())));
			ircOpNameColor.set(IRCFormatting.getColorFormattingLegacy(Utils.unquote(legacyConfig.get("display", "ircOpColor", "gold").getString())));
			ircNoticeTextColor.set(IRCFormatting.getColorFormattingLegacy(Utils.unquote(legacyConfig.get("display", "ircNoticeColor", "gray").getString())));
		}
	}

}
