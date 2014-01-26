// Copyright (c) 2013, Christopher "blay09" Baker
// All rights reserved.

package blay09.mods.eirairc.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import blay09.mods.eirairc.client.screenshot.Screenshot;
import blay09.mods.eirairc.client.screenshot.ScreenshotManager;
import blay09.mods.eirairc.util.Globals;
import blay09.mods.eirairc.util.Utils;

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
		
		btnUpload = new GuiButton(1, field_146294_l / 2 - 153, field_146295_m - 50, 150, 20, Utils.getLocalizedMessage("irc.gui.screenshots.upload"));
		btnUpload.field_146124_l = false;
		field_146292_n.add(btnUpload);
		
		btnClipboard = new GuiButton(2, field_146294_l / 2 + 3, field_146295_m - 50, 150, 20, Utils.getLocalizedMessage("irc.gui.screenshots.toClipboard"));
		btnClipboard.field_146124_l = false;
		field_146292_n.add(btnClipboard);
		
		btnRename = new GuiButton(3, field_146294_l / 2 - 126, field_146295_m - 25, 80, 20, Utils.getLocalizedMessage("irc.gui.rename"));
		btnRename.field_146124_l = false;
		field_146292_n.add(btnRename);
		
		btnDelete = new GuiButton(4, field_146294_l / 2 - 40, field_146295_m - 25, 80, 20, Utils.getLocalizedMessage("irc.gui.delete"));
		btnDelete.field_146124_l = false;
		field_146292_n.add(btnDelete);
		
		btnBack = new GuiButton(0, field_146294_l / 2 + 46, field_146295_m - 25, 80, 20, Utils.getLocalizedMessage("irc.gui.back"));
		field_146292_n.add(btnBack);
		
		selectedElement = -1;
		screenshots = ScreenshotManager.getInstance().getScreenshots().toArray(new Screenshot[ScreenshotManager.getInstance().getScreenshots().size()]);
	}
	
	@Override
	public void func_146284_a(GuiButton button) {
		if(button == btnBack) {
			Minecraft.getMinecraft().func_147108_a(parentScreen);
		} else if(button == btnUpload) {
			ScreenshotManager.getInstance().uploadScreenshot(screenshots[selectedElement]);
		} else if(button == btnRename) {
			onElementClicked(selectedElement);
		} else if(button == btnDelete) {
			Minecraft.getMinecraft().func_147108_a(new GuiYesNo(this, Utils.getLocalizedMessage("irc.gui.reallyRemove", "screenshot"), Utils.getLocalizedMessage("irc.gui.noUndo"), selectedElement));
		} else if(button == btnClipboard) {
			Utils.setClipboardString(screenshots[selectedElement].getUploadURL());
		}
	}
	
	@Override
	public void confirmClicked(boolean yup, int channelIdx) {
		if(yup) {
			ScreenshotManager.getInstance().deleteScreenshot(screenshots[selectedElement]);
		}
		Minecraft.getMinecraft().func_147108_a(this);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		guiScreenshotSlot.drawScreen(par1, par2, par3);
		drawCenteredString(field_146289_q, "irc.gui.screenshotList", field_146294_l / 2, 20, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
	}

	public int size() {
		return screenshots.length;
	}

	public FontRenderer getFontRenderer() {
		return field_146289_q;
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
		btnUpload.field_146124_l = true;
		if(screenshots[selectedElement].isUploaded()) {
			btnUpload.field_146126_j = "Re-Upload";
			btnClipboard.field_146124_l = true;
		} else {
			btnUpload.field_146126_j = "Upload";
			btnClipboard.field_146124_l = false;
		}
		btnRename.field_146124_l = true;
		btnDelete.field_146124_l = true;
	}
	
	public int getSelectedElement() {
		return selectedElement;
	}

	public Screenshot getScreenshot(int i) {
		return screenshots[i];
	}
	
}
