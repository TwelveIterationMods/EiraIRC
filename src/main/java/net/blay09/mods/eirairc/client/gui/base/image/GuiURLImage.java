// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.client.gui.base.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

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
