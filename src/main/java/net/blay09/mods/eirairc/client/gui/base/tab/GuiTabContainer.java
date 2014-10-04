package net.blay09.mods.eirairc.client.gui.base.tab;

import net.blay09.mods.eirairc.client.gui.EiraGuiScreen;
import net.blay09.mods.eirairc.util.Globals;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiTabContainer extends EiraGuiScreen {

	private static final ResourceLocation tabHeader = new ResourceLocation("eirairc", "gfx/tab.png");

	private final List<GuiTabHeader> headers = new ArrayList<GuiTabHeader>();
	protected final List<GuiTabPage> pages = new ArrayList<GuiTabPage>();

	protected GuiTabPage currentTab;
	protected int panelWidth;
	protected int panelHeight;

	public GuiTabContainer(GuiScreen parentScreen) {
		super(parentScreen);
	}

	@Override
	public void initGui() {
		super.initGui();

		pages.clear();

		panelWidth = 300;
		panelHeight = 190;
	}

	protected void buildHeaders() {
		headers.clear();
		int curX = menuX;
		int headerY = menuY - 8;
		for(int i = 0; i < pages.size(); i++) {
			int titleWidth = Math.max(4, fontRendererObj.getStringWidth(pages.get(i).getTitle()) - 8);
			headers.add(new GuiTabHeader(pages.get(i), curX, headerY, titleWidth + 32, 16));
			curX += titleWidth + 24;
		}

		if(!pages.isEmpty()) {
			currentTab = pages.get(0);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		for(int i = 0; i < headers.size(); i++) {
			GuiTabHeader header = headers.get(i);

			if(mouseX >= header.x && mouseX < header.x + header.width - 8 && mouseY >= header.y && mouseY < header.y + header.height) {
				header.tabPage.tabClicked();
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		super.drawScreen(mouseX, mouseY, par3);
		mc.renderEngine.bindTexture(tabHeader);
		drawTexturedRect(menuX, menuY + 8, panelWidth - 16, panelHeight - 16, 0, 16, 16, 16, 256, 256);
		drawTexturedRect(menuX + 16, menuY + 8 + panelHeight - 16, panelWidth - 32, 16, 0, 16, 16, 16, 256, 256);
		drawTexturedRect(menuX + panelWidth - 16, menuY + 24, 16, panelHeight - 32, 0, 16, 16, 16, 256, 256);
		drawTexturedRect(menuX, menuY + 8 + panelHeight - 16, 16, 16, 0, 32, 16, 16, 256, 256);
		drawTexturedRect(menuX + panelWidth - 16, menuY + 8 + panelHeight - 16, 16, 16, 16, 32, 16, 16, 256, 256);
		drawTexturedRect(menuX + panelWidth - 16, menuY + 8, 16, 16, 16, 16, 16, 16, 256, 256);

		if(currentTab != null) {
//			currentTab.drawScreen(mouseX, mouseY, par3);
		}

		for(int i = headers.size() - 1; i >= 0; i--) {
			GuiTabHeader header = headers.get(i);

			boolean hovered = false;
			if(mouseX >= header.x && mouseX < header.x + header.width - 8 && mouseY >= header.y && mouseY < header.y + header.height) {
				hovered = true;
			}

			if(currentTab == header.tabPage) {
				GL11.glColor4f(1f, 1f, 1f, 1f);
			} else {
				GL11.glColor4f(0.5f, 0.5f, 0.5f, 1f);
			}
			GL11.glEnable(GL11.GL_BLEND);
			mc.renderEngine.bindTexture(tabHeader);
			drawTexturedRect(header.x, header.y, 16, 16, 0, 0, 16, 16, 256, 256);
			drawTexturedRect(header.x + 16, header.y, header.width - 32, 16, 16, 0, 16, 16, 256, 256);
			drawTexturedRect(header.x + header.width - 16, header.y, 16, 16, 32, 0, 16, 16, 256, 256);
			GL11.glDisable(GL11.GL_BLEND);

			drawString(fontRendererObj, header.tabPage.getTitle(), header.x + 8, header.y + 8 - fontRendererObj.FONT_HEIGHT / 2, hovered ? -12345678 : Globals.TEXT_COLOR);
		}
	}

}
