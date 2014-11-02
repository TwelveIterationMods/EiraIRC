package net.blay09.mods.eirairc.client.gui.screenshot;

import net.blay09.mods.eirairc.client.gui.EiraGuiScreen;
import net.blay09.mods.eirairc.client.gui.base.*;
import net.blay09.mods.eirairc.client.gui.base.image.GuiFileImage;
import net.blay09.mods.eirairc.client.gui.base.image.GuiImage;
import net.blay09.mods.eirairc.client.screenshot.Screenshot;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.config.ScreenshotAction;
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

	private GuiAdvancedTextField txtSearch;
	private GuiButton btnOpenFolder;
	private GuiAdvancedTextField txtName;

	private GuiImageButton btnGoToFirst;
	private GuiImageButton btnGoToPrevious;
	private GuiImageButton btnGoToNext;
	private GuiImageButton btnGoToLast;
	private GuiImageButton btnZoom;
	private GuiImageButton btnUpload;
	private GuiImageButton btnClipboard;
	private GuiImageButton btnFavorite;
	private GuiImageButton btnDelete;

	private boolean buttonsVisible;
	private int currentIdx;
	private Screenshot currentScreenshot;
	private GuiImage imgPreview;
	private int imgX;
	private int imgY;
	private final int imgWidth = 285;
	private final int imgHeight = 160;

	public GuiScreenshots(GuiScreen parentScreen) {
		super(parentScreen);
		screenshotList = ScreenshotManager.getInstance().getScreenshots(); // TODO will be set by the category tabs
	}

	public void updateScreenshot() {
		if(currentIdx >= 0 && currentIdx < screenshotList.size()) {
			Screenshot screenshot = screenshotList.get(currentIdx);
			if(currentScreenshot != screenshot) {
				imgPreview = new GuiFileImage(screenshot.getFile());
				imgPreview.loadTexture();
				currentScreenshot = screenshot;
			}
			txtName.setDefaultText(screenshot.getOriginalName(), false);
			txtName.setText(screenshot.getName());
		}
	}

	@Override
	public void initGui() {
		super.initGui();

		final int leftX = width / 2 - 145;
		final int rightX = width / 2 + 145;
		final int topY = height / 2 - 80;

		txtSearch = new GuiAdvancedTextField(fontRendererObj, leftX + 2, topY - 10, 200, 16);
		txtSearch.setEmptyOnRightClick(true);
		txtSearch.setDefaultText("Search...", true);
		textFieldList.add(txtSearch);

		btnOpenFolder = new GuiButton(0, rightX - 85, topY - 12, 85, 20, "Open Folder");
		buttonList.add(btnOpenFolder);

		txtName = new GuiAdvancedTextField(fontRendererObj, width / 2 - 100, topY + 152, 200, 15);
		textFieldList.add(txtName);

		// TODO initialize buttons
//		btnGoToFirst = new GuiImageButton();

		updateScreenshot();

		imgX = leftX + 2;
		imgY = topY + 10;
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnGoToFirst) {
			currentIdx = 0;
			updateScreenshot();
		} else if(button == btnGoToPrevious) {
			if(currentIdx > 0) {
				currentIdx--;
				updateScreenshot();
			}
		} else if(button == btnGoToNext) {
			if(currentIdx < screenshotList.size() - 1) {
				currentIdx++;
				updateScreenshot();
			}
		} else if(button == btnGoToLast) {
			currentIdx = screenshotList.size() - 1;
			updateScreenshot();
		} else if(button == btnOpenFolder) {
			Utils.openDirectory(new File(mc.mcDataDir, "screenshots"));
		} else if(button == btnDelete) {
			mc.displayGuiScreen(new GuiYesNo(this, "Do you really want to delete this screenshot?", "This can't be undone, so be careful!", currentIdx));
		} else if(button == btnClipboard) {
			Utils.setClipboardString(currentScreenshot.getUploadURL());
		} else if(button == btnUpload) {
			ScreenshotManager.getInstance().uploadScreenshot(currentScreenshot, ScreenshotAction.None);
		} else if(button == btnFavorite) {
			// TODO implement screenshot favorites
		} else if(button == btnZoom) {
			mc.displayGuiScreen(new GuiScreenshotBigPreview(this, imgPreview));
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
			// Fade all image buttons in/out on hover of image
			if(mouseX >= imgX && mouseX < imgX + imgWidth && mouseY >= imgY && mouseY < imgY + imgHeight) {
				if(!buttonsVisible) {
					for (int i = 0; i < buttonList.size(); i++) {
						GuiButton button = (GuiButton) buttonList.get(i);
						if (button instanceof GuiImageButton) {
							((GuiImageButton) button).setFadeMode(1);
						}
					}
					buttonsVisible = true;
				}
			} else {
				if(buttonsVisible) {
					for (int i = 0; i < buttonList.size(); i++) {
						GuiButton button = (GuiButton) buttonList.get(i);
						if (button instanceof GuiImageButton) {
							((GuiImageButton) button).setFadeMode(-1);
						}
					}
					buttonsVisible = false;
				}
			}

			// Render the screenshot preview image
			imgPreview.draw(imgX, imgY, imgWidth, imgHeight, zLevel);
		}

		super.drawScreen(mouseX, mouseY, par3);
	}

}
