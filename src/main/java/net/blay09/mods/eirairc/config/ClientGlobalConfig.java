package net.blay09.mods.eirairc.config;

import net.blay09.mods.eirairc.client.UploadManager;
import net.blay09.mods.eirairc.util.I19n;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.List;


@SideOnly(Side.CLIENT)
public class ClientGlobalConfig {

	public static final String GENERAL = "general";
	public static final String SCREENSHOTS = "screenshots";
	public static final String NOTIFICATIONS = "notifications";
	public static final String COMPATIBILITY = "compatibility";

	public static Configuration thisConfig;

	// General
	public static boolean persistentConnection = true;
	public static boolean showWelcomeScreen = true;
	public static boolean autoResetChat = false;

	// Screenshots
	public static boolean imageLinkPreview = true;
	public static String screenshotHoster = "";
	public static ScreenshotAction screenshotAction = ScreenshotAction.None;
	public static int uploadBufferSize = 1024;

	// Keybinds
	public static final KeyBinding keyScreenshotShare = new KeyBinding("key.irc.screenshotShare", 0, "key.categories.irc");
	public static final KeyBinding keyOpenScreenshots = new KeyBinding("key.irc.openScreenshots", 0, "key.categories.irc");
	public static final KeyBinding keyToggleTarget = new KeyBinding("key.irc.toggleTarget", Keyboard.KEY_TAB, "key.categories.irc");
	public static final KeyBinding keyOpenMenu = new KeyBinding("key.irc.openMenu", Keyboard.KEY_I, "key.categories.irc");

	// Notifications
	public static String notificationSound = "note.harp";
	public static float notificationSoundVolume = 1f;
	public static float notificationSoundPitch = 1f;
	public static NotificationStyle ntfyFriendJoined = NotificationStyle.TextOnly;
	public static NotificationStyle ntfyNameMentioned = NotificationStyle.TextAndSound;
	public static NotificationStyle ntfyPrivateMessage = NotificationStyle.TextOnly;

	// Compatibility
	public static boolean clientBridge = false;
	public static String clientBridgeMessageToken = "[IG]";
	public static String clientBridgeNickToken = "";
	public static boolean disableChatToggle = false;
	public static boolean chatNoOverride = false;
	public static boolean registerShortCommands = true;

	public static void load(File configDir, boolean reloadFile) {
		if(thisConfig == null || reloadFile) {
			thisConfig = new Configuration(new File(configDir, "client.cfg"));
		}

		// General
		registerShortCommands = thisConfig.getBoolean("registerShortCommands", GENERAL, registerShortCommands, I19n.format("eirairc:config.property.registerShortCommands.tooltip"), "eirairc:config.property.registerShortCommands");
		persistentConnection = thisConfig.getBoolean("persistentConnection", GENERAL, persistentConnection, I19n.format("eirairc:config.property.persistentConnection"), "eirairc:config.property.persistentConnection");
		showWelcomeScreen = thisConfig.getBoolean("showWelcomeScreen", GENERAL, showWelcomeScreen, I19n.format("eirairc:config.property.showWelcomeScreen"), "eirairc:config.property.showWelcomeScreen");
		autoResetChat = thisConfig.getBoolean("autoResetChat", GENERAL, autoResetChat, I19n.format("eirairc:config.property.autoResetChat.tooltip"), "eirairc:config.property.autoResetChat");

		// Screenshots
		imageLinkPreview = thisConfig.getBoolean("imageLinkPreview", SCREENSHOTS, imageLinkPreview, I19n.format("eirairc:config.property.imageLinkPreview"), "eirairc:config.property.imageLinkPreview");
		screenshotHoster = thisConfig.getString("uploadHoster", SCREENSHOTS, screenshotHoster, I19n.format("eirairc:config.property.uploadHoster"), "eirairc:config.property.uploadHoster");
		screenshotAction = ScreenshotAction.valueOf(thisConfig.getString("autoAction", SCREENSHOTS, screenshotAction.name(), I19n.format("eirairc:config.property.autoAction"), ScreenshotAction.NAMES, "eirairc:config.property.autoAction"));
		uploadBufferSize = thisConfig.getInt("uploadBufferSize", SCREENSHOTS, uploadBufferSize, 256, 4096, I19n.format("eirairc:config.property.uploadBufferSize"), "eirairc:config.property.uploadBufferSize");

		// Notifications
		notificationSound = thisConfig.getString("soundName", NOTIFICATIONS, notificationSound, I19n.format("eirairc:config.property.soundName"), "eirairc:config.property.soundName");
		notificationSoundVolume = thisConfig.getFloat("soundVolume", NOTIFICATIONS, notificationSoundVolume, 0f, 1f, I19n.format("eirairc:config.property.soundVolume"), "eirairc:config.property.soundVolume");
		notificationSoundPitch = thisConfig.getFloat("soundPitch", NOTIFICATIONS, notificationSoundPitch, 0.5f, 2f, I19n.format("eirairc:config.property.soundPitch"), "eirairc:config.property.soundPitch");
		ntfyFriendJoined = NotificationStyle.valueOf(thisConfig.getString("friendJoined", NOTIFICATIONS, ntfyFriendJoined.name(), I19n.format("eirairc:config.property.friendJoined"), NotificationStyle.NAMES, "eirairc:config.property.friendJoined"));
		ntfyNameMentioned = NotificationStyle.valueOf(thisConfig.getString("nameMentioned", NOTIFICATIONS, ntfyNameMentioned.name(), I19n.format("eirairc:config.property.nameMentioned"), NotificationStyle.NAMES, "eirairc:config.property.nameMentioned"));
		ntfyPrivateMessage = NotificationStyle.valueOf(thisConfig.getString("privateMessage", NOTIFICATIONS, ntfyPrivateMessage.name(), I19n.format("eirairc:config.property.privateMessage"), NotificationStyle.NAMES, "eirairc:config.property.privateMessage"));

		// Compatibility
		clientBridge = thisConfig.getBoolean("clientBridge", COMPATIBILITY, clientBridge, I19n.format("eirairc:config.property.clientBridge"), "eirairc:config.property.clientBridge");
		clientBridgeMessageToken = thisConfig.getString("clientBridgeMessageToken", COMPATIBILITY, clientBridgeMessageToken, I19n.format("eirairc:config.property.clientBridgeMessageToken"), "eirairc:config.property.clientBridgeMessageToken");
		clientBridgeNickToken = thisConfig.getString("clientBridgeNickToken", COMPATIBILITY, clientBridgeNickToken, I19n.format("eirairc:config.property.clientBridgeNickToken"), "eirairc:config.property.clientBridgeNickToken");
		disableChatToggle = thisConfig.getBoolean("disableChatToggle", COMPATIBILITY, disableChatToggle, I19n.format("eirairc:config.property.disableChatToggle"), "eirairc:config.property.disableChatToggle");
		chatNoOverride = thisConfig.getBoolean("chatNoOverride", COMPATIBILITY, chatNoOverride, I19n.format("eirairc:config.property.chatNoOverride"), "eirairc:config.property.chatNoOverride");

		save();
	}

	public static void save() {
		// Category Comments
		thisConfig.setCategoryComment(GENERAL, I19n.format("eirairc:config.category.general.tooltip"));
		thisConfig.setCategoryComment(SCREENSHOTS, I19n.format("eirairc:config.category.screenshots.tooltip"));
		thisConfig.setCategoryComment(NOTIFICATIONS, I19n.format("eirairc:config.category.notifications.tooltip"));
		thisConfig.setCategoryComment(COMPATIBILITY, I19n.format("eirairc:config.category.compatibility.tooltip"));

		// General
		thisConfig.get(GENERAL, "registerShortCommands", false, I19n.format("eirairc:config.property.registerShortCommands.tooltip")).set(registerShortCommands);
		thisConfig.get(GENERAL, "persistentConnection", false, I19n.format("eirairc:config.property.persistentConnection")).set(persistentConnection);
		thisConfig.get(GENERAL, "showWelcomeScreen", false, I19n.format("eirairc:config.property.showWelcomeScreen")).set(showWelcomeScreen);
		thisConfig.get(GENERAL, "autoResetChat", "", I19n.format("eirairc:config.property.autoResetChat")).set(autoResetChat);

		// Screenshots
		thisConfig.get(SCREENSHOTS, "imageLinkPreview", false, I19n.format("eirairc:config.property.imageLinkPreview.tooltip")).set(imageLinkPreview);
		thisConfig.get(SCREENSHOTS, "uploadHoster", "", I19n.format("eirairc:config.property.uploadHoster")).set(screenshotHoster);
		thisConfig.get(SCREENSHOTS, "autoAction", "", I19n.format("eirairc:config.property.autoAction")).set(screenshotAction.name());
		thisConfig.get(SCREENSHOTS, "uploadBufferSize", 0, I19n.format("eirairc:config.property.uploadBufferSize")).set(uploadBufferSize);

		// Notifications
		thisConfig.get(NOTIFICATIONS, "soundName", "", I19n.format("eirairc:config.property.soundName")).set(notificationSound);
		thisConfig.get(NOTIFICATIONS, "soundVolume", 0f, I19n.format("eirairc:config.property.soundVolume")).set(notificationSoundVolume);
		thisConfig.get(NOTIFICATIONS, "soundPitch", 0f, I19n.format("eirairc:config.property.soundPitch")).set(notificationSoundPitch);
		thisConfig.get(NOTIFICATIONS, "friendJoined", "", I19n.format("eirairc:config.property.friendJoined")).set(ntfyFriendJoined.name());
		thisConfig.get(NOTIFICATIONS, "nameMentioned", "", I19n.format("eirairc:config.property.nameMentioned")).set(ntfyNameMentioned.name());
		thisConfig.get(NOTIFICATIONS, "privateMessage", "", I19n.format("eirairc:config.property.privateMessage")).set(ntfyPrivateMessage.name());

		// Compatibility
		thisConfig.get(COMPATIBILITY, "clientBridge", false, I19n.format("eirairc:config.property.clientBridge")).set(clientBridge);
		thisConfig.get(COMPATIBILITY, "clientBridgeMessageToken", I19n.format("eirairc:config.property.clientBridgeMessageToken")).set(clientBridgeMessageToken);
		thisConfig.get(COMPATIBILITY, "clientBridgeNickToken", I19n.format("eirairc:config.property.clientBridgeNickToken")).set(clientBridgeNickToken);
		thisConfig.get(COMPATIBILITY, "disableChatToggle", false, I19n.format("eirairc:config.property.disableChatToggle")).set(disableChatToggle);
		thisConfig.get(COMPATIBILITY, "chatNoOverride", false, I19n.format("eirairc:config.property.chatNoOverride")).set(chatNoOverride);

		thisConfig.save();
	}

	public static void loadLegacy(File configDir, Configuration legacyConfig) {
		thisConfig = new Configuration(new File(configDir, "client.cfg"));

		// General
		registerShortCommands = legacyConfig.getBoolean("registerShortCommands", "global", registerShortCommands, "");
		persistentConnection = legacyConfig.get("clientonly", "persistentConnection", persistentConnection).getBoolean();

		// Screenshots
		screenshotHoster = Utils.unquote(legacyConfig.get("clientonly", "uploadHoster", screenshotHoster).getString());
		screenshotAction = ScreenshotAction.values[legacyConfig.get("clientonly", "screenshotAction", screenshotAction.ordinal()).getInt()];
		uploadBufferSize = legacyConfig.get("clientonly", "uploadBufferSize", uploadBufferSize).getInt();

		// Keybinds
		keyOpenMenu.setKeyCode(legacyConfig.get("keybinds", "keyMenu", keyOpenMenu.getKeyCodeDefault()).getInt());
		keyToggleTarget.setKeyCode(legacyConfig.get("keybinds", "keyToggleTarget", keyToggleTarget.getKeyCodeDefault()).getInt());
		keyScreenshotShare.setKeyCode(legacyConfig.get("keybinds", "keyScreenshotShare", keyScreenshotShare.getKeyCodeDefault()).getInt());
		keyOpenScreenshots.setKeyCode(legacyConfig.get("keybinds", "keyOpenScreenshots", keyOpenScreenshots.getKeyCodeDefault()).getInt());

		// Notifications
		notificationSound = Utils.unquote(legacyConfig.get("notifications", "sound", notificationSound).getString());
		notificationSoundVolume = (float) legacyConfig.get("notifications", "soundVolume", notificationSoundVolume).getDouble();
		notificationSoundPitch = (float) legacyConfig.get("notifications", "soundPitch", notificationSoundPitch).getDouble();
		ntfyFriendJoined = NotificationStyle.values[legacyConfig.get("notifications", "notifyFriendJoined", ntfyFriendJoined.ordinal()).getInt()];
		ntfyNameMentioned = NotificationStyle.values[legacyConfig.get("notifications", "notifyNameMentioned", ntfyNameMentioned.ordinal()).getInt()];
		ntfyPrivateMessage = NotificationStyle.values[legacyConfig.get("notifications", "notifyPrivateMessage", ntfyPrivateMessage.ordinal()).getInt()];

		// Compatibility
		clientBridge = legacyConfig.get("compatibility", "clientBridge", clientBridge).getBoolean();
		clientBridgeMessageToken = Utils.unquote(legacyConfig.get("compatibility", "clientBridgeMessageToken", clientBridgeMessageToken).getString());
		clientBridgeNickToken = Utils.unquote(legacyConfig.get("compatibility", "clientBridgeNickToken", clientBridgeNickToken).getString());
		disableChatToggle = legacyConfig.get("compatibility", "disableChatToggle", disableChatToggle).getBoolean();
		chatNoOverride = legacyConfig.get("compatibility", "chatNoOverride", chatNoOverride).getBoolean();

		save();
	}

	public static String handleConfigCommand(ICommandSender sender, String key) {
		if(key.equals("persistentConnection")) {
			return String.valueOf(persistentConnection);
		} else if(key.equals("registerShortCommands")) {
			return String.valueOf(registerShortCommands);
		} else if(key.equals("showWelcomeScreen")) {
			return String.valueOf(showWelcomeScreen);
		} else if(key.equals("imageLinkPreview")) {
			return String.valueOf(imageLinkPreview);
		} else if(key.equals("uploadHoster")) {
			return screenshotHoster;
		} else if(key.equals("clientBridge")) {
			return String.valueOf(clientBridge);
		} else if(key.equals("clientBridgeMessageToken")) {
			return clientBridgeMessageToken;
		} else if(key.equals("clientBridgeNickToken")) {
			return clientBridgeNickToken;
		} else if(key.equals("disableChatToggle")) {
			return String.valueOf(disableChatToggle);
		} else if(key.equals("chatNoOverride")) {
			return String.valueOf(chatNoOverride);
		} else if(key.equals("autoResetChat")) {
			return String.valueOf(autoResetChat);
		}
		return null;
	}

	public static boolean handleConfigCommand(ICommandSender sender, String key, String value) {
		boolean result = true;
		if(key.equals("persistentConnection")) {
			persistentConnection = Boolean.parseBoolean(value);
		} else if(key.equals("registerShortCommands")) {
			registerShortCommands = Boolean.parseBoolean(value);
		} else if(key.equals("showWelcomeScreen")) {
			showWelcomeScreen = Boolean.parseBoolean(value);
		} else if(key.equals("uploadHoster")) {
			if (UploadManager.isValidHoster(value)) {
				screenshotHoster = value;
			}
		} else if(key.equals("imageLinkPreview")) {
			imageLinkPreview = Boolean.parseBoolean(value);
		} else if(key.equals("clientBridge")) {
			clientBridge = Boolean.parseBoolean(value);
		} else if(key.equals("clientBridgeMessageToken")) {
			clientBridgeMessageToken = value;
		} else if(key.equals("clientBridgeNickToken")) {
			clientBridgeNickToken = value;
		} else if(key.equals("disableChatToggle")) {
			disableChatToggle = Boolean.parseBoolean(value);
		} else if(key.equals("chatNoOverride")) {
			chatNoOverride = Boolean.parseBoolean(value);
		} else if(key.equals("autoResetChat")) {
			autoResetChat = Boolean.parseBoolean(value);
		} else {
			result = false;
		}
		return result;
	}

	public static void addOptionsToList(List<String> list, String option) {
		if(option == null) {
			list.add("registerShortCommands");
			list.add("imageLinkPreview");
			list.add("persistentConnection");
			list.add("showWelcomeScreen");
			list.add("uploadHoster");
			list.add("clientBridge");
			list.add("clientBridgeMessageToken");
			list.add("clientBridgeNickToken");
			list.add("disableChatToggle");
			list.add("chatNoOverride");
			list.add("autoResetChat");
		} else if(option.equals("persistentConnection") || option.equals("clientBridge") || option.equals("disableChatToggle") || option.equals("showWelcomeScreen") || option.equals("chatNoOverride") || option.equals("autoResetChat")) {
			Utils.addBooleansToList(list);
		}
	}

	public static void updateUploadHosters(String[] availableHosters) {
		thisConfig.getCategory(SCREENSHOTS).get("uploadHoster").setValidValues(availableHosters);
	}
}