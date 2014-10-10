package net.blay09.mods.eirairc.client.gui;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Blay09 on 05.10.2014.
 */
public class EiraGui {

	public static final ResourceLocation tab = new net.minecraft.util.ResourceLocation("eirairc", "gfx/tab.png");

	public static void drawTexturedRect(int x, int y, int width, int height, int texCoordX, int texCoordY, int regionWidth, int regionHeight, int texWidth, int texHeight) {
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
