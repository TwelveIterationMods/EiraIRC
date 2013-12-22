package blay09.mods.eirairc.client;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import blay09.mods.eirairc.config.Globals;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.ResourceManager;
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
				img.createGraphics().drawImage(ImageIO.read(screenshotFile).getScaledInstance(WIDTH, HEIGHT, Image.SCALE_SMOOTH), 0, 0, null);
				ImageIO.write(img, "png", thumbnailFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		resourceLocation = new ResourceLocation(Globals.MOD_ID, "thumbnails/" + thumbnailFile.getName());
	}

	@Override
	public void loadTexture(ResourceManager resourceManager) throws IOException {
		FileInputStream fis = new FileInputStream(thumbnailFile);
		bufferedImage = ImageIO.read(fis);
		fis.close();
		TextureUtil.uploadTextureImage(getGlTextureId(), this.bufferedImage);
	}

	public ResourceLocation getResourceLocation() {
		return resourceLocation;
	}

	public File getFile() {
		return thumbnailFile;
	}

}
