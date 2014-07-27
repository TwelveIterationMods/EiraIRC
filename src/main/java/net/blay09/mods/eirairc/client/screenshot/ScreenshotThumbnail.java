// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.screenshot;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.blay09.mods.eirairc.util.Globals;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class ScreenshotThumbnail extends AbstractTexture {

	public static final int WIDTH = 128;
	public static final int HEIGHT = 64;
	
	private File thumbnailFile;
	private ResourceLocation resourceLocation;
	private BufferedImage bufferedImage;

	public ScreenshotThumbnail(File screenshotFile) {
		this.thumbnailFile = new File(ScreenshotManager.getInstance().getThumbnailDir(), screenshotFile.getName());
		if(!thumbnailFile.exists()) {
			try {
				BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
				BufferedImage screenshotImage = ImageIO.read(screenshotFile);
				if(screenshotImage != null) {
					img.createGraphics().drawImage(screenshotImage.getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH), 0, 0, null);
				}
				ImageIO.write(img, "png", thumbnailFile);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IndexOutOfBoundsException e) {
				System.out.println("Could not generate thumbnail for malformed screenshot file: " + screenshotFile.getName() + " (" + e.getMessage() + ")");
		 	}
		}
		resourceLocation = new ResourceLocation(Globals.MOD_ID, "thumbnails/" + thumbnailFile.getName());
	}

	@Override
	public void loadTexture(IResourceManager resourceManager) {
		try {
			FileInputStream fis = new FileInputStream(thumbnailFile);
			bufferedImage = ImageIO.read(fis);
			fis.close();
			if(bufferedImage != null) {
				TextureUtil.uploadTextureImage(getGlTextureId(), this.bufferedImage);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ResourceLocation getResourceLocation() {
		return resourceLocation;
	}

	public File getFile() {
		return thumbnailFile;
	}

}
