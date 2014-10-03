package net.blay09.mods.eirairc.config;

import net.blay09.mods.eirairc.api.upload.UploadManager;
import net.blay09.mods.eirairc.util.Utils;
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
	public static boolean persistentConnection = true;

	// Screenshots
	public static String screenshotHoster = "";
	public static ScreenshotAction screenshotAction = ScreenshotAction.None;
	public static int uploadBufferSize = 1024;

	// Keybinds
	public static int keyScreenshotShare = -1;
	public static int keyOpenScreenshots = -1;
	public static int keyToggleRecording = -1;
	public static int keyToggleLive = -1;
	public static int keyToggleTarget = Keyboard.KEY_TAB;
	public static int keyOpenMenu = Keyboard.KEY_I;

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
		thisConfig = new Configuration(new File(configDir, "client.cfg"));

		// General
		hudRecState = thisConfig.getBoolean("hudRecState", GENERAL, hudRecState, "");
		persistentConnection = thisConfig.getBoolean("persistentConnection", GENERAL, persistentConnection, "");

		// Screenshots
		screenshotHoster = thisConfig.getString("uploadHoster", SCREENSHOTS, screenshotHoster, "", UploadManager.getAvailableHosters());
		screenshotAction = ScreenshotAction.values[thisConfig.getInt("autoAction", SCREENSHOTS, screenshotAction.ordinal(), 0, ScreenshotAction.MAX, "")];
		uploadBufferSize = thisConfig.getInt("uploadBufferSize", SCREENSHOTS, uploadBufferSize, 256, Integer.MAX_VALUE, "");

		// Keybinds
		keyScreenshotShare = thisConfig.getInt("screenshotShare", KEYBINDS, keyScreenshotShare, -1, Integer.MAX_VALUE, "");
		keyOpenScreenshots = thisConfig.getInt("openScreenshots", KEYBINDS, keyOpenScreenshots, -1, Integer.MAX_VALUE, "");
		keyToggleRecording = thisConfig.getInt("toggleRecording", KEYBINDS, keyToggleRecording, -1, Integer.MAX_VALUE, "");
		keyToggleLive = thisConfig.getInt("toggleLive", KEYBINDS, keyToggleLive, -1, Integer.MAX_VALUE, "");
		keyToggleTarget = thisConfig.getInt("toggleTarget", KEYBINDS, keyToggleTarget, -1, Integer.MAX_VALUE, "");
		keyOpenMenu = thisConfig.getInt("openMenu", KEYBINDS, keyOpenMenu, -1, Integer.MAX_VALUE, "");

		// Notifications
		notificationSound = thisConfig.getString("soundName", NOTIFICATIONS, notificationSound, "");
		notificationSoundVolume = thisConfig.getFloat("soundVolume", NOTIFICATIONS, notificationSoundVolume, 0f, 1f, "");
		notificationSoundPitch = thisConfig.getFloat("soundPitch", NOTIFICATIONS, notificationSoundPitch, 0.5f, 2f, "");
		ntfyFriendJoined = NotificationStyle.values[thisConfig.getInt("friendJoined", NOTIFICATIONS, ntfyFriendJoined.ordinal(), 0, NotificationStyle.MAX, "")];
		ntfyNameMentioned = NotificationStyle.values[thisConfig.getInt("nameMentioned", NOTIFICATIONS, ntfyNameMentioned.ordinal(), 0, NotificationStyle.MAX, "")];
		ntfyUserRecording = NotificationStyle.values[thisConfig.getInt("userRecording", NOTIFICATIONS, ntfyUserRecording.ordinal(), 0, NotificationStyle.MAX, "")];
		ntfyPrivateMessage = NotificationStyle.values[thisConfig.getInt("privateMessage", NOTIFICATIONS, ntfyPrivateMessage.ordinal(), 0, NotificationStyle.MAX, "")];

		// Compatibility
		clientBridge = thisConfig.getBoolean("clientBridge", COMPATIBILITY, clientBridge, "");
		clientBridgeMessageToken = thisConfig.getString("clientBridgeMessageToken", COMPATIBILITY, clientBridgeMessageToken, "");
		clientBridgeNickToken = thisConfig.getString("clientBridgeNickToken", COMPATIBILITY, clientBridgeNickToken, "");
		disableChatToggle = thisConfig.getBoolean("disableChatToggle", COMPATIBILITY, disableChatToggle, "");
		vanillaChat = thisConfig.getBoolean("vanillaChat", COMPATIBILITY, vanillaChat, "");
	}

	public static void save() {
		// General
		thisConfig.get(GENERAL, "hudRecState", false, "[Deprecated] If set to true, your screen will get cool red and green dots in some locations.").set(hudRecState);
		thisConfig.get(GENERAL, "persistentConnection", false, "If set to true, the IRC connection will remain open after leaving a world or server (until Minecraft closes).").set(persistentConnection);

		// Screenshots
		thisConfig.get(SCREENSHOTS, "uploadHoster", "", "The name of the hoster to upload screenshots to. Valid values are: imgur, DirectUpload").set(screenshotHoster);
		thisConfig.get(SCREENSHOTS, "autoAction", 0, "[Deprecated] The action to perform after a screenshot was taken.").set(screenshotAction.ordinal());
		thisConfig.get(SCREENSHOTS, "uploadBufferSize", 0, "[Advanced] Why would you even touch this option?").set(uploadBufferSize);

		// Keybinds
		thisConfig.get(KEYBINDS, "screenshotShare", -1, "Key code to take a screenshot and share it in the current channel.").set(keyScreenshotShare);
		thisConfig.get(KEYBINDS, "openScreenshots", -1, "Key code to open the screenshot archive.").set(keyOpenScreenshots);
		thisConfig.get(KEYBINDS, "toggleRecording", -1, "[Deprecated] Key code to fire a recording notification.").set(keyToggleRecording);
		thisConfig.get(KEYBINDS, "toggleLive", -1, "[Deprecated] Key code to fire a livestream notification.").set(keyToggleLive);
		thisConfig.get(KEYBINDS, "toggleTarget", -1, "Key code to switch between the current chat channel.").set(keyToggleTarget);
		thisConfig.get(KEYBINDS, "openMenu", -1, "Key code to open the EiraIRC menu.").set(keyOpenMenu);

		// Notifications
		thisConfig.get(NOTIFICATIONS, "soundName", "", "Name of a sound known to Minecraft to play on notifications.").set(notificationSound);
		thisConfig.get(NOTIFICATIONS, "soundVolume", 0f, "Volume for the sound to play on notifications.").set(notificationSoundVolume);
		thisConfig.get(NOTIFICATIONS, "soundPitch", 0f, "[Deprecated] Pitch for the sound to play on notifications. Currently unused.").set(notificationSoundPitch);
		thisConfig.get(NOTIFICATIONS, "friendJoined", 0, "0: none, 1: text, 2: sound, 3: text and sound").set(ntfyFriendJoined.ordinal());
		thisConfig.get(NOTIFICATIONS, "nameMentioned", 0, "0: none, 1: text, 2: sound, 3: text and sound").set(ntfyNameMentioned.ordinal());
		thisConfig.get(NOTIFICATIONS, "userRecording", 0, "0: none, 1: text, 2: sound, 3: text and sound").set(ntfyUserRecording.ordinal());
		thisConfig.get(NOTIFICATIONS, "privateMessage", 0, "0: none, 1: text, 2: sound, 3: text and sound").set(ntfyPrivateMessage.ordinal());

		// Compatibility
		thisConfig.get(COMPATIBILITY, "clientBridge", false, "If set to true, EiraIRC will act like on a server and send chat messages to both Minecraft and IRC.").set(clientBridge);
		thisConfig.get(COMPATIBILITY, "clientBridgeMessageToken", "A token to add at the end of messages when using the clientBridge. Necessary to avoid double messages when two players on the same server use the clientBridge.").set(clientBridgeMessageToken);
		thisConfig.get(COMPATIBILITY, "clientBridgeNickToken", "See clientBridgeMessageToken. Is used in favor over the message token. Messages from nicks with this token will be omitted.").set(clientBridgeNickToken);
		thisConfig.get(COMPATIBILITY, "disableChatToggle", false, "Disables the chat toggle at the top left. Originally meant for TabbyChat, so you could set up Tabs with the '/irc msg' command instead.").set(disableChatToggle);
		thisConfig.get(COMPATIBILITY, "vanillaChat", false, "Nothing, go away. No effect.").set(vanillaChat);

		thisConfig.save();
	}

	public static void loadLegacy(File configDir, Configuration legacyConfig) {
		thisConfig = new Configuration(new File(configDir, "client.cfg"));

		// General
		hudRecState = legacyConfig.get("display", "hudRecState", hudRecState).getBoolean();
		persistentConnection = legacyConfig.get("clientonly", "persistentConnection", persistentConnection).getBoolean();

		// Screenshots
		screenshotHoster = Utils.unquote(legacyConfig.get("clientonly", "uploadHoster", screenshotHoster).getString());
		screenshotAction = ScreenshotAction.values[legacyConfig.get("clientonly", "screenshotAction", screenshotAction.ordinal()).getInt()];
		uploadBufferSize = legacyConfig.get("clientonly", "uploadBufferSize", uploadBufferSize).getInt();

		// Keybinds
		keyOpenMenu = legacyConfig.get("keybinds", "keyMenu", keyOpenMenu).getInt();
		keyToggleTarget = legacyConfig.get("keybinds", "keyToggleTarget", keyToggleTarget).getInt();
		keyToggleLive = legacyConfig.get("keybinds", "keyToggleLive", keyToggleLive).getInt();
		keyToggleRecording = legacyConfig.get("keybinds", "keyToggleRecording", keyToggleRecording).getInt();
		keyScreenshotShare = legacyConfig.get("keybinds", "keyScreenshotShare", keyScreenshotShare).getInt();
		keyOpenScreenshots = legacyConfig.get("keybinds", "keyOpenScreenshots", keyOpenScreenshots).getInt();

		// Notifications
		notificationSound = Utils.unquote(legacyConfig.get("notifications", "sound", notificationSound).getString());
		notificationSoundVolume = (float) legacyConfig.get("notifications", "soundVolume", notificationSoundVolume).getDouble();
		notificationSoundPitch = (float) legacyConfig.get("notifications", "soundPitch", notificationSoundPitch).getDouble();
		ntfyFriendJoined = NotificationStyle.values[legacyConfig.get("notifications", "notifyFriendJoined", ntfyFriendJoined.ordinal()).getInt()];
		ntfyNameMentioned = NotificationStyle.values[legacyConfig.get("notifications", "notifyNameMentioned", ntfyNameMentioned.ordinal()).getInt()];
		ntfyPrivateMessage = NotificationStyle.values[legacyConfig.get("notifications", "notifyPrivateMessage", ntfyPrivateMessage.ordinal()).getInt()];
		ntfyUserRecording = NotificationStyle.values[legacyConfig.get("notifications", "notifyUserRecording", ntfyUserRecording.ordinal()).getInt()];

		// Compatibility
		clientBridge = legacyConfig.get("compatibility", "clientBridge", clientBridge).getBoolean();
		clientBridgeMessageToken = Utils.unquote(legacyConfig.get("compatibility", "clientBridgeMessageToken", clientBridgeMessageToken).getString());
		clientBridgeNickToken = Utils.unquote(legacyConfig.get("compatibility", "clientBridgeNickToken", clientBridgeNickToken).getString());
		disableChatToggle = legacyConfig.get("compatibility", "disableChatToggle", disableChatToggle).getBoolean();
		vanillaChat = legacyConfig.get("compatibility", "vanillaChat", vanillaChat).getBoolean();
	}

}