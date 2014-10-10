package net.blay09.mods.eirairc.client.gui.screenshot;

import net.blay09.mods.eirairc.client.gui.EiraGuiScreen;
import net.blay09.mods.eirairc.client.gui.base.image.GuiImage;
import net.blay09.mods.eirairc.util.Globals;
import net.minecraft.client.gui.GuiScreen;

/**
 * Created by Blay09 on 10.10.2014.
 */
public class GuiScreenshotBigPreview extends EiraGuiScreen {

	private final GuiImage image;

	public GuiScreenshotBigPreview(GuiScreen parentScreen, GuiImage image) {
		super(parentScreen);
		this.image = image;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		gotoPrevious();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		super.drawScreen(mouseX, mouseY, p_73863_3_);

		image.draw(0, 0, width, height, zLevel);

		String s = "Click anywhere to go back.";
		drawString(fontRendererObj, s, width - fontRendererObj.getStringWidth(s) - 5, height - fontRendererObj.FONT_HEIGHT - 5, Globals.TEXT_COLOR);
	}
}
