// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client;

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

public class ScreenshotManager {

	private static ScreenshotManager instance;
	public static void create() {
		instance = new ScreenshotManager();
		instance.load();
		instance.findNewScreenshots();
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
			return (int) (first.getFile().lastModified() - second.getFile().lastModified());
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
			prop.setProperty(screenshot.getName(), screenshot.getUploadURL());
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
	
	public void handleNewScreenshot(Screenshot screenshot) {
		// TODO if ingame: upload & share if config is set
	}
	
	public void findNewScreenshots() {
		File[] screenshotFiles = screenshotDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String fileName) {
				return fileName.endsWith(".png");
			}
		});
		if(screenshotFiles != null) {
			for(int i = 0; i < screenshotFiles.length; i++) {
				Screenshot screenshot = new Screenshot(screenshotFiles[i]);
				handleNewScreenshot(screenshot);
				screenshots.add(screenshot);
				screenshotFiles[i].renameTo(new File(managedDir, screenshotFiles[i].getName()));
				System.out.println("Found new screenshot: " + screenshot.getName());
			}
		}
		Collections.sort(screenshots, comparator);
	}
	public File getThumbnailDir() {
		return thumbnailDir;
	}
}
