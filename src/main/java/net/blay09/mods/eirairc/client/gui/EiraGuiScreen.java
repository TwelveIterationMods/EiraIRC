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

/**
 * Created by Blay09 on 04.10.2014.
 */
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
		if(overlay != null && overlay.mouseClick(mouseX, mouseY, mouseButton)) {
			return true;
		}
		if(controlClicked(mouseX, mouseY, mouseButton)) {
			return true;
		}
		return false;
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
					event.button.func_146113_a(mc.getSoundHandler());
					actionPerformed(event.button);
					if(this.equals(mc.currentScreen))
						MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.ActionPerformedEvent.Post(this, event.button, buttonList));
					return true;
				}
			}
		}
		for(int i = 0; i < textFieldList.size(); i++) {
			textFieldList.get(i).mouseClicked(mouseX, mouseY, mouseButton);
			if(textFieldList.get(i).isFocused()) {
				return true;
			}
		}
		for(int i = 0; i < listList.size(); i++) {
			if(listList.get(i).mouseClicked(mouseX, mouseY, mouseButton)) {
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
			for(int i = 0; i < listList.size(); i++) {
				listList.get(i).mouseWheelMoved(wheelDelta);
			}
		}
	}

	@Override
	protected void mouseMovedOrUp(int mouseX, int mouseY, int mouseButton) {
		if(selectedButton != null && mouseButton == 0) {
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

		for(int i = 0; i < textFieldList.size(); i++) {
			if(textFieldList.get(i).textboxKeyTyped(unicode, keyCode)) {
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

		for(int i = 0; i < textFieldList.size(); i++) {
			textFieldList.get(i).updateCursorCounter();
		}
	}

	@Override
	public void drawScreen (int mouseX, int mouseY, float p_73863_3_){
		super.drawScreen(mouseX, mouseY, p_73863_3_);

		for(int i = 0; i < labelList.size(); i++) {
			labelList.get(i).drawLabel();
		}


		for(int i = 0; i < textFieldList.size(); i++) {
			textFieldList.get(i).drawTextBox();
		}

		for(int i = 0; i < listList.size(); i++) {
			listList.get(i).drawList(mouseX, mouseY);
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
		func_146283_a(tooltipList, mouseX, mouseY);
	}
}