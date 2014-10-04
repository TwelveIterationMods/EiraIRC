package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.client.gui.base.GuiMenuButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class EiraGuiScreen extends GuiScreen {

	private static final ResourceLocation menuBG = new ResourceLocation("eirairc", "gfx/menubg.png");

	protected final GuiScreen parentScreen;
	protected final List<GuiMenuButton> menuButtonList = new ArrayList<GuiMenuButton>();
	protected final List<GuiTextField> textFieldList = new ArrayList<GuiTextField>();
	protected final List<GuiLabel> labelList = new ArrayList<GuiLabel>();

	protected int menuX;
	protected int menuY;
	protected int menuWidth;
	protected int menuHeight;

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

		menuButtonList.clear();
		textFieldList.clear();
		labelList.clear();
	}

	public void setupMenuSize(int menuWidth, int menuHeight) {
		this.menuWidth = menuWidth;
		this.menuHeight = menuHeight;
		menuX = width / 2 - menuWidth / 2;
		menuY = height / 2 - menuHeight / 2;
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



		if(mouseX >= menuX && mouseX < menuX + menuWidth && mouseY >= menuY && mouseY < menuY + menuHeight) {
			super.mouseClicked(mouseX, mouseY, mouseButton);
		} else {
			mc.displayGuiScreen(parentScreen);
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

	public void actionPerformed (GuiMenuButton menuButton){}

	@Override
	public void drawScreen ( int mouseX, int mouseY, float p_73863_3_){
		super.drawScreen(mouseX, mouseY, p_73863_3_);

		for(int i = 0; i < labelList.size(); i++) {
			labelList.get(i).drawLabel();
		}

		for(int i = 0; i < textFieldList.size(); i++) {
			textFieldList.get(i).drawTextBox();
		}

		for(int i = 0; i < menuButtonList.size(); i++) {
			menuButtonList.get(i).draw(mouseX, mouseY);
		}
	}

	public void drawLightBackground(int x, int y, int width, int height) {
		mc.renderEngine.bindTexture(menuBG);
		drawTexturedRect(x, y, width, height, 0, 0, 300, 200, 300, 200);
	}

	public void drawTexturedRect(int x, int y, int width, int height, int texCoordX, int texCoordY, int regionWidth, int regionHeight, int texWidth, int texHeight) {
		float u = (float) texCoordX / (float) texWidth;
		float v = (float) texCoordY / (float) texHeight;
		float u2 = (float) (texCoordX + regionWidth) / (float) texWidth;
		float v2 = (float) (texCoordY + regionHeight) / (float) texHeight;

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x, y + height, this.zLevel, u, v2);
		tessellator.addVertexWithUV(x + width, y + height, this.zLevel, u2, v2);
		tessellator.addVertexWithUV(x + width, y, this.zLevel, u2, v);
		tessellator.addVertexWithUV(x, y, this.zLevel, u, v);
		tessellator.draw();
	}

}