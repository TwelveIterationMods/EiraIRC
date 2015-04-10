package net.blay09.mods.eirairc.client.gui.screenshot;

import net.blay09.mods.eirairc.api.event.ScreenshotUploadEvent;
import net.blay09.mods.eirairc.client.gui.EiraGui;
import net.blay09.mods.eirairc.client.gui.EiraGuiScreen;
import net.blay09.mods.eirairc.client.gui.base.GuiAdvancedTextField;
import net.blay09.mods.eirairc.client.gui.base.GuiImageButton;
import net.blay09.mods.eirairc.client.gui.base.image.GuiFileImage;
import net.blay09.mods.eirairc.client.gui.base.image.GuiImage;
import net.blay09.mods.eirairc.client.gui.overlay.OverlayYesNo;
import net.blay09.mods.eirairc.client.screenshot.Screenshot;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.config.ScreenshotAction;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	private GuiScreenshotPage activePageButton;
	private int currentIdx;
	private String lastSearchText = "";
	private final List<Screenshot> searchResults = new ArrayList<Screenshot>();
	private Screenshot currentScreenshot;
	private GuiImage imgPreview;

	private int imgX;
	private int imgY;

	private boolean isUploading;
	private boolean buttonsVisible;
	private float hoverTime;
	private GuiImageButton hoverObject;

	public GuiScreenshots(GuiScreen parentScreen) {
		super(parentScreen);
		screenshotGroup = ScreenshotManager.getInstance().getScreenshots();
		screenshotList = screenshotGroup;
	}

	private void setScreenshotGroup(GuiScreenshotPage pageButton, List<Screenshot> screenshotGroup) {
		if(activePageButton != null) {
			activePageButton.setActive(false);
		}
		pageButton.setActive(true);
		activePageButton = pageButton;
		this.screenshotGroup = screenshotGroup;
		setScreenshotList(screenshotGroup);
		resetSearch();
	}

	public void setScreenshotList(List<Screenshot> screenshotList) {
		this.screenshotList = screenshotList;
		currentIdx = 0;
		updateScreenshot();
	}

	public void updateScreenshot() {
		if(screenshotList.isEmpty()) {
			currentScreenshot = null;
			if(imgPreview != null) {
				imgPreview.dispose();
			}
			imgPreview = null;
		} else if(currentIdx >= 0 && currentIdx < screenshotList.size()) {
			Screenshot screenshot = screenshotList.get(currentIdx);
			if(currentScreenshot != screenshot) {
				if(imgPreview != null) {
					imgPreview.dispose();
				}
				imgPreview = new GuiFileImage(screenshot.getFile());
				imgPreview.loadTexture();
				currentScreenshot = screenshot;
			}
			txtName.setDefaultText(screenshot.getOriginalName(), false);
			txtName.setText(screenshot.getName());
			if(screenshot.isUploaded()) {
				btnUpload.visible = false;
				btnClipboard.visible = true;
				btnReupload.visible = true;
			} else {
				btnUpload.visible = true;
				btnClipboard.visible = false;
				btnReupload.visible = false;
			}
			setFavoriteButtonState(screenshot.isFavorited());
		}
	}

	@Override
	public void initGui() {
		super.initGui();

		MinecraftForge.EVENT_BUS.register(this);

		final int leftX = width / 2 - 145;
		final int rightX = width / 2 + 145;
		final int topY = height / 2 - 80;

		txtSearch = new GuiAdvancedTextField(0, fontRendererObj, leftX + 2, topY - 10, 200, 16);
		txtSearch.setEmptyOnRightClick(true);
		txtSearch.setDefaultText("Search...", true);
		textFieldList.add(txtSearch);

		btnOpenFolder = new GuiButton(0, rightX - 85, topY - 12, 85, 20, "Open Folder");
		buttonList.add(btnOpenFolder);

		txtName = new GuiAdvancedTextField(1, fontRendererObj, width / 2 - 100, topY + 152, 200, 15);
		textFieldList.add(txtName);

		btnGoToFirst = new GuiImageButton(1, width / 2 - 39, topY + 12, EiraGui.atlas.findRegion("button_first"));
		buttonList.add(btnGoToFirst);

		btnGoToPrevious = new GuiImageButton(2, width / 2 - 13, topY + 12, EiraGui.atlas.findRegion("button_prev"));
		buttonList.add(btnGoToPrevious);

		btnGoToNext = new GuiImageButton(3, width / 2 + 5, topY + 12, EiraGui.atlas.findRegion("button_next"));
		buttonList.add(btnGoToNext);

		btnGoToLast = new GuiImageButton(4, width / 2 + 23, topY + 12, EiraGui.atlas.findRegion("button_last"));
		buttonList.add(btnGoToLast);

		btnFavorite = new GuiImageButton(5, rightX - 37, topY + 12, EiraGui.atlas.findRegion("button_favorite"));
		btnFavorite.setTooltipText("Favorite");
		buttonList.add(btnFavorite);

		btnUpload = new GuiImageButton(6, rightX - 37, topY + 50, EiraGui.atlas.findRegion("button_upload"));
		btnUpload.setTooltipText("Upload");
		buttonList.add(btnUpload);

		btnClipboard = new GuiImageButton(7, rightX - 37, topY + 50, EiraGui.atlas.findRegion("button_clipboard"));
		btnClipboard.visible = false;
		btnClipboard.setTooltipText("To Clipboard");
		buttonList.add(btnClipboard);

		btnZoom = new GuiImageButton(8, rightX - 37, topY + 135, EiraGui.atlas.findRegion("button_zoom"));
		btnZoom.setTooltipText("Zoom");
		buttonList.add(btnZoom);

		btnDelete = new GuiImageButton(9, leftX + 5, topY + 12, EiraGui.atlas.findRegion("button_delete"));
		btnDelete.setTooltipText("Delete");
		buttonList.add(btnDelete);

		btnReupload = new GuiImageButton(10, leftX + 5, topY + 50, EiraGui.atlas.findRegion("button_upload"));
		btnReupload.visible = false;
		btnReupload.setTooltipText("Re-Upload");
		buttonList.add(btnReupload);

		int pageLeft = rightX - 3;
		int pageTop = topY + 10;

		GuiScreenshotPage pageAll = new GuiScreenshotPage(11, pageLeft, pageTop, "All") {
			@Override
			public void onClick() {
				setScreenshotGroup(this, ScreenshotManager.getInstance().getScreenshots());
			}
		};
		pageAll.setActive(true);
		activePageButton = pageAll;
		buttonList.add(pageAll);
		pageTop += pageAll.height;

		GuiScreenshotPage pageFavorited = new GuiScreenshotPage(11, pageLeft, pageTop, "Favorited") {
			@Override
			public void onClick() {
				List<Screenshot> groupList = new ArrayList<Screenshot>();
				for(Screenshot screenshot : ScreenshotManager.getInstance().getScreenshots()) {
					if(screenshot.isFavorited()) {
						groupList.add(screenshot);
					}
				}
				setScreenshotGroup(this, groupList);
			}
		};
		buttonList.add(pageFavorited);
		pageTop += pageFavorited.height + 3;

		GuiScreenshotPage pageTimestamp = new GuiScreenshotPage(11, pageLeft, pageTop, "Today") {
			@Override
			public void onClick() {
				List<Screenshot> groupList = new ArrayList<Screenshot>();
				long now = System.currentTimeMillis();
				for(Screenshot screenshot : ScreenshotManager.getInstance().getScreenshots()) {
					long diff = now - screenshot.getTimeStamp();
					if(diff <= 86400000L) {
						groupList.add(screenshot);
					}
				}
				setScreenshotGroup(this, groupList);
			}
		};
		buttonList.add(pageTimestamp);
		pageTop += pageTimestamp.height;

		pageTimestamp = new GuiScreenshotPage(11, pageLeft, pageTop, "This Week") {
			@Override
			public void onClick() {
				List<Screenshot> groupList = new ArrayList<Screenshot>();
				long now = System.currentTimeMillis();
				for(Screenshot screenshot : ScreenshotManager.getInstance().getScreenshots()) {
					long diff = now - screenshot.getTimeStamp();
					if(diff > 86400000L && diff <= 86400000L * 7) {
						groupList.add(screenshot);
					}
				}
				setScreenshotGroup(this, groupList);
			}
		};
		buttonList.add(pageTimestamp);
		pageTop += pageTimestamp.height;

		pageTimestamp = new GuiScreenshotPage(11, pageLeft, pageTop, "This Month") {
			@Override
			public void onClick() {
				List<Screenshot> groupList = new ArrayList<Screenshot>();
				long now = System.currentTimeMillis();
				for(Screenshot screenshot : ScreenshotManager.getInstance().getScreenshots()) {
					long diff = now - screenshot.getTimeStamp();
					if(diff > 86400000L * 7 && diff <= 86400000L * 7 * 4) {
						groupList.add(screenshot);
					}
				}
				setScreenshotGroup(this, groupList);
			}
		};
		buttonList.add(pageTimestamp);
		pageTop += pageTimestamp.height;

		pageTimestamp = new GuiScreenshotPage(11, pageLeft, pageTop, "This Year") {
			@Override
			public void onClick() {
				List<Screenshot> groupList = new ArrayList<Screenshot>();
				long now = System.currentTimeMillis();
				for(Screenshot screenshot : ScreenshotManager.getInstance().getScreenshots()) {
					long diff = now - screenshot.getTimeStamp();
					if(diff > 86400000L * 7 * 4 && diff <= 86400000L * 7 * 4 * 12) {
						groupList.add(screenshot);
					}
				}
				setScreenshotGroup(this, groupList);
			}
		};
		buttonList.add(pageTimestamp);
		pageTop += pageTimestamp.height;

		pageTimestamp = new GuiScreenshotPage(11, pageLeft, pageTop, "Older") {
			@Override
			public void onClick() {
				List<Screenshot> groupList = new ArrayList<Screenshot>();
				long now = System.currentTimeMillis();
				for(Screenshot screenshot : ScreenshotManager.getInstance().getScreenshots()) {
					long diff = now - screenshot.getTimeStamp();
					if(diff > 86400000L * 7 * 4 * 12) {
						groupList.add(screenshot);
					}
				}
				setScreenshotGroup(this, groupList);
			}
		};
		buttonList.add(pageTimestamp);

		updateScreenshot();

		imgX = leftX + 2;
		imgY = topY + 10;
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		MinecraftForge.EVENT_BUS.unregister(this);
		ScreenshotManager.getInstance().save();
	}

	@Override
	public boolean isClickClosePosition(int mouseX, int mouseY) {
		return (mouseX < menuX);
	}

	private void resetSearch() {
		lastSearchText = "";
		txtSearch.setText("");
		searchResults.clear();
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnGoToFirst) {
			if(!isUploading) {
				currentIdx = 0;
				updateScreenshot();
			}
		} else if(button == btnGoToPrevious) {
			if(currentIdx > 0 && !isUploading) {
				currentIdx--;
				updateScreenshot();
			}
		} else if(button == btnGoToNext) {
			if(currentIdx < screenshotList.size() - 1 && !isUploading) {
				currentIdx++;
				updateScreenshot();
			}
		} else if(button == btnGoToLast) {
			if(!isUploading) {
				currentIdx = screenshotList.size() - 1;
				updateScreenshot();
			}
		} else if(button == btnOpenFolder) {
			Utils.openDirectory(new File(mc.mcDataDir, "screenshots"));
		} else if(button == btnDelete) {
			if(currentScreenshot != null && !isUploading) {
				setOverlay(new OverlayYesNo(this, "Do you really want to delete this screenshot?", "This can't be undone, so be careful!", currentIdx));
			}
		} else if(button == btnClipboard) {
			if(currentScreenshot != null) {
				Utils.setClipboardString(currentScreenshot.getUploadURL());
			}
		} else if(button == btnUpload) {
			if(currentScreenshot != null) {
				btnUpload.enabled = false;
				isUploading = true;
				ScreenshotManager.getInstance().uploadScreenshot(currentScreenshot, ScreenshotAction.None);
			}
		} else if(button == btnReupload) {
			if(currentScreenshot != null) {
				btnReupload.enabled = false;
				isUploading = true;
				ScreenshotManager.getInstance().uploadScreenshot(currentScreenshot, ScreenshotAction.None);
			}
		} else if(button == btnFavorite) {
			if(currentScreenshot != null) {
				currentScreenshot.setFavorited(!currentScreenshot.isFavorited());
				setFavoriteButtonState(currentScreenshot.isFavorited());
			}
		} else if(button == btnZoom) {
			if(!isUploading) {
				mc.displayGuiScreen(new GuiScreenshotBigPreview(this, imgPreview));
			}
		}
	}

	@SubscribeEvent
	public void onScreenshotUploaded(ScreenshotUploadEvent event) {
		if(isUploading) {
			btnUpload.enabled = true;
			btnReupload.enabled = true;
			isUploading = false;
			updateScreenshot();
		}
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		if(result) {
			ScreenshotManager.getInstance().deleteScreenshot(currentScreenshot, false);
			searchResults.remove(currentScreenshot);
			if(currentIdx >= screenshotList.size()) {
				currentIdx = Math.max(0, currentIdx - 1);
			}
			updateScreenshot();
		}
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
		int imgWidth = 285;
		int imgHeight = 160;
		if(imgPreview != null && mouseX >= imgX && mouseX < imgX + imgWidth && mouseY >= imgY && mouseY < imgY + imgHeight) {
			if(!buttonsVisible) {
				for(Object entry : buttonList) {
					GuiButton button = (GuiButton) entry;
					if (button instanceof GuiImageButton) {
						((GuiImageButton) button).setFadeMode(1);
					}
				}
				buttonsVisible = true;
			}
		} else {
			if (buttonsVisible) {
				for (Object entry : buttonList) {
					GuiButton button = (GuiButton) entry;
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

		for (Object entry : buttonList) {
			GuiButton button = (GuiButton) entry;
			if (button instanceof GuiImageButton) {
				GuiImageButton imageButton = (GuiImageButton) button;
				if (imageButton.isInside(mouseX, mouseY) && imageButton.isAlphaVisible() && imageButton.getTooltipText() != null) {
					if (imageButton != hoverObject) {
						hoverObject = imageButton;
						hoverTime = 0f;
					}
					hoverTime++;
					if (hoverTime > TOOLTIP_TIME) {
						tooltipList.clear();
						tooltipList.add(imageButton.getTooltipText());
						drawTooltip(tooltipList, mouseX, mouseY);
					}
					break;
				}
			}
		}

		if(isUploading) {
			drawCenteredString(fontRendererObj, "Uploading...", width / 2, height / 2, Globals.TEXT_COLOR);
		}
	}

	public void setFavoriteButtonState(boolean state) {
		if(state) {
			btnFavorite.setTextureRegion(EiraGui.atlas.findRegion("button_favorite"));
		} else {
			btnFavorite.setTextureRegion(EiraGui.atlas.findRegion("button_unfavorite"));
		}
	}
}
