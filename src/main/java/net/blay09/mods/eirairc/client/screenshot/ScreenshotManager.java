// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.screenshot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCChannel;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.api.IRCContext;
import net.blay09.mods.eirairc.api.IRCUser;
import net.blay09.mods.eirairc.api.bot.IRCBot;
import net.blay09.mods.eirairc.api.event.RelayChat;
import net.blay09.mods.eirairc.api.upload.IUploadHoster;
import net.blay09.mods.eirairc.api.upload.UploadManager;
import net.blay09.mods.eirairc.config.DisplayConfig;
import net.blay09.mods.eirairc.config.ScreenshotConfig;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.MessageFormat;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ScreenshotManager {

	private static ScreenshotManager instance;

	public static void create() {
		instance = new ScreenshotManager();
		instance.load();
	}

	public static ScreenshotManager getInstance() {
		return instance;
	}

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
	private static final String PROPERTY_DELETE_URL = "_delete";
	private static final String PROPERTY_HOSTER = "_hoster";
	private static IntBuffer intBuffer;
	private static int[] buffer;
	
	private final File screenshotDir = new File(Minecraft.getMinecraft().mcDataDir, "screenshots");
	private final File thumbnailDir = new File(screenshotDir, "thumbnails");
	private final List<Screenshot> screenshots = new ArrayList<Screenshot>();
	private final Comparator<Screenshot> comparator = new Comparator<Screenshot>() {
		@Override
		public int compare(Screenshot first, Screenshot second) {
			long flm = first.getFile().lastModified();
			long slm = second.getFile().lastModified();
			if (flm < slm) {
				return 1;
			} else if(flm > slm) {
				return -1;
			}
			return 0;
		}
	};

	private final List<AsyncUploadScreenshot> uploadTasks = new ArrayList<AsyncUploadScreenshot>();
	private long lastScreenshotScan;
	
	public ScreenshotManager() {
		thumbnailDir.mkdirs();
	}

	public void load() {
		Properties prop = new Properties();
		try {
			FileInputStream in = new FileInputStream(new File(screenshotDir, "eirairc.properties"));
			prop.load(in);
			in.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		File[] screenshotFiles = screenshotDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String fileName) {
				return fileName.endsWith(".png");
			}
		});
		if (screenshotFiles != null) {
			for (int i = 0; i < screenshotFiles.length; i++) {
				Screenshot screenshot = new Screenshot(screenshotFiles[i]);
				screenshot.setURL(prop.getProperty(screenshot.getName()));
				screenshot.setDeleteURL(prop.getProperty(screenshot.getName() + PROPERTY_DELETE_URL));
				screenshot.setHoster(prop.getProperty(screenshot.getName() + PROPERTY_HOSTER));
				screenshots.add(screenshot);
			}
		}
		lastScreenshotScan = System.currentTimeMillis();
		Collections.sort(screenshots, comparator);
	}

	public void save() {
		Properties prop = new Properties();
		for (int i = 0; i < screenshots.size(); i++) {
			Screenshot screenshot = screenshots.get(i);
			if (screenshot.isUploaded()) {
				prop.setProperty(screenshot.getName(), screenshot.getUploadURL());
				prop.setProperty(screenshot.getName() + PROPERTY_DELETE_URL, screenshot.getDeleteURL() != null ? screenshot.getDeleteURL() : "");
				prop.setProperty(screenshot.getName() + PROPERTY_HOSTER, screenshot.getHoster() != null ? screenshot.getHoster() : "");
			}
		}
		try {
			FileOutputStream out = new FileOutputStream(new File(screenshotDir, "eirairc.properties"));
			prop.store(out, null);
			out.close();
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
			doSomeCrazyMagic(buffer, width, height);
			BufferedImage bufferedImage = new BufferedImage(width, height, 1);
			bufferedImage.setRGB(0, 0, width, height, buffer, 0, width);
			File screenshotFile = new File(screenshotDir, getScreenshotName(screenshotDir));
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

	private static void doSomeCrazyMagic(int[] buffer, int width, int height) {
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

	public void uploadScreenshot(Screenshot screenshot, int followUpAction) {
		IUploadHoster hoster = UploadManager.getUploadHoster(ScreenshotConfig.uploadHoster);
		if (hoster != null) {
			uploadTasks.add(new AsyncUploadScreenshot(hoster, screenshot, followUpAction));
		}
	}
	
	public void clientTick(ClientTickEvent event) {
		for(int i = uploadTasks.size() - 1; i >= 0; i--) {
			if(uploadTasks.get(i).isComplete()) {
				AsyncUploadScreenshot task = uploadTasks.remove(i);
				if(task.getScreenshot().isUploaded()) {
					int action = task.getFollowUpAction();
					if (action == ScreenshotConfig.VALUE_UPLOADCLIPBOARD) {
						Utils.setClipboardString(task.getScreenshot().getUploadURL());
					} else if (action == ScreenshotConfig.VALUE_UPLOADSHARE) {
						shareScreenshot(task.getScreenshot());
					}
					save();
				}
			}
		}
	}
	
	public void shareScreenshot(Screenshot screenshot) {
		String text = Utils.getLocalizedMessage("irc.display.shareScreenshot", screenshot.getUploadURL());
		if(EiraIRC.instance.getChatSessionHandler().isMinecraftTarget()) {
			String mcMessage = "/me " + text;
			Minecraft.getMinecraft().thePlayer.sendChatMessage(mcMessage);
		} else {
			// TODO damn, clean up your shitty code once 1.6.4 and 1.7.2 is dropped
			EntityPlayer sender = Minecraft.getMinecraft().thePlayer;
			MinecraftForge.EVENT_BUS.post(new RelayChat(sender, text, true));
			String chatTarget = EiraIRC.instance.getChatSessionHandler().getChatTarget();
			if(chatTarget == null) {
				return;
			}
			String[] target = chatTarget.split("/");
			IRCConnection connection = EiraIRC.instance.getConnection(target[0]);
			if(connection != null) {
				IRCBot bot = connection.getBot();
				EnumChatFormatting emoteColor;
				IChatComponent chatComponent;
				if (target[1].startsWith("#")) {
					IRCChannel targetChannel = connection.getChannel(target[1]);
					if (targetChannel == null) {
						return;
					}
					emoteColor = Utils.getColorFormatting(ConfigHelper.getEmoteColor(targetChannel));
					chatComponent = MessageFormat.formatChatComponent(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(targetChannel)).mcSendChannelEmote, sender, text, MessageFormat.Target.IRC, MessageFormat.Mode.Emote);
				} else {
					IRCUser targetUser = connection.getUser(target[1]);
					if (targetUser == null) {
						return;
					}
					emoteColor = Utils.getColorFormatting(ConfigHelper.getEmoteColor(targetUser));
					chatComponent = MessageFormat.formatChatComponent(ConfigHelper.getDisplayFormat(bot.getDisplayFormat(targetUser)).mcSendPrivateEmote, sender, text, MessageFormat.Target.IRC, MessageFormat.Mode.Emote);
				}
				if (emoteColor != null) {
					chatComponent.getChatStyle().setColor(emoteColor);
				}
				Utils.addMessageToChat(chatComponent);
			}
		}
	}

	public void handleNewScreenshot(Screenshot screenshot) {
		if (EiraIRC.proxy.isIngame()) {
			int action = ScreenshotConfig.screenshotAction;
			if (action == ScreenshotConfig.VALUE_UPLOADCLIPBOARD || action == ScreenshotConfig.VALUE_UPLOADSHARE) {
				uploadScreenshot(screenshot, action);
			}
		}
	}

	public void findNewScreenshots(boolean autoAction) {
		File[] screenshotFiles = screenshotDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if(!file.getName().endsWith(".png")) {
					return false;
				}
				if(file.lastModified() > lastScreenshotScan) {
					return true;
				}
				return false;
			}
		});
		if (screenshotFiles != null) {
			for (int i = 0; i < screenshotFiles.length; i++) {
				Screenshot screenshot = new Screenshot(screenshotFiles[i]);
				if (autoAction) {
					handleNewScreenshot(screenshot);
				}
				screenshots.add(screenshot);
			}
		}
		Collections.sort(screenshots, comparator);
		lastScreenshotScan = System.currentTimeMillis();
	}

	public File getThumbnailDir() {
		return thumbnailDir;
	}

}
