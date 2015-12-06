package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.client.graphics.TextureAtlas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;


public class EiraGui {

	public static TextureAtlas atlas;

	public static void init(IResourceManager resourceManager) {
		try {
			atlas = new TextureAtlas(resourceManager, new ResourceLocation("eirairc", "gfx/eiragui.pack"));
		} catch (IOException e) {
			Minecraft.getMinecraft().displayCrashReport(new CrashReport("Could not load EiraIRC texture atlas", e));
		}
	}

	public static void drawTexturedRect(int x, int y, int width, int height, int texCoordX, int texCoordY, int regionWidth, int regionHeight, float zLevel, int texWidth, int texHeight) {
		float u = (float) texCoordX / (float) texWidth;
		float v = (float) texCoordY / (float) texHeight;
		float u2 = (float) (texCoordX + regionWidth) / (float) texWidth;
		float v2 = (float) (texCoordY + regionHeight) / (float) texHeight;

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer renderer = tessellator.getWorldRenderer();
		renderer.func_181668_a(7, DefaultVertexFormats.field_181707_g); // startDrawingQuads
		renderer.func_181662_b(x, y + height, zLevel).func_181673_a(u, v2).func_181675_d(); // addVertexPosition addVertexUV finishVertex
		renderer.func_181662_b(x + width, y + height, zLevel).func_181673_a(u2, v2).func_181675_d(); // addVertexPosition addVertexUV finishVertex
		renderer.func_181662_b(x + width, y, zLevel).func_181673_a(u2, v).func_181675_d(); // addVertexPosition addVertexUV finishVertex
		renderer.func_181662_b(x, y, zLevel).func_181673_a(u, v).func_181675_d(); // addVertexPosition addVertexUV finishVertex
		tessellator.draw();
	}

}
