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

	private static final ResourceLocation tabHeader = new ResourceLocation("eirairc", "gfx/tab.png");

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
			GL11.glColor4f(0.5f, 0.5f, 0.5f, 1f);
		}
		GL11.glEnable(GL11.GL_BLEND);
		mc.renderEngine.bindTexture(tabHeader);
		EiraGui.drawTexturedRect(x, y, 16, 16, 0, 0, 16, 16, 256, 256);
		EiraGui.drawTexturedRect(x + 16, y, width - 32, 16, 16, 0, 16, 16, 256, 256);
		EiraGui.drawTexturedRect(x + width - 16, y, 16, 16, 32, 0, 16, 16, 256, 256);
		GL11.glDisable(GL11.GL_BLEND);

		drawString(mc.fontRenderer, tabPage.getTitle(), x + 8, y + 8 - mc.fontRenderer.FONT_HEIGHT / 2, hovered ? -12345678 : Globals.TEXT_COLOR);
	}
}
