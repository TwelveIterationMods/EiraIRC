// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.client.graphics;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class TextureRegion {

	private static final int TEXTURE_WIDTH = 256;
	private static final int TEXTURE_HEIGHT = 256;

	public final ResourceLocation texture;
	public final String name;
	private int regionX;
	private int regionY;
	private int regionWidth;
	private int regionHeight;

	public TextureRegion(ResourceLocation texture, String name) {
		this.texture = texture;
		this.name = name;
	}

	public void setRegionX(int regionX) {
		this.regionX = regionX;
	}

	public int getRegionX() {
		return regionX;
	}

	public void setRegionY(int regionY) {
		this.regionY = regionY;
	}

	public int getRegionY() {
		return regionY;
	}

	public void setRegionWidth(int regionWidth) {
		this.regionWidth = regionWidth;
	}

	public int getRegionWidth() {
		return regionWidth;
	}

	public void setRegionHeight(int regionHeight) {
		this.regionHeight = regionHeight;
	}

	public int getRegionHeight() {
		return regionHeight;
	}

	public void draw(int x, int y) {
		draw(x, y, regionWidth, regionHeight);
	}

	public void draw(int x, int y, int width, int height) {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		float u = (float) regionX / (float) TEXTURE_WIDTH;
		float v = (float) regionY / (float) TEXTURE_HEIGHT;
		float u2 = (float) (regionX + regionWidth) / (float) TEXTURE_WIDTH;
		float v2 = (float) (regionY + regionHeight) / (float) TEXTURE_HEIGHT;

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + height, 0, u, v2);
		tessellator.addVertexWithUV(x + width, y + height, 0, u2, v2);
		tessellator.addVertexWithUV(x + width, y, 0, u2, v);
		tessellator.addVertexWithUV(x, y, 0, u, v);
		tessellator.draw();
	}

}
