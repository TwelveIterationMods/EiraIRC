package net.blay09.mods.eirairc.client.gui.base.tab;

import net.blay09.mods.eirairc.client.gui.EiraGui;
import net.blay09.mods.eirairc.util.Globals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * Created by Blay09 on 05.10.2014.
 */
public class GuiTabHeader extends Gui {

	private Minecraft mc;
	public final GuiTabPage tabPage;
	public final int x;
	public final int y;
	public final int width;
	public final int height;

	public GuiTabHeader(GuiTabPage tabPage, int x, int y, int width, int height) {
		this.mc = Minecraft.getMinecraft();
		this.tabPage = tabPage;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void draw(int mouseX, int mouseY, boolean active) {
		boolean hovered = false;
		if(mouseX >= x && mouseX < x + width - 8 && mouseY >= y && mouseY < y + height) {
			hovered = true;
		}

		if(active) {
			GL11.glColor4f(1f, 1f, 1f, 1f);
		} else {
			GL11.glColor4f(0f, 0f, 0f, 1f);
		}
		GL11.glEnable(GL11.GL_BLEND);
		mc.renderEngine.bindTexture(EiraGui.texMenu);
		EiraGui.drawTexturedRect256(x, y, 16, 16, 0, 128, 16, 16, zLevel);
		EiraGui.drawTexturedRect256(x + 16, y, width - 32, 16, 16, 128, 16, 16, zLevel);
		EiraGui.drawTexturedRect256(x + width - 16, y, 16, 16, 32, 128, 16, 16, zLevel);
		GL11.glDisable(GL11.GL_BLEND);

		drawString(mc.fontRenderer, tabPage.getTitle(), x + 8, y + 8 - mc.fontRenderer.FONT_HEIGHT / 2, hovered ? -12345678 : Globals.TEXT_COLOR);
	}
}
