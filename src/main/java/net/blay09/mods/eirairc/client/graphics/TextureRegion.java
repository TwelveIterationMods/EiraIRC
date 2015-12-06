package net.blay09.mods.eirairc.client.graphics;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer renderer = tessellator.getWorldRenderer();
		renderer.func_181668_a(7, DefaultVertexFormats.field_181707_g); // startDrawingQuads
		renderer.func_181662_b(x, y + height, 0).func_181673_a(u, v2).func_181675_d(); // addVertexPosition addVertexUV finishVertex
		renderer.func_181662_b(x + width, y + height, 0).func_181673_a(u2, v2).func_181675_d(); // addVertexPosition addVertexUV finishVertex
		renderer.func_181662_b(x + width, y, 0).func_181673_a(u2, v).func_181675_d(); // addVertexPosition addVertexUV finishVertex
		renderer.func_181662_b(x, y, 0).func_181673_a(u, v).func_181675_d(); // addVertexPosition addVertexUV finishVertex
		tessellator.draw();
	}

}
