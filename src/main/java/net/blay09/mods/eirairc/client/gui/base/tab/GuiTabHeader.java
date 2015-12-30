package net.blay09.mods.eirairc.client.gui.base.tab;

import net.blay09.mods.eirairc.client.graphics.TextureRegion;
import net.blay09.mods.eirairc.client.gui.EiraGui;
import net.blay09.mods.eirairc.util.Globals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;


public class GuiTabHeader extends Gui {

	private Minecraft mc;
	public final GuiTabPage tabPage;
	public final int x;
	public final int y;
	public final int width;
	public final int height;
	public final TextureRegion regionLeft;
	public final TextureRegion regionMiddle;
	public final TextureRegion regionRight;

	public GuiTabHeader(GuiTabPage tabPage, int x, int y, int width, int height) {
		this.mc = Minecraft.getMinecraft();
		this.tabPage = tabPage;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		regionLeft = EiraGui.atlas.findRegion("tab_header_left");
		regionMiddle = EiraGui.atlas.findRegion("tab_header_middle");
		regionRight = EiraGui.atlas.findRegion("tab_header_right");
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
		regionLeft.draw(x, y);
		regionMiddle.draw(x + regionLeft.getRegionWidth(), y, width - regionLeft.getRegionWidth() - regionRight.getRegionWidth(), regionLeft.getRegionHeight());
		regionRight.draw(x + width - regionRight.getRegionWidth(), y);
		GL11.glDisable(GL11.GL_BLEND);

		drawString(mc.fontRendererObj, tabPage.getTitle(), x + 8, y + 8 - mc.fontRendererObj.FONT_HEIGHT / 2, hovered ? -12345678 : Globals.TEXT_COLOR);
	}
}
