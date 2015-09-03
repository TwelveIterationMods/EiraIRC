// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.client.gui.base.image;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class GuiImage {

	private int textureId = -1;
	private BufferedImage loadBuffer;
	private float imageWidth;
	private float imageHeight;

	public void loadTexture() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					loadBuffer = loadImage();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public boolean isLoaded() {
		return textureId != -1;
	}

	public abstract BufferedImage loadImage() throws IOException;

	public void draw(int xPos, int yPos, int width, int height, float zLevel) {
		if(loadBuffer != null) {
			imageWidth = loadBuffer.getWidth();
			imageHeight = loadBuffer.getHeight();
			textureId = TextureUtil.glGenTextures();
			TextureUtil.uploadTextureImage(textureId, loadBuffer);
			loadBuffer = null;
		}

		if(textureId != -1) {
			float renderWidth = imageWidth;
			float renderHeight = imageHeight;
			float factor;
			if (renderWidth > width) {
				factor = width / renderWidth;
				renderWidth *= factor;
				renderHeight *= factor;
			}
			if (renderHeight > height) {
				factor = height / renderHeight;
				renderWidth *= factor;
				renderHeight *= factor;
			}
			float renderX = xPos + width / 2 - renderWidth / 2;
			float renderY = yPos + height / 2 - renderHeight / 2;

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(renderX, renderY + renderHeight, zLevel, 0, 1);
			tessellator.addVertexWithUV(renderX + renderWidth, renderY + renderHeight, zLevel, 1, 1);
			tessellator.addVertexWithUV(renderX + renderWidth, renderY, zLevel, 1, 0);
			tessellator.addVertexWithUV(renderX, renderY, zLevel, 0, 0);
			tessellator.draw();
		}
	}

	public void dispose() {
		TextureUtil.deleteTexture(textureId);
	}
}
