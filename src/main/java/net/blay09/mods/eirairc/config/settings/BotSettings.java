// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config.settings;

import net.blay09.mods.eirairc.addon.Compatibility;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.base.MessageFormatConfig;
import net.blay09.mods.eirairc.config.property.ConfigProperty;
import net.blay09.mods.eirairc.api.config.StringList;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraftforge.common.config.Configuration;

public class BotSettings extends AbstractSettings {

	private static final String BOT = "bot";

	public final ConfigProperty<String> ident = new ConfigProperty<>(manager, BOT, "ident", "EiraIRC");
	public final ConfigProperty<String> description = new ConfigProperty<>(manager, BOT, "description", "EiraIRC Bot");
	public final ConfigProperty<String> quitMessage = new ConfigProperty<>(manager, BOT, "quitMessage", "Lycopene~");
	public final ConfigProperty<String> messageFormat = new ConfigProperty<>(manager, BOT, "messageFormat", "S-Light");
	public final ConfigProperty<String> mcNickFormat = new ConfigProperty<>(manager, BOT, "mcNickFormat", "%s");

	public final ConfigProperty<Boolean> relayDeathMessages = new ConfigProperty<>(manager, BOT, "relayDeathMessages", false);
	public final ConfigProperty<Boolean> relayMinecraftJoinLeave = new ConfigProperty<>(manager, BOT, "relayMinecraftJoinLeave", false);
	public final ConfigProperty<Boolean> relayIRCJoinLeave = new ConfigProperty<>(manager, BOT, "relayIRCJoinLeave", true);
	public final ConfigProperty<Boolean> relayNickChanges = new ConfigProperty<>(manager, BOT, "relayNickChanges", true);
	public final ConfigProperty<Boolean> filterLinks = new ConfigProperty<>(manager, BOT, "filterLinks", false);
	public final ConfigProperty<Boolean> hideNotices = new ConfigProperty<>(manager, BOT, "hideNotices", false);
	public final ConfigProperty<Boolean> convertColors = new ConfigProperty<>(manager, BOT, "convertColors", true);
	public final ConfigProperty<Boolean> allowPrivateMessages = new ConfigProperty<>(manager, BOT, "allowPrivateMessages", true);
	public final ConfigProperty<Boolean> sendAutoWho = new ConfigProperty<>(manager, BOT, "sendAutoWho", false);
	public final ConfigProperty<Boolean> relayBroadcasts = new ConfigProperty<>(manager, BOT, "relayBroadcasts", true);
	public final ConfigProperty<Boolean> interOp = new ConfigProperty<>(manager, BOT, "interOp", true);
	public final ConfigProperty<Boolean> relayAchievements = new ConfigProperty<>(manager, BOT, "relayAchievements", false);
	public final ConfigProperty<Boolean> allowCTCP = new ConfigProperty<>(manager, BOT, "allowCTCP", false);

	public final ConfigProperty<StringList> interOpAuthList = new ConfigProperty<>(manager, BOT, "interOpAuthList", new StringList());
	public final ConfigProperty<StringList> disabledNativeCommands = new ConfigProperty<>(manager, BOT, "disabledNativeCommands", new StringList());
	public final ConfigProperty<StringList> disabledInterOpCommands = new ConfigProperty<>(manager, BOT, "disabledInterOpCommands", new StringList());

	public BotSettings(BotSettings parent) {
		super(parent, BOT);
	}

	public MessageFormatConfig getMessageFormat() {
		if(Compatibility.isTabbyChat2Installed()) {
			return ConfigurationHandler.getMessageFormat("TabbyChat2");
		}
		return ConfigurationHandler.getMessageFormat(messageFormat.get());
	}

	@Override
	public void load(Configuration config, boolean ignoreDefaultValues) {
		super.load(config, ignoreDefaultValues);
		config.getCategory(BOT).get(messageFormat.getName()).setValidValues(ConfigurationHandler.getAvailableMessageFormats());
	}

	@Override
	public Configuration pullDummyConfig() {
		Configuration dummyConfig = super.pullDummyConfig();
		dummyConfig.getCategory(BOT).get(messageFormat.getName()).setValidValues(ConfigurationHandler.getAvailableMessageFormats());
		return dummyConfig;
	}

	public void loadLegacy(Configuration legacyConfig, String category) {
		if(category != null) {
			description.set(Utils.unquote(legacyConfig.get(category, "description", description.getDefaultValue()).getString()));
			ident.set(Utils.unquote(legacyConfig.get(category, "ident", ident.getDefaultValue()).getString()));
			String quitMessageOld = Utils.unquote(legacyConfig.get(category, "quitMessage", "").getString());
			if(!quitMessageOld.isEmpty()) {
				quitMessage.set(quitMessageOld);
			}
		} else {
			mcNickFormat.set(Utils.unquote(legacyConfig.get("serveronly", "nickPrefix", "").getString()) + "%s" + Utils.unquote(legacyConfig.get("serveronly", "nickSuffix", "").getString()));
			hideNotices.set(legacyConfig.get("display", "hideNotices", hideNotices.getDefaultValue()).getBoolean());
			convertColors.set(legacyConfig.get("display", "enableIRCColors", convertColors.getDefaultValue()).getBoolean());
		}
	}

}
