package net.blay09.mods.eirairc.client.gui.screenshot;

import net.blay09.mods.eirairc.client.gui.EiraGui;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiScreenshots extends EiraGuiScreen implements GuiYesNoCallback {

	private static final float TOOLTIP_TIME = 30;

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
	private GuiImageButton btnReupload;
	private GuiImageButton btnFavorite;
	private GuiImageButton btnDelete;

	private List<Screenshot> screenshotList;
	private List<Screenshot> screenshotGroup;
	private int currentIdx;
	private String lastSearchText = "";
	private final List<Screenshot> searchResults = new ArrayList<Screenshot>();
	private Screenshot currentScreenshot;
	private GuiImage imgPreview;

	private int imgX;
	private int imgY;
	private final int imgWidth = 285;
	private final int imgHeight = 160;

	private boolean buttonsVisible;
	private float hoverTime;
	private GuiImageButton hoverObject;

	public GuiScreenshots(GuiScreen parentScreen) {
		super(parentScreen);
		screenshotGroup = ScreenshotManager.getInstance().getScreenshots();
		screenshotList = screenshotGroup;
	}

	public void setScreenshotList(List<Screenshot> screenshotList) {
		if(screenshotList.size() > 0) {
			this.screenshotList = screenshotList;
			currentIdx = 0;
			updateScreenshot();
		}
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
			setFavoriteButtonState(screenshot.isFavorited());
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

		btnGoToFirst = new GuiImageButton(1, width / 2 - 39, topY + 12, EiraGui.texMenu, 32, 192, 16, 16);
		buttonList.add(btnGoToFirst);

		btnGoToPrevious = new GuiImageButton(2, width / 2 - 13, topY + 12, EiraGui.texMenu, 32, 176, 8, 16);
		buttonList.add(btnGoToPrevious);

		btnGoToNext = new GuiImageButton(3, width / 2 + 5, topY + 12, EiraGui.texMenu, 40, 176, 8, 16);
		buttonList.add(btnGoToNext);

		btnGoToLast = new GuiImageButton(4, width / 2 + 23, topY + 12, EiraGui.texMenu, 48, 192, 16, 16);
		buttonList.add(btnGoToLast);

		btnFavorite = new GuiImageButton(5, rightX - 37, topY + 12, EiraGui.texMenu, 0, 208, 32, 32);
		btnFavorite.setTooltipText("Favorite");
		buttonList.add(btnFavorite);

		btnUpload = new GuiImageButton(6, rightX - 37, topY + 50, EiraGui.texMenu, 32, 208, 32, 32);
		btnUpload.setTooltipText("Upload");
		buttonList.add(btnUpload);

		btnClipboard = new GuiImageButton(7, rightX - 37, topY + 50, EiraGui.texMenu, 128, 208, 32, 32);
		btnClipboard.visible = false;
		btnClipboard.setTooltipText("To Clipboard");
		buttonList.add(btnClipboard);

		btnZoom = new GuiImageButton(8, rightX - 37, topY + 135, EiraGui.texMenu, 0, 176, 32, 32);
		btnZoom.setTooltipText("Zoom");
		buttonList.add(btnZoom);

		btnDelete = new GuiImageButton(9, leftX + 5, topY + 12, EiraGui.texMenu, 96, 208, 32, 32);
		btnDelete.setTooltipText("Delete");
		buttonList.add(btnDelete);

		btnReupload = new GuiImageButton(10, leftX + 5, topY + 50, EiraGui.texMenu, 32, 208, 32, 32);
		btnReupload.visible = false;
		btnReupload.setTooltipText("Re-Upload");
		buttonList.add(btnReupload);

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
			currentScreenshot.setFavorited(!currentScreenshot.isFavorited());
			setFavoriteButtonState(currentScreenshot.isFavorited());
		} else if(button == btnZoom) {
			mc.displayGuiScreen(new GuiScreenshotBigPreview(this, imgPreview));
		}
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		if(result) {
			ScreenshotManager.getInstance().deleteScreenshot(currentScreenshot, false);
		}
		Minecraft.getMinecraft().displayGuiScreen(this);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if(currentScreenshot != null) {
			currentScreenshot.setName(txtName.getTextOrDefault());
		}

		if(!lastSearchText.equals(txtSearch.getText())) {
			if(!txtSearch.getText().isEmpty()) {
				searchResults.clear();
				for(Screenshot screenshot : screenshotGroup) {
					if(screenshot.getName().contains(txtSearch.getText()) || screenshot.getOriginalName().contains(txtSearch.getText())) {
						searchResults.add(screenshot);
					}
				}
				setScreenshotList(searchResults);
			} else {
				setScreenshotList(screenshotGroup);
			}
			lastSearchText = txtSearch.getText();
		}
	}

	private static final List<String> tooltipList = new ArrayList<String>();
	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		drawLightBackground(menuX, menuY, menuWidth, menuHeight);

		// Fade all image buttons in/out on hover of image
		if(imgPreview != null && mouseX >= imgX && mouseX < imgX + imgWidth && mouseY >= imgY && mouseY < imgY + imgHeight) {
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
			if (buttonsVisible) {
				for (int i = 0; i < buttonList.size(); i++) {
					GuiButton button = (GuiButton) buttonList.get(i);
					if (button instanceof GuiImageButton) {
						((GuiImageButton) button).setFadeMode(-1);
					}
				}
				buttonsVisible = false;
			}
		}

		if(imgPreview != null) {
			// Render the screenshot preview image
			imgPreview.draw(imgX, imgY, imgWidth, imgHeight, zLevel);
		}

		super.drawScreen(mouseX, mouseY, par3);

		for (int i = 0; i < buttonList.size(); i++) {
			GuiButton button = (GuiButton) buttonList.get(i);
			if (button instanceof GuiImageButton) {
				GuiImageButton imageButton = (GuiImageButton) button;
				if(imageButton.isInside(mouseX, mouseY) && imageButton.isAlphaVisible() && imageButton.getTooltipText() != null) {
					if(imageButton != hoverObject) {
						hoverObject = imageButton;
						hoverTime = 0f;
					}
					hoverTime++;
					if(hoverTime > TOOLTIP_TIME) {
						tooltipList.clear();
						tooltipList.add(imageButton.getTooltipText());
						func_146283_a(tooltipList, mouseX, mouseY);
					}
					break;
				}
			}
		}
	}

	public void setFavoriteButtonState(boolean state) {
		if(state) {
			btnFavorite.setTextureRegion(0, 208, 32, 32);
		} else {
			btnFavorite.setTextureRegion(64, 208, 32, 32);
		}
	}
}
