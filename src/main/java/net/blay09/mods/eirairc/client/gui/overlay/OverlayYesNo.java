package net.blay09.mods.eirairc.client.gui.overlay;

import net.blay09.mods.eirairc.client.gui.EiraGuiScreen;
import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.util.Globals;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;


public class OverlayYesNo extends GuiOverlay {

	private final int id;
	private final String bigText;
	private final String smallText;
	private GuiButton btnConfirm;
	private GuiButton btnCancel;

	public OverlayYesNo(GuiScreen parentScreen, String bigText, String smallText, int id) {
		super(parentScreen);
		this.bigText = bigText;
		this.smallText = smallText;
		this.id = id;
	}

	@Override
	public void initGui() {
		super.initGui();
		GuiLabel lblText = new GuiLabel(bigText, 0, height / 2 - 30, Globals.TEXT_COLOR);
		lblText.setHAlignment(GuiLabel.HAlignment.Center, width);
		labelList.add(lblText);
		lblText = new GuiLabel(smallText, 0, height / 2 - 10, Globals.TEXT_COLOR);
		lblText.setHAlignment(GuiLabel.HAlignment.Center, width);
		labelList.add(lblText);

		btnConfirm = new GuiButton(0, width / 2 - 110, height / 2 + 10, 100, 20, "Yes");
		buttonList.add(btnConfirm);
		btnCancel = new GuiButton(1, width / 2 + 10, height / 2 + 10, 100, 20, "No");
		buttonList.add(btnCancel);

		setupMenuSize(width, 80);
		menuY = height / 2 - 40;
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(button == btnConfirm) {
			parentScreen.confirmClicked(true, id);
		} else if(button == btnCancel) {
			parentScreen.confirmClicked(false, id);
		}
		((EiraGuiScreen) parentScreen).setOverlay(null);
	}

	@Override
	public void gotoPrevious() {
		parentScreen.confirmClicked(false, id);
		super.gotoPrevious();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		drawRect(0, height / 2 - 40, width, height / 2 + 40, -16777216);
		super.drawScreen(mouseX, mouseY, p_73863_3_);
	}
}
