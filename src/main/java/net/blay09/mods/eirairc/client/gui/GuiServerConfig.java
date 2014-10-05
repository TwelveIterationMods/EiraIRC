package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabContainer;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabPage;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import org.lwjgl.input.Keyboard;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiServerConfig extends GuiTabPage {

	private ServerConfig config;
	private GuiTextField txtAddress;
	private GuiTextField txtNick;
	private GuiButton btnAdvanced;

	public GuiServerConfig() {
		super("<new>");
	}

	public GuiServerConfig(ServerConfig config) {
		super(config.getAddress());
		this.config = config;
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);

		final int topX = height / 2 - 30;
		String oldText;

		labelList.add(new GuiLabel("Address", width / 2 - 90, topX, Globals.TEXT_COLOR));

		if(txtAddress != null) {
			oldText = txtAddress.getText();
		} else {
			oldText = "";
		}
		txtAddress = new GuiTextField(fontRendererObj, width / 2 - 90, topX, 180, 15);
		txtAddress.setText(oldText);
		textFieldList.add(txtAddress);

		labelList.add(new GuiLabel("Nick", width / 2 - 90, topX, Globals.TEXT_COLOR));

		if(txtNick != null) {
			oldText = txtNick.getText();
		} else {
			oldText = config != null ? config.getNick() : Globals.DEFAULT_NICK;
		}
		txtNick = new GuiTextField(fontRendererObj, width / 2 - 90, topX + 15, 180, 15);
		txtNick.setText(oldText);
		textFieldList.add(txtNick);
	}

	@Override
	public void actionPerformed(GuiButton button) {

	}

	@Override
	public void confirmClicked(boolean result, int id) {
		if(result) {
			Utils.openWebpage(Globals.TWITCH_OAUTH);
		}
		Minecraft.getMinecraft().displayGuiScreen(this);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

}
