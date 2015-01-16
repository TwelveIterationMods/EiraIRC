package net.blay09.mods.eirairc.client.gui.base.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;

/**
 * Created by Blay09 on 10.10.2014.
 */
public class GuiURLImage extends GuiImage {

	private final URL url;

	public GuiURLImage(URL url) {
		this.url = url;
	}

	@Override
	public BufferedImage loadImage() throws IOException {
		return ImageIO.read(url);
	}

}
