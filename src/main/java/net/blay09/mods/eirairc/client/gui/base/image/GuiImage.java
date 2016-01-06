package net.blay09.mods.eirairc.client.gui.base.image;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import java.awt.image.BufferedImage;
import java.io.IOException;

public abstract class GuiImage {

	private int textureId = -1;
	private BufferedImage loadBuffer;
	private float imageWidth;
	private float imageHeight;

	public void loadTexture() {
		new Thread(() -> {
            try {
                loadBuffer = loadImage();
            } catch (IOException e) {
                e.printStackTrace();
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

		GlStateManager.bindTexture(textureId);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer renderer = tessellator.getWorldRenderer();
		renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		renderer.pos(renderX, renderY + renderHeight, zLevel).tex(0, 1).endVertex();
		renderer.pos(renderX + renderWidth, renderY + renderHeight, zLevel).tex(1, 1).endVertex();
		renderer.pos(renderX + renderWidth, renderY, zLevel).tex(1, 0).endVertex();
		renderer.pos(renderX, renderY, zLevel).tex(0, 0).endVertex();
		tessellator.draw();
	}

	public void dispose() {
		TextureUtil.deleteTexture(textureId);
		textureId = -1;
	}
}
