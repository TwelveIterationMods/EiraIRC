// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.client.gui.screenshot;

import com.google.common.collect.Lists;
import net.blay09.mods.eirairc.client.gui.EiraGui;
import net.blay09.mods.eirairc.client.gui.EiraGuiScreen;
import net.blay09.mods.eirairc.client.gui.base.GuiImageButton;
import net.blay09.mods.eirairc.client.gui.base.image.GuiImage;
import net.blay09.mods.eirairc.client.gui.base.image.GuiURLImage;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.I19n;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiYesNoCallback;
import org.lwjgl.opengl.GL11;

import java.net.URL;
import java.util.List;

public class GuiImagePreview extends EiraGuiScreen implements GuiYesNoCallback {

	private static final float TOOLTIP_TIME = 30;

	private final URL url;
	private final URL directURL;

	private GuiImage imgPreview;
	private GuiImageButton btnZoom;
	private GuiImageButton btnClipboard;
	private GuiImageButton btnGoToURL;

	private int imgX;
	private int imgY;

	private boolean buttonsVisible;
	private float hoverTime;
	private GuiImageButton hoverObject;

	public GuiImagePreview(URL directURL, URL url) {
		super(null);
		this.url = url != null ? url : directURL;
		this.directURL = directURL;

		imgPreview = new GuiURLImage(directURL);
		imgPreview.loadTexture();
	}

	@Override
	public void initGui() {
		super.initGui();

		final int leftX = width / 2 - 145;
		final int rightX = width / 2 + 145;
		final int topY = height / 2 - 100;

		btnZoom = new GuiImageButton(0, rightX - 37, topY + 12, EiraGui.atlas.findRegion("button_zoom"));
		btnZoom.setTooltipText(I19n.format("eirairc:gui.image.fullscreen"));
		buttonList.add(btnZoom);

		btnGoToURL = new GuiImageButton(1, rightX - 37, topY + 50, EiraGui.atlas.findRegion("button_upload"));
		btnGoToURL.setTooltipText(I19n.format("eirairc:gui.image.openBrowser"));
		buttonList.add(btnGoToURL);

		btnClipboard = new GuiImageButton(2, rightX - 37, topY + 88, EiraGui.atlas.findRegion("button_clipboard"));
		btnClipboard.setTooltipText(I19n.format("eirairc:gui.image.toClipboard"));
		buttonList.add(btnClipboard);

		imgX = leftX + 2;
		imgY = topY + 10;
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		imgPreview.dispose();
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnClipboard) {
			Utils.setClipboardString(url.toString());
		} else if(button == btnZoom) {
			mc.displayGuiScreen(new GuiScreenshotBigPreview(this, directURL));
		} else if(button == btnGoToURL) {
			mc.displayGuiScreen(new GuiConfirmOpenLink(this, url.toString(), 0, true));
		}
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		if(result) {
			Utils.openWebpage(url);
			Minecraft.getMinecraft().displayGuiScreen(null);
		} else {
			Minecraft.getMinecraft().displayGuiScreen(this);
		}
	}

	private static final List<String> tooltipList = Lists.newArrayList();
	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		drawLightBackground(menuX, menuY, menuWidth, menuHeight);

		// Fade all image buttons in/out on hover of image
		int imgWidth = 285;
		int imgHeight = 180;
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
				for(Object entry : buttonList) {
					GuiButton button = (GuiButton) entry;
					if (button instanceof GuiImageButton) {
						((GuiImageButton) button).setFadeMode(-1);
					}
				}
				buttonsVisible = false;
			}
		}

		if(imgPreview != null) {
			if(!imgPreview.isLoaded()) {
				String s = I19n.format("eirairc:gui.image.loading");
				mc.fontRenderer.drawStringWithShadow(s, width / 2 - fontRendererObj.getStringWidth(s) / 2, height / 2 - fontRendererObj.FONT_HEIGHT / 2, Globals.TEXT_COLOR);
			}

			// Render the preview image
			imgPreview.draw(imgX, imgY, imgWidth, imgHeight, zLevel);
		}

		GL11.glEnable(GL11.GL_BLEND);
		super.drawScreen(mouseX, mouseY, par3);
		GL11.glDisable(GL11.GL_BLEND);

		for(Object entry : buttonList) {
			GuiButton button = (GuiButton) entry;
			if (button instanceof GuiImageButton) {
				GuiImageButton imageButton = (GuiImageButton) button;
				if (imageButton.isInside(mouseX, mouseY) && imageButton.visible && imageButton.isAlphaVisible() && imageButton.getTooltipText() != null) {
					if (imageButton != hoverObject) {
						hoverObject = imageButton;
						hoverTime = 0f;
					}
					hoverTime++;
					if (hoverTime > TOOLTIP_TIME) {
						tooltipList.clear();
						tooltipList.add(imageButton.getTooltipText());
						func_146283_a(tooltipList, mouseX, mouseY); // drawHoveringText
					}
					break;
				}
			}
		}
	}

}
