// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.config;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.blay09.mods.eirairc.config.property.ConfigManager;
import net.blay09.mods.eirairc.config.property.ConfigProperty;
import net.blay09.mods.eirairc.util.I19n;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientGlobalConfig {

	public static final String GENERAL = "general";
	public static final String SCREENSHOTS = "screenshots";
	public static final String NOTIFICATIONS = "notifications";
	public static final String COMPATIBILITY = "compatibility";
	public static final String ADDONS = "addons";

	public static Configuration thisConfig;
	public static final ConfigManager manager = new ConfigManager();

	// General
	public static final ConfigProperty<Boolean> persistentConnection = new ConfigProperty<>(manager, GENERAL, "persistentConnection", true);
	public static final ConfigProperty<Boolean> showWelcomeScreen = new ConfigProperty<>(manager, GENERAL, "showWelcomeScreen", true);
	public static final ConfigProperty<Boolean> autoResetChat = new ConfigProperty<>(manager, GENERAL, "autoResetChat", false);
	public static final ConfigProperty<Boolean> terminalStyleInput = new ConfigProperty<>(manager, GENERAL, "terminalStyleInput", false);

	// Screenshots
	public static final ConfigProperty<Boolean> imageLinkPreview = new ConfigProperty<>(manager, SCREENSHOTS, "imageLinkPreview", true);
	public static final ConfigProperty<String> screenshotHoster = new ConfigProperty<>(manager, SCREENSHOTS, "uploadHoster", "imgur");
	public static final ConfigProperty<ScreenshotAction> screenshotAction = new ConfigProperty<>(manager, SCREENSHOTS, "autoAction", ScreenshotAction.None);
	public static final ConfigProperty<Integer> uploadBufferSize = new ConfigProperty<>(manager, SCREENSHOTS, "uploadBufferSize", 1024);

	// Notifications
	public static final ConfigProperty<String> notificationSound = new ConfigProperty<>(manager, NOTIFICATIONS, "notificationSound", "note.harp");
	public static final ConfigProperty<Float> notificationSoundVolume = new ConfigProperty<>(manager, NOTIFICATIONS, "notificationSoundVolume", 1f);
	public static final ConfigProperty<Float> notificationSoundPitch = new ConfigProperty<>(manager, NOTIFICATIONS, "notificationSoundPitch", 1f);
	public static final ConfigProperty<NotificationStyle> ntfyFriendJoined = new ConfigProperty<>(manager, NOTIFICATIONS, "friendJoined", NotificationStyle.TextOnly);
	public static final ConfigProperty<NotificationStyle> ntfyNameMentioned = new ConfigProperty<>(manager, NOTIFICATIONS, "nameMentioned", NotificationStyle.TextAndSound);
	public static final ConfigProperty<NotificationStyle> ntfyPrivateMessage = new ConfigProperty<>(manager, NOTIFICATIONS, "privateMessage", NotificationStyle.TextOnly);

	// Compatibility
	public static final ConfigProperty<Boolean> clientBridge = new ConfigProperty<>(manager, COMPATIBILITY, "clientBridge", false);
	public static final ConfigProperty<Boolean> disableChatToggle = new ConfigProperty<>(manager, COMPATIBILITY, "disableChatToggle", false);
	public static final ConfigProperty<Boolean> chatNoOverride = new ConfigProperty<>(manager, COMPATIBILITY, "chatNoOverride", false);
	public static final ConfigProperty<Boolean> registerShortCommands = new ConfigProperty<>(manager, COMPATIBILITY, "registerShortCommands", true);

	// Keybinds
	public static final KeyBinding keyScreenshotShare = new KeyBinding("key.irc.screenshotShare", 0, "key.categories.irc");
	public static final KeyBinding keyOpenScreenshots = new KeyBinding("key.irc.openScreenshots", 0, "key.categories.irc");
	public static final KeyBinding keyToggleTarget = new KeyBinding("key.irc.toggleTarget", Keyboard.KEY_TAB, "key.categories.irc");
	public static final KeyBinding keyOpenMenu = new KeyBinding("key.irc.openMenu", Keyboard.KEY_I, "key.categories.irc");

	public static void load(File configDir, boolean reloadFile) {
		if(thisConfig == null || reloadFile) {
			thisConfig = new Configuration(new File(configDir, "client.cfg"));
			manager.setParentConfig(thisConfig);
		}

		manager.load(thisConfig);

		save();
	}

	public static void save() {
		// Category Comments
		thisConfig.setCategoryComment(GENERAL, I19n.format("eirairc:config.category.general.tooltip"));
		thisConfig.setCategoryComment(SCREENSHOTS, I19n.format("eirairc:config.category.screenshots.tooltip"));
		thisConfig.setCategoryComment(NOTIFICATIONS, I19n.format("eirairc:config.category.notifications.tooltip"));
		thisConfig.setCategoryComment(COMPATIBILITY, I19n.format("eirairc:config.category.compatibility.tooltip"));

		manager.save(thisConfig);

		thisConfig.save();
	}

	public static void loadLegacy(File configDir, Configuration legacyConfig) {
		thisConfig = new Configuration(new File(configDir, "client.cfg"));
		manager.setParentConfig(thisConfig);

		// General
		registerShortCommands.set(legacyConfig.getBoolean("registerShortCommands", "global", registerShortCommands.get(), ""));
		persistentConnection.set(legacyConfig.get("clientonly", "persistentConnection", persistentConnection.get()).getBoolean());

		// Screenshots
		screenshotHoster.set(Utils.unquote(legacyConfig.get("clientonly", "uploadHoster", screenshotHoster.get()).getString()));
		screenshotAction.set(ScreenshotAction.values[legacyConfig.get("clientonly", "screenshotAction", 0).getInt()]);
		uploadBufferSize.set(legacyConfig.get("clientonly", "uploadBufferSize", uploadBufferSize.get()).getInt());

		// Keybinds
		keyOpenMenu.setKeyCode(legacyConfig.get("keybinds", "keyMenu", keyOpenMenu.getKeyCodeDefault()).getInt());
		keyToggleTarget.setKeyCode(legacyConfig.get("keybinds", "keyToggleTarget", keyToggleTarget.getKeyCodeDefault()).getInt());
		keyScreenshotShare.setKeyCode(legacyConfig.get("keybinds", "keyScreenshotShare", keyScreenshotShare.getKeyCodeDefault()).getInt());
		keyOpenScreenshots.setKeyCode(legacyConfig.get("keybinds", "keyOpenScreenshots", keyOpenScreenshots.getKeyCodeDefault()).getInt());

		// Notifications
		notificationSound.set(Utils.unquote(legacyConfig.get("notifications", "sound", notificationSound.get()).getString()));
		notificationSoundVolume.set((float) legacyConfig.get("notifications", "soundVolume", notificationSoundVolume.get()).getDouble());
		notificationSoundPitch.set((float) legacyConfig.get("notifications", "soundPitch", notificationSoundPitch.get()).getDouble());
		ntfyFriendJoined.set(NotificationStyle.values[legacyConfig.get("notifications", "notifyFriendJoined", 0).getInt()]);
		ntfyNameMentioned.set(NotificationStyle.values[legacyConfig.get("notifications", "notifyNameMentioned", 0).getInt()]);
		ntfyPrivateMessage.set(NotificationStyle.values[legacyConfig.get("notifications", "notifyPrivateMessage", 0).getInt()]);

		// Compatibility
		clientBridge.set(legacyConfig.get("compatibility", "clientBridge", clientBridge.get()).getBoolean());
		disableChatToggle.set(legacyConfig.get("compatibility", "disableChatToggle", disableChatToggle.get()).getBoolean());
		chatNoOverride.set(legacyConfig.get("compatibility", "chatNoOverride", chatNoOverride.get()).getBoolean());

		save();
	}

	public static String handleConfigCommand(ICommandSender sender, String key) {
		return manager.getAsString(key);
	}

	@SuppressWarnings("unchecked")
	public static boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		return manager.setFromString(key, value);
	}

	public static void addOptionsToList(List<String> list, String option, boolean autoCompleteOption) {
		if (autoCompleteOption) {
			for(ConfigProperty property : manager.getProperties()) {
				if(property.getName().startsWith(option)) {
					list.add(property.getName());
				}
			}
		} else {
			ConfigProperty property = manager.getProperty(option);
			if(property != null && property.get().getClass() == Boolean.class) {
				list.add("true");
				list.add("false");
			}
		}
	}

	public static void updateUploadHosters(String[] availableHosters) {
		thisConfig.getCategory(SCREENSHOTS).get("uploadHoster").setValidValues(availableHosters);
	}
}