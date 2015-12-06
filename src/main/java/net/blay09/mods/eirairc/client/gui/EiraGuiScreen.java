// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.client.gui.base.list.GuiList;
import net.blay09.mods.eirairc.client.gui.overlay.GuiOverlay;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class EiraGuiScreen extends GuiScreen {

	private static final ResourceLocation texMenuBackground = new ResourceLocation("eirairc", "gfx/menubg.png");

	protected final GuiScreen parentScreen;
	protected final List<GuiTextField> textFieldList = new ArrayList<GuiTextField>();
	protected final List<GuiLabel> labelList = new ArrayList<GuiLabel>();
	protected final List<GuiList> listList = new ArrayList<GuiList>();

	protected GuiOverlay overlay;
	protected int menuX;
	protected int menuY;
	protected int menuWidth;
	protected int menuHeight;
	protected boolean allowSideClickClose = true;
	private GuiButton selectedButton;

	public EiraGuiScreen() {
		this(null);
	}

	public EiraGuiScreen(GuiScreen parentScreen) {
		this.parentScreen = parentScreen;
	}

	@Override
	public void initGui() {
		super.initGui();

		setupMenuSize(300, 200);

		textFieldList.clear();
		labelList.clear();
		listList.clear();

		if(overlay != null) {
			overlay.setWorldAndResolution(mc, width, height);
		}
	}

	public void setupMenuSize(int menuWidth, int menuHeight) {
		this.menuWidth = menuWidth;
		this.menuHeight = menuHeight;
		menuX = width / 2 - menuWidth / 2;
		menuY = height / 2 - menuHeight / 2;
	}

	public boolean mouseClick(int mouseX, int mouseY, int mouseButton) {
		return overlay != null && overlay.mouseClick(mouseX, mouseY, mouseButton) || controlClicked(mouseX, mouseY, mouseButton);
	}

	public boolean controlClicked(int mouseX, int mouseY, int mouseButton) {
		if(mouseButton == 0) {
			for(int i = 0; i < buttonList.size(); i++) {
				GuiButton button = (GuiButton) buttonList.get(i);
				if(button.mousePressed(mc, mouseX, mouseY)) {
					GuiScreenEvent.ActionPerformedEvent.Pre event = new GuiScreenEvent.ActionPerformedEvent.Pre(this, button, buttonList);
					if(MinecraftForge.EVENT_BUS.post(event))
						break;
					selectedButton = event.button;
					event.button.playPressSound(mc.getSoundHandler());
					actionPerformed(event.button);
					if(this.equals(mc.currentScreen))
						MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.ActionPerformedEvent.Post(this, event.button, buttonList));
					return true;
				}
			}
		}
		boolean foundFocus = false;
		for(GuiTextField textField : textFieldList) {
			textField.mouseClicked(mouseX, mouseY, mouseButton);
			if (textField.isFocused()) {
				foundFocus = true;
			}
		}
		if(foundFocus) {
			return true;
		}
		for(GuiList list : listList) {
			if (list.mouseClicked(mouseX, mouseY, mouseButton)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int wheelDelta = Mouse.getEventDWheel();
		if(wheelDelta != 0) {
			for(GuiList list : listList) {
				list.mouseWheelMoved(wheelDelta);
			}
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if(selectedButton != null && state == 0) {
			selectedButton.mouseReleased(mouseX, mouseY);
			selectedButton = null;
		}
	}

	@Override
	protected final void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(mouseClick(mouseX, mouseY, mouseButton)) {
			return;
		}
		if(overlay == null && allowSideClickClose && isClickClosePosition(mouseX, mouseY)) {
			gotoPrevious();
		}
	}

	public boolean isClickClosePosition(int mouseX, int mouseY) {
		return (mouseX < menuX || mouseX >= menuX + menuWidth);
	}

	public void gotoPrevious() {
		mc.displayGuiScreen(parentScreen);
	}

	@Override
	public void keyTyped(char unicode, int keyCode) {
		super.keyTyped(unicode, keyCode);

		for(GuiTextField textField : textFieldList) {
			if (textField.textboxKeyTyped(unicode, keyCode)) {
				return;
			}
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if(overlay != null) {
			overlay.updateScreen();
		}

		for(GuiTextField textField : textFieldList) {
			textField.updateCursorCounter();
		}
	}

	@Override
	public void drawScreen (int mouseX, int mouseY, float p_73863_3_){
		super.drawScreen(mouseX, mouseY, p_73863_3_);

		for(GuiLabel label : labelList) {
			label.drawLabel();
		}

		for(GuiTextField textField : textFieldList) {
			textField.drawTextBox();
		}

		for(GuiList list : listList) {
			list.drawList(mouseX, mouseY);
		}

		if(overlay != null) {
			overlay.drawScreen(mouseX, mouseY, p_73863_3_);
		}
	}

	public void drawLightBackground(int x, int y, int width, int height) {
		mc.renderEngine.bindTexture(texMenuBackground);
		EiraGui.drawTexturedRect(x, y, width, height, 0, 0, 300, 200, zLevel, 300, 200);
	}

	public void setOverlay(GuiOverlay overlay) {
		this.overlay = overlay;
		if(overlay != null) {
			overlay.setWorldAndResolution(mc, width, height);
		}
	}

	public void drawTooltip(List<String> tooltipList, int mouseX, int mouseY) {
		drawHoveringText(tooltipList, mouseX, mouseY);
	}
}