package net.blay09.mods.eirairc.client.gui.screenshot;

import cpw.mods.fml.client.config.GuiUtils;
import net.blay09.mods.eirairc.client.gui.EiraGui;
import net.blay09.mods.eirairc.client.gui.EiraGuiScreen;
import net.blay09.mods.eirairc.client.gui.base.*;
import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.client.gui.base.image.GuiFileImage;
import net.blay09.mods.eirairc.client.gui.base.image.GuiImage;
import net.blay09.mods.eirairc.client.screenshot.Screenshot;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.config.ScreenshotAction;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;

import java.io.File;
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
	private Screenshot currentScreenshot;
	private GuiImage imgPreview;
	private int imgX;
	private int imgY;
	private final int imgWidth = 200;
	private final int imgHeight = 113;

	public GuiScreenshots(GuiScreen parentScreen) {
		super(parentScreen);
		screenshotList = ScreenshotManager.getInstance().getScreenshots();
	}

	public void updateScreenshot() {
		if(currentIdx >= 0 && currentIdx < screenshotList.size()) {
			Screenshot screenshot = screenshotList.get(currentIdx);
			if(currentScreenshot != screenshot) {
				imgPreview = new GuiFileImage(screenshot.getFile());
				imgPreview.loadTexture();
				currentScreenshot = screenshot;
			}
			txtName.setDefaultText(screenshot.getName(), false);
		}
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

		btnUpload = new GuiButton(1, rightX - 202, topY + 125, 100, 20, "Upload");
		buttonList.add(btnUpload);

		btnClipboard = new GuiButton(2, rightX - 100, topY + 125, 100, 20, "To Clipboard");
		btnClipboard.enabled = false;
		buttonList.add(btnClipboard);

		btnDelete = new GuiButton(3, rightX - 100, topY + 155, 100, 20, "Delete");
		buttonList.add(btnDelete);

		updateScreenshot();

		imgX = menuX + menuWidth - 207;
		imgY = menuY + 7;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		if(mouseX >= imgX && mouseX < imgX + imgWidth && mouseY >= imgY && mouseY < imgY + imgHeight) {
			mc.displayGuiScreen(new GuiScreenshotBigPreview(this, imgPreview));
		}
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnOpenFolder) {
			Utils.openDirectory(new File(mc.mcDataDir, "screenshots"));
		} else if(button == btnDelete) {
			mc.displayGuiScreen(new GuiYesNo(this, "Do you really want to delete this screenshot?", "This can't be undone, so be careful!", currentIdx));
		} else if(button == btnClipboard) {
			Utils.setClipboardString(currentScreenshot.getUploadURL());
		} else if(button == btnUpload) {
			ScreenshotManager.getInstance().uploadScreenshot(currentScreenshot, ScreenshotAction.None);
		}
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		if(result) {
			ScreenshotManager.getInstance().deleteScreenshot(currentScreenshot);
		}
		Minecraft.getMinecraft().displayGuiScreen(this);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		drawLightBackground(menuX, menuY, menuWidth, menuHeight);

		if(imgPreview != null) {
			imgPreview.draw(imgX, imgY, imgWidth, imgHeight, zLevel);

			if(mouseX >= imgX && mouseX < imgX + imgWidth && mouseY >= imgY && mouseY < imgY + imgHeight) {
				mc.renderEngine.bindTexture(EiraGui.tab);
				GuiUtils.drawTexturedModalRect(imgX + imgWidth - 32, imgY + imgHeight - 32, 0, 48, 32, 32, zLevel);
			}
		}

		super.drawScreen(mouseX, mouseY, par3);
	}

}
