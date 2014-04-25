// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.screenshot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.client.upload.UploadHoster;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ScreenshotConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.irc.IRCChannel;
import net.blay09.mods.eirairc.irc.IRCConnection;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

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

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	private static IntBuffer intBuffer;
	private static int[] buffer;

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
		managedDir.mkdirs();
		thumbnailDir.mkdirs();
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
		if (screenshotFiles != null) {
			for (int i = 0; i < screenshotFiles.length; i++) {
				Screenshot screenshot = new Screenshot(screenshotFiles[i]);
				screenshot.setURL(prop.getProperty(screenshot.getName()));
				screenshots.add(screenshot);
			}
		}
		Collections.sort(screenshots, comparator);
	}

	public void save() {
		Properties prop = new Properties();
		for (int i = 0; i < screenshots.size(); i++) {
			Screenshot screenshot = screenshots.get(i);
			if (screenshot.isUploaded()) {
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

	public Screenshot takeScreenshot() {
		try {
			int width = Minecraft.getMinecraft().displayWidth;
			int height = Minecraft.getMinecraft().displayHeight;
			int k = width * height;
			if (intBuffer == null || intBuffer.capacity() < k) {
				intBuffer = BufferUtils.createIntBuffer(k);
				buffer = new int[k];
			}
			GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			intBuffer.clear();
			GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intBuffer);
			intBuffer.get(buffer);
			func_74289_a(buffer, width, height);
			BufferedImage bufferedImage = new BufferedImage(width, height, 1);
			bufferedImage.setRGB(0, 0, width, height, buffer, 0, width);
			File screenshotFile = new File(managedDir, getScreenshotName(managedDir));
			ImageIO.write(bufferedImage, "png", screenshotFile);
			Screenshot screenshot = new Screenshot(screenshotFile);
			screenshots.add(screenshot);
			Collections.sort(screenshots, comparator);
			return screenshot;
		} catch (Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}

	private static void func_74289_a(int[] buffer, int width, int height) {
		int[] aint1 = new int[width];
		int k = height / 2;

		for (int l = 0; l < k; ++l) {
			System.arraycopy(buffer, l * width, aint1, 0, width);
			System.arraycopy(buffer, (height - 1 - l) * width, buffer, l * width, width);
			System.arraycopy(aint1, 0, buffer, (height - 1 - l) * width, width);
		}
	}

	private static String getScreenshotName(File directory) {
		String s = dateFormat.format(new Date()).toString();
		int i = 1;
		while (true) {
			File file = new File(directory, s + (i == 1 ? "" : "_" + i) + ".png");
			if (!file.exists()) {
				return file.getName();
			}
			i++;
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
		if (host != null) {
			String uploadURL = host.uploadFile(screenshot.getFile());
			screenshot.setURL(uploadURL);
			save();
			return uploadURL;
		}
		return null;
	}
	
	public void shareScreenshot(Screenshot screenshot) {
		String ircMessage = Utils.getLocalizedMessage("irc.display.shareScreenshot", screenshot.getUploadURL());
		String mcMessage = "/me " + ircMessage;
		Minecraft.getMinecraft().thePlayer.sendChatMessage(mcMessage);
		for (IRCConnection connection : EiraIRC.instance.getConnections()) {
			ServerConfig serverConfig = Utils.getServerConfig(connection);
			for (IRCChannel channel : connection.getChannels()) {
				ChannelConfig channelConfig = serverConfig.getChannelConfig(channel);
				if (!channelConfig.isReadOnly()) {
					connection.sendChannelMessage(channel, IRCConnection.EMOTE_START + ircMessage + IRCConnection.EMOTE_END);
				}
			}
		}
	}

	public void handleNewScreenshot(Screenshot screenshot) {
		if (EiraIRC.proxy.isIngame()) {
			int action = ScreenshotConfig.screenshotAction;
			if (action != ScreenshotConfig.VALUE_NONE) {
				uploadScreenshot(screenshot);
				if (action == ScreenshotConfig.VALUE_UPLOADCLIPBOARD) {
					GuiScreen.setClipboardString(screenshot.getUploadURL());
				} else if (action == ScreenshotConfig.VALUE_UPLOADSHARE) {
					shareScreenshot(screenshot);
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
		if (screenshotFiles != null) {
			for (int i = 0; i < screenshotFiles.length; i++) {
				File newFile = new File(managedDir, screenshotFiles[i].getName());
				screenshotFiles[i].renameTo(newFile);
				Screenshot screenshot = new Screenshot(newFile);
				if (autoAction) {
					handleNewScreenshot(screenshot);
				}
				screenshots.add(screenshot);
			}
		}
		Collections.sort(screenshots, comparator);
	}

	public File getThumbnailDir() {
		return thumbnailDir;
	}
}
