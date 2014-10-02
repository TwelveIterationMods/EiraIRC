package net.blay09.mods.eirairc.config2;

import net.blay09.mods.eirairc.api.upload.UploadManager;
import net.minecraftforge.common.config.Configuration;
import org.lwjgl.input.Keyboard;

import java.io.File;

/**
 * Created by Blay09 on 29.09.2014.
 */
public class ClientGlobalConfig {

	private static final String GENERAL = "general";
	private static final String SCREENSHOTS = "screenshots";
	private static final String KEYBINDS = "keybinds";
	private static final String NOTIFICATIONS = "notifications";
	private static final String COMPATIBILITY = "compatibility";

	private static Configuration thisConfig;

	// General
	public static boolean hudRecState = false;

	// Screenshots
	public static String screenshotHoster = "";
	public static int uploadBufferSize = 1024;

	// Keybinds
	public static int keyScreenshotShare = -1;
	public static int keyOpenScreenshots = -1;
	public static int keyToggleRecording = -1;
	public static int keyToggleLive = -1;
	public static int keyToggleTarget = Keyboard.KEY_TAB;
	public static int keyOpenMenu = Keyboard.KEY_I;

	// Notifications
	public static enum NotificationStyle {
		None,
		TextOnly,
		SoundOnly,
		TextAndSound;

		public static final NotificationStyle[] values = values();
		public static final int MAX = values.length - 1;
	}

	public static String notificationSound = "note.harp";
	public static float notificationSoundVolume = 1f;
	public static float notificationSoundPitch = 1f;
	public static NotificationStyle ntfyFriendJoined = NotificationStyle.TextOnly;
	public static NotificationStyle ntfyNameMentioned = NotificationStyle.TextAndSound;
	public static NotificationStyle ntfyUserRecording = NotificationStyle.TextAndSound;
	public static NotificationStyle ntfyPrivateMessage = NotificationStyle.TextOnly;

	// Compatibility
	public static boolean clientBridge = false;
	public static String clientBridgeMessageToken = "[IG]";
	public static String clientBridgeNickToken = "";
	public static boolean disableChatToggle = false;
	public static boolean vanillaChat = true;

	public static void load(File configDir) {
		thisConfig = new Configuration(new File(configDir, "eirairc/client.cfg"));

		// General
		hudRecState = thisConfig.getBoolean("hudRecState", GENERAL, hudRecState, "[Deprecated] If set to true, your screen will get cool red and green dots in some locations.");

		// Screenshots
		screenshotHoster = thisConfig.getString("uploadHoster", SCREENSHOTS, screenshotHoster, "The name of the hoster to upload screenshots to. Valid values are: imgur, DirectUpload", UploadManager.getAvailableHosters());
		uploadBufferSize = thisConfig.getInt("uploadBufferSize", SCREENSHOTS, uploadBufferSize, 256, Integer.MAX_VALUE, "[Advanced] Why would you even touch this option?");

		// Keybinds
		keyScreenshotShare = thisConfig.getInt("screenshotShare", KEYBINDS, keyScreenshotShare, -1, Integer.MAX_VALUE, "Key code to take a screenshot and share it in the current channel");
		keyOpenScreenshots = thisConfig.getInt("openScreenshots", KEYBINDS, keyOpenScreenshots, -1, Integer.MAX_VALUE, "Key code to open the screenshot archive");
		keyToggleRecording = thisConfig.getInt("toggleRecording", KEYBINDS, keyToggleRecording, -1, Integer.MAX_VALUE, "[Deprecated] Key code to fire a recording notification");
		keyToggleLive = thisConfig.getInt("toggleLive", KEYBINDS, keyToggleLive, -1, Integer.MAX_VALUE, "[Deprecated] Key code to fire a livestream notification");
		keyToggleTarget = thisConfig.getInt("toggleTarget", KEYBINDS, keyToggleTarget, -1, Integer.MAX_VALUE, "Key code to switch between the current chat channel");
		keyOpenMenu = thisConfig.getInt("openMenu", KEYBINDS, keyOpenMenu, -1, Integer.MAX_VALUE, "Key code to open the EiraIRC menu");

		// Notifications
		notificationSound = thisConfig.getString("soundName", NOTIFICATIONS, notificationSound, "Name of a sound known to Minecraft to play on notifications.");
		notificationSoundVolume = thisConfig.getFloat("soundVolume", NOTIFICATIONS, notificationSoundVolume, 0f, 1f, "Volume for the sound to play on notifications.");
		notificationSoundPitch = thisConfig.getFloat("soundPitch", NOTIFICATIONS, notificationSoundPitch, 0.5f, 2f, "Pitch for the sound to play on notifications.");
		ntfyFriendJoined = NotificationStyle.values[thisConfig.getInt("friendJoined", NOTIFICATIONS, ntfyFriendJoined.ordinal(), 0, NotificationStyle.MAX, "0: none, 1: text, 2: sound, 3: text and sound")];
		ntfyNameMentioned = NotificationStyle.values[thisConfig.getInt("nameMentioned", NOTIFICATIONS, ntfyNameMentioned.ordinal(), 0, NotificationStyle.MAX, "0: none, 1: text, 2: sound, 3: text and sound")];
		ntfyUserRecording = NotificationStyle.values[thisConfig.getInt("userRecording", NOTIFICATIONS, ntfyUserRecording.ordinal(), 0, NotificationStyle.MAX, "0: none, 1: text, 2: sound, 3: text and sound")];
		ntfyPrivateMessage = NotificationStyle.values[thisConfig.getInt("privateMessage", NOTIFICATIONS, ntfyPrivateMessage.ordinal(), 0, NotificationStyle.MAX, "0: none, 1: text, 2: sound, 3: text and sound")];

		// Compatibility
		clientBridge = thisConfig.getBoolean("clientBridge", COMPATIBILITY, clientBridge, "");
		clientBridgeMessageToken = thisConfig.getString("clientBridgeMessageToken", COMPATIBILITY, clientBridgeMessageToken, "");
		clientBridgeNickToken = thisConfig.getString("clientBridgeNickToken", COMPATIBILITY, clientBridgeNickToken, "");
		disableChatToggle = thisConfig.getBoolean("disableChatToggle", COMPATIBILITY, disableChatToggle, "");
		vanillaChat = thisConfig.getBoolean("vanillaChat", COMPATIBILITY, vanillaChat, "");
	}

	public static void save() {
		// General
		thisConfig.get(GENERAL, "hudRecState", false).set(hudRecState);

		// Screenshots
		thisConfig.get(SCREENSHOTS, "uploadHoster", "").set(screenshotHoster);

		// Keybinds
		thisConfig.get(KEYBINDS, "screenshotShare", -1).set(keyScreenshotShare);
		thisConfig.get(KEYBINDS, "openScreenshots", -1).set(keyOpenScreenshots);
		thisConfig.get(KEYBINDS, "toggleRecording", -1).set(keyToggleRecording);
		thisConfig.get(KEYBINDS, "toggleLive", -1).set(keyToggleLive);
		thisConfig.get(KEYBINDS, "toggleTarget", -1).set(keyToggleTarget);
		thisConfig.get(KEYBINDS, "openMenu", -1).set(keyOpenMenu);

		// Notifications
		thisConfig.get(NOTIFICATIONS, "soundName", "").set(notificationSound);
		thisConfig.get(NOTIFICATIONS, "soundVolume", 0f).set(notificationSoundVolume);
		thisConfig.get(NOTIFICATIONS, "soundPitch", 0f).set(notificationSoundPitch);
		thisConfig.get(NOTIFICATIONS, "friendJoined", 0).set(ntfyFriendJoined.ordinal());
		thisConfig.get(NOTIFICATIONS, "nameMentioned", 0).set(ntfyNameMentioned.ordinal());
		thisConfig.get(NOTIFICATIONS, "userRecording", 0).set(ntfyUserRecording.ordinal());
		thisConfig.get(NOTIFICATIONS, "privateMessage", 0).set(ntfyPrivateMessage.ordinal());

		// Compatibility
		thisConfig.get(COMPATIBILITY, "clientBridge", false).set(clientBridge);
		thisConfig.get(COMPATIBILITY, "clientBridgeMessageToken", "").set(clientBridgeMessageToken);
		thisConfig.get(COMPATIBILITY, "clientBridgeNickToken", "").set(clientBridgeNickToken);
		thisConfig.get(COMPATIBILITY, "disableChatToggle", false).set(disableChatToggle);
		thisConfig.get(COMPATIBILITY, "vanillaChat", false).set(vanillaChat);

		thisConfig.save();
	}

	public static void loadLegacy(File configDir, Configuration legacyConfig) {
		thisConfig = new Configuration(new File(configDir, "eirairc/client.cfg"));

		// General
		hudRecState = legacyConfig.get("display", "hudRecState", hudRecState).getBoolean();

		// Screenshots
		screenshotHoster = legacyConfig.get("clientonly", "screenshotHoster", screenshotHoster).getString();

		// Keybinds
		keyOpenMenu = legacyConfig.get("keybinds", "keyMenu", keyOpenMenu).getInt();
		keyToggleTarget = legacyConfig.get("keybinds", "keyToggleTarget", keyToggleTarget).getInt();
		keyToggleLive = legacyConfig.get("keybinds", "keyToggleLive", keyToggleLive).getInt();
		keyToggleRecording = legacyConfig.get("keybinds", "keyToggleRecording", keyToggleRecording).getInt();
		keyScreenshotShare = legacyConfig.get("keybinds", "keyScreenshotShare", keyScreenshotShare).getInt();
		keyOpenScreenshots = legacyConfig.get("keybinds", "keyOpenScreenshots", keyOpenScreenshots).getInt();

		// Notifications
		notificationSound = legacyConfig.get("notifications", "sound", notificationSound).getString();
		notificationSoundVolume = (float) legacyConfig.get("notifications", "soundVolume", notificationSoundVolume).getDouble();
		notificationSoundPitch = (float) legacyConfig.get("notifications", "soundPitch", notificationSoundPitch).getDouble();
		ntfyFriendJoined = NotificationStyle.values[legacyConfig.get("notifications", "notifyFriendJoined", ntfyFriendJoined.ordinal()).getInt()];
		ntfyNameMentioned = NotificationStyle.values[legacyConfig.get("notifications", "notifyNameMentioned", ntfyNameMentioned.ordinal()).getInt()];
		ntfyPrivateMessage = NotificationStyle.values[legacyConfig.get("notifications", "notifyPrivateMessage", ntfyPrivateMessage.ordinal()).getInt()];
		ntfyUserRecording = NotificationStyle.values[legacyConfig.get("notifications", "notifyUserRecording", ntfyUserRecording.ordinal()).getInt()];

		// Compatibility
		clientBridge = legacyConfig.get("compatibility", "clientBridge", clientBridge).getBoolean();
		clientBridgeMessageToken = legacyConfig.get("compatibility", "clientBridgeMessageToken", clientBridgeMessageToken).getString();
		clientBridgeNickToken = legacyConfig.get("compatibility", "clientBridgeNickToken", clientBridgeNickToken).getString();
		disableChatToggle = legacyConfig.get("compatibility", "disableChatToggle", disableChatToggle).getBoolean();
		vanillaChat = legacyConfig.get("compatibility", "vanillaChat", vanillaChat).getBoolean();

		save();
	}

}