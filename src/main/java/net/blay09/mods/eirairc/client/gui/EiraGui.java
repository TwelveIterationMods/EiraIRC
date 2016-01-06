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
		renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		renderer.pos(x, y + height, zLevel).tex(u, v2).endVertex();
		renderer.pos(x + width, y + height, zLevel).tex(u2, v2).endVertex();
		renderer.pos(x + width, y, zLevel).tex(u2, v).endVertex();
		renderer.pos(x, y, zLevel).tex(u, v).endVertex();
		tessellator.draw();
	}

}
