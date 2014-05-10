// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.screenshot;

import net.blay09.mods.eirairc.client.screenshot.Screenshot;
import net.blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import net.blay09.mods.eirairc.config.ScreenshotConfig;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;

public class GuiScreenshotList extends GuiScreen {

	private final GuiScreen parentScreen;
	private GuiScreenshotSlot guiScreenshotSlot;
	private GuiButton btnUpload;
	private GuiButton btnClipboard;
	private GuiButton btnRename;
	private GuiButton btnDelete;
	private GuiButton btnBack;
	
	private Screenshot[] screenshots;
	private int selectedElement;
	
	public GuiScreenshotList(GuiScreen parentScreen) {
		this.parentScreen = parentScreen;
	}
	
	@Override
	public void initGui() {
		guiScreenshotSlot = new GuiScreenshotSlot(this);
		
		btnUpload = new GuiButton(1, width / 2 - 153, height - 50, 150, 20, Utils.getLocalizedMessage("irc.gui.screenshots.upload"));
		btnUpload.enabled = false;
		buttonList.add(btnUpload);
		
		btnClipboard = new GuiButton(2, width / 2 + 3, height - 50, 150, 20, Utils.getLocalizedMessage("irc.gui.screenshots.toClipboard"));
		btnClipboard.enabled = false;
		buttonList.add(btnClipboard);
		
		btnRename = new GuiButton(3, width / 2 - 126, height - 25, 80, 20, Utils.getLocalizedMessage("irc.gui.rename"));
		btnRename.enabled = false;
		buttonList.add(btnRename);
		
		btnDelete = new GuiButton(4, width / 2 - 40, height - 25, 80, 20, Utils.getLocalizedMessage("irc.gui.delete"));
		btnDelete.enabled = false;
		buttonList.add(btnDelete);
		
		btnBack = new GuiButton(0, width / 2 + 46, height - 25, 80, 20, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);
		
		selectedElement = -1;
		screenshots = ScreenshotManager.getInstance().getScreenshots().toArray(new Screenshot[ScreenshotManager.getInstance().getScreenshots().size()]);
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().displayGuiScreen(parentScreen);
		} else if(button == btnUpload) {
			ScreenshotManager.getInstance().uploadScreenshot(screenshots[selectedElement], ScreenshotConfig.VALUE_NONE);
		} else if(button == btnRename) {
			onElementClicked(selectedElement);
		} else if(button == btnDelete) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiYesNo(this, Utils.getLocalizedMessage("irc.gui.reallyRemove", "screenshot"), Utils.getLocalizedMessage("irc.gui.noUndo"), selectedElement));
		} else if(button == btnClipboard) {
			Utils.setClipboardString(screenshots[selectedElement].getUploadURL());
		}
	}
	
	@Override
	public void confirmClicked(boolean yup, int channelIdx) {
		if(yup) {
			ScreenshotManager.getInstance().deleteScreenshot(screenshots[selectedElement]);
		}
		Minecraft.getMinecraft().displayGuiScreen(this);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		guiScreenshotSlot.drawScreen(par1, par2, par3);
		drawCenteredString(fontRenderer, Utils.getLocalizedMessage("irc.gui.screenshotList"), width / 2, 20, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}

	public int size() {
		return screenshots.length;
	}

	public FontRenderer getFontRenderer() {
		return fontRenderer;
	}

	public boolean hasElementSelected() {
		return (selectedElement >= 0 && selectedElement < screenshots.length);
	}
	
	public void onElementClicked(int i) {
		// TODO show rename screenshot gui screen
	}
	
	public void onElementSelected(int i) {
		selectedElement = i;
		if(!hasElementSelected()) {
			return;
		}
		btnUpload.enabled = true;
		if(screenshots[selectedElement].isUploaded()) {
			btnUpload.displayString = "Re-Upload";
			btnClipboard.enabled = true;
		} else {
			btnUpload.displayString = "Upload";
			btnClipboard.enabled = false;
		}
		btnRename.enabled = true;
		btnDelete.enabled = true;
	}
	
	public int getSelectedElement() {
		return selectedElement;
	}

	public Screenshot getScreenshot(int i) {
		return screenshots[i];
	}
	
}
