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
	}
	public static ScreenshotManager getInstance() {
		return instance;
	}
	
	private final File screenshotDir = new File(Minecraft.getMinecraft().mcDataDir, "screenshots");
	private final List<Screenshot> screenshots = new ArrayList<Screenshot>();
	private final Comparator<Screenshot> comparator = new Comparator<Screenshot>() {
		@Override
		public int compare(Screenshot first, Screenshot second) {
			return (int) (second.getFile().lastModified() - first.getFile().lastModified());
		}
	};
	
	public ScreenshotManager() {
		Properties prop = new Properties();
		try {
			FileInputStream in = new FileInputStream(new File(screenshotDir, "eirairc.properties"));
			prop.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		File[] screenshotFiles = screenshotDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File file, String fileName) {
				return fileName.endsWith(".png");
			}
		});
		for(int i = 0; i < screenshotFiles.length; i++) {
			Screenshot screenshot = new Screenshot(screenshotFiles[i]);
			screenshot.setURL(prop.getProperty(screenshot.getName()));
			screenshots.add(screenshot);
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
		screenshots.remove(screenshot);
	}
	
}
