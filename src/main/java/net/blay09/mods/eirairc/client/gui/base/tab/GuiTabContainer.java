package net.blay09.mods.eirairc.client.gui.base.tab;

import net.blay09.mods.eirairc.client.graphics.TextureRegion;
import net.blay09.mods.eirairc.client.gui.EiraGui;
import net.blay09.mods.eirairc.client.gui.EiraGuiScreen;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GuiTabContainer extends EiraGuiScreen {

	private final List<GuiTabHeader> headers = new ArrayList<GuiTabHeader>();
	protected final List<GuiTabPage> pages = new ArrayList<GuiTabPage>();

	private final TextureRegion regionContent;
	private final TextureRegion regionTopRight;
	private final TextureRegion regionBottomRight;
	private final TextureRegion regionBottomLeft;

	protected GuiTabPage currentTab;
	protected int panelWidth;
	protected int panelHeight;

	public GuiTabContainer(GuiScreen parentScreen) {
		super(parentScreen);

		regionContent = EiraGui.atlas.findRegion("tab_bg_content");
		regionTopRight = EiraGui.atlas.findRegion("tab_bg_topright");
		regionBottomRight = EiraGui.atlas.findRegion("tab_bg_bottomright");
		regionBottomLeft = EiraGui.atlas.findRegion("tab_bg_bottomleft");
	}

	@Override
	public void initGui() {
		super.initGui();

		panelWidth = 300;
		panelHeight = 190;

		if(currentTab != null) {
			currentTab.setWorldAndResolution(mc, width, height);
		}
	}

	protected void buildHeaders() {
		headers.clear();
		int curX = menuX;
		int headerY = menuY - 8;
		for (GuiTabPage page : pages) {
			int titleWidth = Math.max(4, fontRendererObj.getStringWidth(page.getTitle()) - 8);
			headers.add(new GuiTabHeader(page, curX, headerY, titleWidth + 32, 16));
			curX += titleWidth + 24;
		}
	}

	public void setCurrentTab(GuiTabPage tabPage, boolean forceClose) {
		if(currentTab == tabPage) {
			return;
		}
		if(currentTab != null) {
			if(!forceClose && !currentTab.requestClose()) {
				return;
			}
			currentTab.onGuiClosed();
		}
		currentTab = tabPage;
		if(currentTab != null) {
			currentTab.setWorldAndResolution(mc, width, height);
		}
	}

	@Override
<<<<<<< HEAD
	public boolean mouseClick(int mouseX, int mouseY, int mouseButton) throws IOException {
		for(GuiTabHeader header : headers) {
			if(mouseX >= header.x && mouseX < header.x + header.width - 8 && mouseY >= header.y && mouseY < header.y + header.height) {
=======
	public boolean mouseClick(int mouseX, int mouseY, int mouseButton) {
		for(GuiTabHeader header : headers) {
			if (mouseX >= header.x && mouseX < header.x + header.width - 8 && mouseY >= header.y && mouseY < header.y + header.height) {
>>>>>>> d248e1685dde1dafba3323d197ad61200374c3a9
				header.tabPage.tabClicked();
				break;
			}
		}
		return currentTab != null && currentTab.mouseClick(mouseX, mouseY, mouseButton) || super.mouseClick(mouseX, mouseY, mouseButton);
	}

	@Override
	public void gotoPrevious() {
		if(currentTab.getParentScreen() instanceof GuiTabPage) {
			setCurrentTab((GuiTabPage) currentTab.getParentScreen(), false);
		} else {
			super.gotoPrevious();
		}
	}

	@Override
	public void keyTyped(char unicode, int keyCode) throws IOException {
		super.keyTyped(unicode, keyCode);

		if(currentTab != null) {
			currentTab.keyTyped(unicode, keyCode);
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();

		if(currentTab != null) {
			currentTab.requestClose();
			currentTab.onGuiClosed();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		super.drawScreen(mouseX, mouseY, par3);

		regionContent.draw(menuX, menuY + 8, panelWidth - 16, panelHeight - 16);
		regionContent.draw(menuX + 16, menuY + 8 + panelHeight - 16, panelWidth - 32, 16);
		regionContent.draw(menuX + panelWidth - 16, menuY + 24, 16, panelHeight - 32);
		regionBottomLeft.draw(menuX, menuY + 8 + panelHeight - 16);
		regionBottomRight.draw(menuX + panelWidth - 16, menuY + 8 + panelHeight - 16);
		regionTopRight.draw(menuX + panelWidth - 16, menuY + 8);

		if(currentTab != null) {
			currentTab.drawScreen(mouseX, mouseY, par3);
		}

		GuiTabHeader currentHeader = null;
		for(int i = headers.size() - 1; i >= 0; i--) {
			GuiTabHeader header = headers.get(i);
			if(currentTab != null && (header.tabPage == currentTab || header.tabPage == currentTab.getParentScreen())) {
				currentHeader = header;
			} else {
				header.draw(mouseX, mouseY, false);
			}
		}
		if(currentHeader != null) {
			currentHeader.draw(mouseX, mouseY, true);
		}
	}

	public void removePage(GuiTabPage tabPage) {
		pages.remove(tabPage);
		if(tabPage == currentTab) {
			if(pages.size() > 0) {
				setCurrentTab(pages.get(0), true);
			} else {
				setCurrentTab(null, true);
			}
		}
	}
}
