package net.blay09.mods.eirairc.client.gui.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiLabel extends Gui {

	private final Minecraft mc;
	private final int posX;
	private final int posY;
	private final String text;
	private final int color;

	public GuiLabel(String text, int posX, int posY, int color) {
		this.mc = Minecraft.getMinecraft();
		this.text = text;
		this.posX = posX;
		this.posY = posY;
		this.color = color;
	}

	public void drawLabel() {
		drawString(mc.fontRenderer, text, posX, posY, color);
	}

}
