package net.blay09.mods.eirairc.client.gui.screenshot;

import net.blay09.mods.eirairc.client.graphics.TextureRegion;
import net.blay09.mods.eirairc.client.gui.EiraGui;
import net.blay09.mods.eirairc.util.Globals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

public abstract class GuiScreenshotPage extends GuiButton {

	private final TextureRegion regionContent;
	private final TextureRegion regionRight;

	private boolean active;

	public GuiScreenshotPage(int id, int x, int y, String displayString) {
		super(id, x, y, displayString);
		regionContent = EiraGui.atlas.findRegion("tab_bg_content");
		regionRight = EiraGui.atlas.findRegion("tab_bg_topright");

		width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(displayString) + regionRight.getRegionWidth();
		height = regionContent.getRegionHeight();
	}

	public abstract void onClick();

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if(enabled && visible && mouseX >= xPosition && mouseX < xPosition + width - 8 && mouseY >= yPosition && mouseY < yPosition + height) {
			onClick();
		}
		return false;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		boolean hovered = false;
		if(mouseX >= xPosition && mouseX < xPosition + width - 8 && mouseY >= yPosition && mouseY < yPosition + height) {
			hovered = true;
		}
		if(active) {
			GL11.glColor4f(1f, 1f, 1f, 1f);
		} else {
			GL11.glColor4f(0f, 0f, 0f, 1f);
		}
		GL11.glEnable(GL11.GL_BLEND);
		regionContent.draw(xPosition, yPosition, width - regionRight.getRegionWidth(), regionContent.getRegionHeight());
		regionRight.draw(xPosition + width - regionRight.getRegionWidth(), yPosition);
		GL11.glDisable(GL11.GL_BLEND);
		drawString(mc.fontRendererObj, displayString, xPosition + regionRight.getRegionWidth() / 2, yPosition + (height - 8) / 2, hovered ? -12345678 : Globals.TEXT_COLOR);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
