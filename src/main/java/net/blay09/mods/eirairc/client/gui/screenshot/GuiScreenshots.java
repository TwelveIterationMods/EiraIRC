package net.blay09.mods.eirairc.client.gui.screenshot;

import net.blay09.mods.eirairc.client.gui.EiraGuiScreen;
import net.blay09.mods.eirairc.client.gui.base.GuiAdvancedTextField;
import net.blay09.mods.eirairc.client.gui.base.image.GuiFileImage;
import net.blay09.mods.eirairc.client.gui.base.image.GuiImage;
import net.blay09.mods.eirairc.client.screenshot.Screenshot;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;

import java.util.List;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiScreenshots extends EiraGuiScreen implements GuiYesNoCallback {

	private final List<Screenshot> screenshotList;

	private GuiButton btnOpenFolder;
	private GuiAdvancedTextField txtName;
	private GuiButton btnUpload;
	private GuiButton btnClipboard;
	private GuiButton btnDelete;

	private int currentIdx;
	private GuiImage imgPreview;

	public GuiScreenshots(GuiScreen parentScreen) {
		super(parentScreen);

		screenshotList = ScreenshotManager.getInstance().getScreenshots();
		updateScreenshot();
	}

	public void updateScreenshot() {
		if(currentIdx >= 0 && currentIdx < screenshotList.size()) {
			Screenshot screenshot = screenshotList.get(currentIdx);
			imgPreview = new GuiFileImage(screenshot.getFile());
			imgPreview.loadTexture();
			txtName.setDefaultText(screenshot.getName(), false);
		}
	}

	public void prevScreenshot() {
		currentIdx--;
		if(currentIdx < 0) {
			currentIdx = screenshotList.size() - 1;
		}
		updateScreenshot();
	}

	public void nextScreenshot() {
		currentIdx++;
		if(currentIdx >= screenshotList.size()) {
			currentIdx = 0;
		}
		updateScreenshot();
	}

	@Override
	public void initGui() {
		super.initGui();

		final int leftX = width / 2 - 145;
		final int rightX = width / 2 + 145;
		final int topY = height / 2 - 80;

		btnOpenFolder = new GuiButton(0, leftX, topY + 155, 85, 20, "Open Folder");
		buttonList.add(btnOpenFolder);

		txtName = new GuiAdvancedTextField(fontRendererObj, rightX - 202, topY + 105, 200, 15);
		textFieldList.add(txtName);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		int imgX = menuX + menuWidth - 207;
		int imgY = menuY + 7;
		int imgW = 200;
		int imgH = 113;
		if(mouseX >= imgX && mouseX < imgX + imgW && mouseY >= imgY && mouseY < imgY + imgH) {
			// TODO open big preview
		}
	}

	@Override
	public void actionPerformed(GuiButton button) {
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		if(result) {

		}
		Minecraft.getMinecraft().displayGuiScreen(this);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		drawLightBackground(menuX, menuY, menuWidth, menuHeight);

		if(imgPreview != null) {
			imgPreview.draw(menuX + menuWidth - 207, menuY + 7, 200, 113, zLevel);
		}

		super.drawScreen(mouseX, mouseY, par3);
	}

}
