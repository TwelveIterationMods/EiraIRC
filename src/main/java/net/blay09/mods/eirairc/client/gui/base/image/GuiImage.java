package net.blay09.mods.eirairc.client.gui.base.image;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureUtil;

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

		float renderWidth = imageWidth;
		float renderHeight = imageHeight;
		float factor;
		if(renderWidth > width) {
			factor = width / renderWidth;
			renderWidth *= factor;
			renderHeight *= factor;
		}
		if(renderHeight > height) {
			factor = height / renderHeight;
			renderWidth *= factor;
			renderHeight *= factor;
		}
		float renderX = xPos + width / 2 - renderWidth / 2;
		float renderY = yPos + height / 2 - renderHeight / 2;

		GlStateManager.func_179144_i(textureId); // bindTexture
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer renderer = tessellator.getWorldRenderer();
		renderer.startDrawingQuads();
		renderer.addVertexWithUV(renderX, renderY + renderHeight, zLevel, 0, 1);
		renderer.addVertexWithUV(renderX + renderWidth, renderY + renderHeight, zLevel, 1, 1);
		renderer.addVertexWithUV(renderX + renderWidth, renderY, zLevel, 1, 0);
		renderer.addVertexWithUV(renderX, renderY, zLevel, 0, 0);
		tessellator.draw();
	}

	public void dispose() {
		TextureUtil.deleteTexture(textureId);
		textureId = -1;
	}
}
