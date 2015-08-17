package net.blay09.mods.eirairc.client.gui.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;


public class GuiLabel extends Gui {

	private static final int LINE_SPACING = 3;

	private final Minecraft mc;
	private final int posX;
	private final int posY;
	private final String[] lines;
	private final int color;
	private HAlignment hAlign = HAlignment.Left;
	private int alignWidth;

	public enum HAlignment {
		Left,
		Center,
		Right
	}

	public GuiLabel(String text, int posX, int posY, int color) {
		this.mc = Minecraft.getMinecraft();
		this.lines = text.split("\n");
		this.posX = posX;
		this.posY = posY;
		this.color = color;
	}

	public void setHAlignment(HAlignment hAlign, int alignWidth) {
		this.hAlign = hAlign;
		this.alignWidth = alignWidth;
	}

	public void drawLabel() {
		for(int i = 0; i < lines.length; i++) {
			int textWidth = mc.fontRenderer.getStringWidth(lines[i]);
			int offX = 0;
			switch(hAlign) {
				case Left: offX = 0; break;
				case Center: offX = alignWidth / 2 - textWidth / 2; break;
				case Right: offX = alignWidth - textWidth; break;
			}
			drawString(mc.fontRenderer, lines[i], posX + offX, posY + i * (mc.fontRenderer.FONT_HEIGHT + LINE_SPACING), color);
		}
	}

}
