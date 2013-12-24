// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.screenshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import blay09.mods.eirairc.EiraIRC;
import blay09.mods.eirairc.client.upload.UploadHoster;
import blay09.mods.eirairc.config.ChannelConfig;
import blay09.mods.eirairc.config.ScreenshotConfig;
import blay09.mods.eirairc.config.ServerConfig;
import blay09.mods.eirairc.irc.IRCChannel;
import blay09.mods.eirairc.irc.IRCConnection;
import blay09.mods.eirairc.util.Utils;

public class ScreenshotManager {

	private static ScreenshotManager instance;
	public static void create() {
		instance = new ScreenshotManager();
		instance.load();
		instance.findNewScreenshots(false);
	}
	public static ScreenshotManager getInstance() {
		return instance;
	}
	
	private final File screenshotDir = new File(Minecraft.getMinecraft().mcDataDir, "screenshots");
	private final File managedDir = new File(screenshotDir, "managed");
	private final File thumbnailDir = new File(screenshotDir, "thumbnails");
	private final List<Screenshot> screenshots = new ArrayList<Screenshot>();
	private final Comparator<Screenshot> comparator = new Comparator<Screenshot>() {
		@Override
		public int compare(Screenshot first, Screenshot second) {
			return (int) (second.getFile().lastModified() - first.getFile().lastModified());
		}
	};
	
	public ScreenshotManager() {
		managedDir.mkdir();
		thumbnailDir.mkdir();
	}
	
	public void load() {
		Properties prop = new Properties();
		try {
			FileInputStream in = new FileInputStream(new File(screenshotDir, "eirairc.properties"));
			prop.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File[] screenshotFiles = managedDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String fileName) {
				return fileName.endsWith(".png");
			}
		});
		if(screenshotFiles != null) {
			for(int i = 0; i < screenshotFiles.length; i++) {
				Screenshot screenshot = new Screenshot(screenshotFiles[i]);
				screenshot.setURL(prop.getProperty(screenshot.getName()));
				screenshots.add(screenshot);
			}
		}
		Collections.sort(screenshots, comparator);
	}
	
	public void save() {
		Properties prop = new Properties();
		for(int i = 0; i < screenshots.size(); i++) {
			Screenshot screenshot = screenshots.get(i);
			if(screenshot.isUploaded()) {
				prop.setProperty(screenshot.getName(), screenshot.getUploadURL());
			}
		}
		try {
			FileOutputStream out = new FileOutputStream(new File(screenshotDir, "eirairc.properties"));
			prop.store(out, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Collection<Screenshot> getScreenshots() {
		return screenshots;
	}
	
	public void deleteScreenshot(Screenshot screenshot) {
		screenshot.getFile().delete();
		screenshot.getThumbnail().getFile().delete();
		screenshots.remove(screenshot);
	}
	
	public String uploadScreenshot(Screenshot screenshot) {
		UploadHoster host = UploadHoster.getUploadHoster(ScreenshotConfig.uploadHoster);
		if(host != null) {
			String uploadURL = host.uploadFile(screenshot.getFile());
			screenshot.setURL(uploadURL);
			save();
			return uploadURL;
		}
		return null;
	}
	
	public void handleNewScreenshot(Screenshot screenshot) {
		if(EiraIRC.proxy.isIngame()) {
			int action = ScreenshotConfig.screenshotAction;
			if(action != ScreenshotConfig.SCREENSHOT_NONE) {
				uploadScreenshot(screenshot);
				if(action == ScreenshotConfig.SCREENSHOT_UPLOADCLIPBOARD) {
					GuiScreen.setClipboardString(screenshot.getUploadURL());
				} else if(action == ScreenshotConfig.SCREENSHOT_UPLOADSHARE) {
					String ircMessage = "just uploaded a screenshot: " + screenshot.getUploadURL();
					String mcMessage = "/me " + ircMessage;
					Minecraft.getMinecraft().thePlayer.sendChatMessage(mcMessage);
					for(IRCConnection connection : EiraIRC.instance.getConnections()) {
						ServerConfig serverConfig = Utils.getServerConfig(connection);
						for(IRCChannel channel : connection.getChannels()) {
							ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
							if(!channelConfig.isReadOnly()) {
								connection.sendChannelMessage(channel, "ACTION " + ircMessage + "");
							}
						}
					}
				}
			}
		}
	}
	
	public void findNewScreenshots(boolean autoAction) {
		File[] screenshotFiles = screenshotDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String fileName) {
				return fileName.endsWith(".png");
			}
		});
		if(screenshotFiles != null) {
			for(int i = 0; i < screenshotFiles.length; i++) {
				File newFile = new File(managedDir, screenshotFiles[i].getName());
				screenshotFiles[i].renameTo(newFile);
				Screenshot screenshot = new Screenshot(newFile);
				if(autoAction) {
					handleNewScreenshot(screenshot);
				}
				screenshots.add(screenshot);
				System.out.println("Found new screenshot: " + screenshot.getName());
			}
		}
		Collections.sort(screenshots, comparator);
	}
	public File getThumbnailDir() {
		return thumbnailDir;
	}
}
