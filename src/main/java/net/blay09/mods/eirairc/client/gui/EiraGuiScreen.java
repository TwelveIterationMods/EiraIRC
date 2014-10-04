package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.client.gui.base.MenuButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class EiraGuiScreen extends GuiScreen {

	protected final List<MenuButton> menuButtonList = new ArrayList<MenuButton>();
	protected final List<GuiTextField> textFieldList = new ArrayList<GuiTextField>();

	@Override
	public void initGui() {
		super.initGui();

		menuButtonList.clear();
		textFieldList.clear();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		for(int i = 0; i < menuButtonList.size(); i++) {
			menuButtonList.get(i).mouseClicked(mouseX, mouseY, mouseButton);
		}
		for(int i = 0; i < textFieldList.size(); i++) {
			textFieldList.get(i).mouseClicked(mouseX, mouseY, mouseButton);
		}
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

		for(int i = 0; i < textFieldList.size(); i++) {
			textFieldList.get(i).updateCursorCounter();
		}
	}

	public void actionPerformed (MenuButton menuButton){
	}


	@Override
	public void drawScreen ( int mouseX, int mouseY, float p_73863_3_){
		super.drawScreen(mouseX, mouseY, p_73863_3_);

		for(int i = 0; i < menuButtonList.size(); i++) {
			menuButtonList.get(i).draw(mouseX, mouseY);
		}

		for(int i = 0; i < textFieldList.size(); i++) {
			textFieldList.get(i).drawTextBox();
		}
	}

	public void drawLightBackground(int x, int y, int width, int height) {
		drawGradientRect(x, y, x + width, y + height, -16509940, -535818224);
	}

}