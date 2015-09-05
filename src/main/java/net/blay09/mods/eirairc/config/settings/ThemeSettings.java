// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config.settings;

import net.blay09.mods.eirairc.config.property.ConfigProperty;
import net.minecraft.util.EnumChatFormatting;

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

}
