// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.client.graphics.TextureAtlas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
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

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + height, 0, u, v2);
		tessellator.addVertexWithUV(x + width, y + height, 0, u2, v2);
		tessellator.addVertexWithUV(x + width, y, 0, u2, v);
		tessellator.addVertexWithUV(x, y, 0, u, v);
		tessellator.draw();
	}

}
