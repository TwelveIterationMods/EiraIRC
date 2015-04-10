package net.blay09.mods.eirairc.client.gui.base.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class GuiFileImage extends GuiImage {

	private final File file;

	public GuiFileImage(File file) {
		this.file = file;
	}

	@Override
	public BufferedImage loadImage() throws IOException {
		return ImageIO.read(file);
	}

}
