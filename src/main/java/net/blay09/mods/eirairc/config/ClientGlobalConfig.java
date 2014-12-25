package net.blay09.mods.eirairc.config;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.blay09.mods.eirairc.api.upload.UploadManager;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.config.Configuration;
import org.lwjgl.input.Keyboard;

import java.io.File;

/**
 * Created by Blay09 on 29.09.2014.
 */
@SideOnly(Side.CLIENT)
public class ClientGlobalConfig {

	public static final String GENERAL = "general";
	public static final String SCREENSHOTS = "screenshots";
	public static final String NOTIFICATIONS = "notifications";
	public static final String COMPATIBILITY = "compatibility";

	public static Configuration thisConfig;

	// General
	public static boolean hudRecState = false;
	public static boolean persistentConnection = true;

	// Screenshots
	public static String screenshotHoster = "";
	public static ScreenshotAction screenshotAction = ScreenshotAction.None;
	public static int uploadBufferSize = 1024;

	// Keybinds
	public static final KeyBinding keyScreenshotShare = new KeyBinding("key.irc.screenshotShare", 0, "key.categories.irc");
	public static final KeyBinding keyOpenScreenshots = new KeyBinding("key.irc.openScreenshots", 0, "key.categories.irc");
	public static final KeyBinding keyToggleRecording = new KeyBinding("key.irc.toggleRecording", 0, "key.categories.irc");
	public static final KeyBinding keyToggleLive = new KeyBinding("key.irc.toggleLive", 0, "key.categories.irc");
	public static final KeyBinding keyToggleTarget = new KeyBinding("key.irc.toggleTarget", Keyboard.KEY_TAB, "key.categories.irc");
	public static final KeyBinding keyOpenMenu = new KeyBinding("key.irc.openMenu", Keyboard.KEY_I, "key.categories.irc");

	// Notifications
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
		if(thisConfig == null) {
			thisConfig = new Configuration(new File(configDir, "client.cfg"));
		}

		// General
		hudRecState = thisConfig.getBoolean("hudRecState", GENERAL, hudRecState, "", "eirairc:config.property.hudRecState");
		persistentConnection = thisConfig.getBoolean("persistentConnection", GENERAL, persistentConnection, "", "eirairc:config.property.persistentConnection");

		// Screenshots
		screenshotHoster = thisConfig.getString("uploadHoster", SCREENSHOTS, screenshotHoster, "", UploadManager.getAvailableHosters(), "eirairc:config.property.uploadHoster");
		screenshotAction = ScreenshotAction.valueOf(thisConfig.getString("autoAction", SCREENSHOTS, screenshotAction.name(), "", ScreenshotAction.NAMES, "eirairc:config.property.autoAction"));
		uploadBufferSize = thisConfig.getInt("uploadBufferSize", SCREENSHOTS, uploadBufferSize, 256, 4096, "", "eirairc:config.property.uploadBufferSize");

		// Notifications
		notificationSound = thisConfig.getString("soundName", NOTIFICATIONS, notificationSound, "", "eirairc:config.property.soundName");
		notificationSoundVolume = thisConfig.getFloat("soundVolume", NOTIFICATIONS, notificationSoundVolume, 0f, 1f, "", "eirairc:config.property.soundVolume");
		notificationSoundPitch = thisConfig.getFloat("soundPitch", NOTIFICATIONS, notificationSoundPitch, 0.5f, 2f, "", "eirairc:config.property.soundPitch");
		ntfyFriendJoined = NotificationStyle.valueOf(thisConfig.getString("friendJoined", NOTIFICATIONS, ntfyFriendJoined.name(), "", NotificationStyle.NAMES, "eirairc:config.property.friendJoined"));
		ntfyNameMentioned = NotificationStyle.valueOf(thisConfig.getString("nameMentioned", NOTIFICATIONS, ntfyNameMentioned.name(), "", NotificationStyle.NAMES, "eirairc:config.property.nameMentioned"));
		ntfyUserRecording = NotificationStyle.valueOf(thisConfig.getString("userRecording", NOTIFICATIONS, ntfyUserRecording.name(), "", NotificationStyle.NAMES, "eirairc:config.property.userRecording"));
		ntfyPrivateMessage = NotificationStyle.valueOf(thisConfig.getString("privateMessage", NOTIFICATIONS, ntfyPrivateMessage.name(), "", NotificationStyle.NAMES, "eirairc:config.property.privateMessage"));

		// Compatibility
		clientBridge = thisConfig.getBoolean("clientBridge", COMPATIBILITY, clientBridge, "", "eirairc:config.property.clientBridge");
		clientBridgeMessageToken = thisConfig.getString("clientBridgeMessageToken", COMPATIBILITY, clientBridgeMessageToken, "", "eirairc:config.property.clientBridgeMessageToken");
		clientBridgeNickToken = thisConfig.getString("clientBridgeNickToken", COMPATIBILITY, clientBridgeNickToken, "", "eirairc:config.property.clientBridgeNickToken");
		disableChatToggle = thisConfig.getBoolean("disableChatToggle", COMPATIBILITY, disableChatToggle, "", "eirairc:config.property.disableChatToggle");
		vanillaChat = thisConfig.getBoolean("vanillaChat", COMPATIBILITY, vanillaChat, "", "eirairc:config.property.vanillaChat");
	}

	public static void save() {
		// Category Comments
		thisConfig.setCategoryComment(GENERAL, I18n.format("eirairc:config.category.general.tooltip"));
		thisConfig.setCategoryComment(SCREENSHOTS, I18n.format("eirairc:config.category.screenshots.tooltip"));
		thisConfig.setCategoryComment(NOTIFICATIONS, I18n.format("eirairc:config.category.notifications.tooltip"));
		thisConfig.setCategoryComment(COMPATIBILITY, I18n.format("eirairc:config.category.compatibility.tooltip"));

		// General
		thisConfig.get(GENERAL, "hudRecState", false, I18n.format("eirairc:config.property.hudRecState")).set(hudRecState);
		thisConfig.get(GENERAL, "persistentConnection", false, I18n.format("eirairc:config.property.persistentConnection")).set(persistentConnection);

		// Screenshots
		thisConfig.get(SCREENSHOTS, "uploadHoster", "", I18n.format("eirairc:config.property.uploadHoster")).set(screenshotHoster);
		thisConfig.get(SCREENSHOTS, "autoAction", "", I18n.format("eirairc:config.property.autoAction")).set(screenshotAction.name());
		thisConfig.get(SCREENSHOTS, "uploadBufferSize", 0, I18n.format("eirairc:config.property.uploadBufferSize")).set(uploadBufferSize);

		// Notifications
		thisConfig.get(NOTIFICATIONS, "soundName", "", I18n.format("eirairc:config.property.soundName")).set(notificationSound);
		thisConfig.get(NOTIFICATIONS, "soundVolume", 0f, I18n.format("eirairc:config.property.soundVolume")).set(notificationSoundVolume);
		thisConfig.get(NOTIFICATIONS, "soundPitch", 0f, I18n.format("eirairc:config.property.soundPitch")).set(notificationSoundPitch);
		thisConfig.get(NOTIFICATIONS, "friendJoined", "", I18n.format("eirairc:config.property.friendJoined")).set(ntfyFriendJoined.name());
		thisConfig.get(NOTIFICATIONS, "nameMentioned", "", I18n.format("eirairc:config.property.nameMentioned")).set(ntfyNameMentioned.name());
		thisConfig.get(NOTIFICATIONS, "userRecording", "", I18n.format("eirairc:config.property.userRecording")).set(ntfyUserRecording.name());
		thisConfig.get(NOTIFICATIONS, "privateMessage", "", I18n.format("eirairc:config.property.privateMessage")).set(ntfyPrivateMessage.name());

		// Compatibility
		thisConfig.get(COMPATIBILITY, "clientBridge", false, I18n.format("eirairc:config.property.clientBridge")).set(clientBridge);
		thisConfig.get(COMPATIBILITY, "clientBridgeMessageToken", I18n.format("eirairc:config.property.clientBridgeMessageToken")).set(clientBridgeMessageToken);
		thisConfig.get(COMPATIBILITY, "clientBridgeNickToken", I18n.format("eirairc:config.property.clientBridgeNickToken")).set(clientBridgeNickToken);
		thisConfig.get(COMPATIBILITY, "disableChatToggle", false, I18n.format("eirairc:config.property.disableChatToggle")).set(disableChatToggle);
		thisConfig.get(COMPATIBILITY, "vanillaChat", false, I18n.format("eirairc:config.property.vanillaChat")).set(vanillaChat);

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
		keyOpenMenu.setKeyCode(legacyConfig.get("keybinds", "keyMenu", keyOpenMenu.getKeyCodeDefault()).getInt());
		keyToggleTarget.setKeyCode(legacyConfig.get("keybinds", "keyToggleTarget", keyToggleTarget.getKeyCodeDefault()).getInt());
		keyToggleLive.setKeyCode(legacyConfig.get("keybinds", "keyToggleLive", keyToggleLive.getKeyCodeDefault()).getInt());
		keyToggleRecording.setKeyCode(legacyConfig.get("keybinds", "keyToggleRecording", keyToggleRecording.getKeyCodeDefault()).getInt());
		keyScreenshotShare.setKeyCode(legacyConfig.get("keybinds", "keyScreenshotShare", keyScreenshotShare.getKeyCodeDefault()).getInt());
		keyOpenScreenshots.setKeyCode(legacyConfig.get("keybinds", "keyOpenScreenshots", keyOpenScreenshots.getKeyCodeDefault()).getInt());

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