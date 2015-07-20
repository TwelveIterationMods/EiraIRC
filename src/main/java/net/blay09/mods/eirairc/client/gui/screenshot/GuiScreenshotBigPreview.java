// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.client.gui.screenshot;

import net.blay09.mods.eirairc.client.gui.EiraGuiScreen;
import net.blay09.mods.eirairc.client.gui.base.image.GuiFileImage;
import net.blay09.mods.eirairc.client.gui.base.image.GuiImage;
import net.blay09.mods.eirairc.client.gui.base.image.GuiURLImage;
import net.blay09.mods.eirairc.client.screenshot.Screenshot;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.I19n;
import net.minecraft.client.gui.GuiScreen;

import java.net.URL;

public class GuiScreenshotBigPreview extends EiraGuiScreen {

	private final GuiImage image;

	public GuiScreenshotBigPreview(GuiScreen parentScreen, URL url) {
		super(parentScreen);
		image = new GuiURLImage(url);
		image.loadTexture();
	}

	public GuiScreenshotBigPreview(GuiScreen parentScreen, Screenshot screenshot) {
		super(parentScreen);
		image = new GuiFileImage(screenshot.getFile());
		image.loadTexture();
	}

	@Override
	public boolean mouseClick(int mouseX, int mouseY, int mouseButton) {
		gotoPrevious();
		return true;
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();

		image.dispose();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		super.drawScreen(mouseX, mouseY, p_73863_3_);

		image.draw(0, 0, width, height, zLevel);

		String s = I19n.format("eirairc:gui.image.clickToGoBack");
		drawString(fontRendererObj, s, width - fontRendererObj.getStringWidth(s) - 5, height - fontRendererObj.FONT_HEIGHT - 5, Globals.TEXT_COLOR);
	}
}
