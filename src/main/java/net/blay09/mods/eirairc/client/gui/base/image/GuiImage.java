// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.base.image;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class GuiImage extends AbstractTexture {

	private float imageWidth;
	private float imageHeight;

	public void loadTexture() {
		try {
			loadTexture(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadTexture(IResourceManager resourceManager) throws IOException {
		try {
			BufferedImage bufferedImage = loadImage();
			if(bufferedImage != null) {
				imageWidth = bufferedImage.getWidth();
				imageHeight = bufferedImage.getHeight();
				TextureUtil.uploadTextureImage(getGlTextureId(), bufferedImage);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public abstract BufferedImage loadImage() throws IOException;

	public void draw(int xPos, int yPos, int width, int height, float zLevel) {
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

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getGlTextureId());
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(renderX, renderY + renderHeight, zLevel, 0, 1);
		tessellator.addVertexWithUV(renderX + renderWidth, renderY + renderHeight, zLevel, 1, 1);
		tessellator.addVertexWithUV(renderX + renderWidth, renderY, zLevel, 1, 0);
		tessellator.addVertexWithUV(renderX, renderY, zLevel, 0, 0);
		tessellator.draw();
	}

	public void dispose() {
		TextureUtil.deleteTexture(getGlTextureId());
	}
}
