package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.client.gui.base.GuiAdvancedTextField;
import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabContainer;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabPage;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import org.lwjgl.input.Keyboard;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiServerConfigAdvanced extends GuiTabPage implements GuiYesNoCallback {

	private final GuiServerConfig parent;
	private final ServerConfig config;

	private GuiTextField txtAddress;
	private GuiTextField txtNick;
	private GuiAdvancedTextField txtServerPassword;
	private GuiTextField txtNickServName;
	private GuiAdvancedTextField txtNickServPassword;
	private GuiButton btnBack;
	private GuiButton btnDelete;

	public GuiServerConfigAdvanced(GuiTabContainer tabContainer, GuiServerConfig parent) {
		super(tabContainer, parent.getTitle());
		this.parent = parent;
		config = parent.getServerConfig();
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		allowSideClickClose = false;

		final int leftX = width / 2 - 130;
		final int rightX = width / 2 + 130;
		final int topY = height / 2 - 80;
		String oldText;

		labelList.add(new GuiLabel("Address", leftX, topY, Globals.TEXT_COLOR));

		if(txtAddress != null) {
			oldText = txtAddress.getText();
		} else {
			oldText = config.getAddress();
		}
		txtAddress = new GuiTextField(fontRendererObj, leftX, topY + 15, 100, 15);
		txtAddress.setText(oldText);
		textFieldList.add(txtAddress);

		labelList.add(new GuiLabel("Nick", leftX, topY + 40, Globals.TEXT_COLOR));

		if(txtNick != null) {
			oldText = txtNick.getText();
		} else {
			oldText = config.getNick();
		}
		txtNick = new GuiTextField(fontRendererObj, leftX, topY + 55, 100, 15);
		txtNick.setText(oldText);
		textFieldList.add(txtNick);

		labelList.add(new GuiLabel("NickServ Username", leftX, topY + 80, Globals.TEXT_COLOR));

		if(txtNickServName != null) {
			oldText = txtNickServName.getText();
		} else {
			oldText = config.getNickServName();
		}
		txtNickServName = new GuiTextField(fontRendererObj, leftX, topY + 95, 100, 15);
		txtNickServName.setText(oldText);
		textFieldList.add(txtNickServName);

		labelList.add(new GuiLabel("NickServ Password", leftX, topY + 120, Globals.TEXT_COLOR));

		if(txtNickServPassword != null) {
			oldText = txtNickServPassword.getText();
		} else {
			oldText = config.getNickServPassword();
		}
		txtNickServPassword = new GuiAdvancedTextField(fontRendererObj, leftX, topY + 135, 100, 15);
		txtNickServPassword.setText(oldText);
		txtNickServPassword.setDefaultPasswordChar();
		textFieldList.add(txtNickServPassword);

		labelList.add(new GuiLabel("Server Password", rightX - 100, topY, Globals.TEXT_COLOR));

		if(txtServerPassword != null) {
			oldText = txtServerPassword.getText();
		} else {
			oldText = config.getServerPassword();
		}
		txtServerPassword = new GuiAdvancedTextField(fontRendererObj, rightX - 100, topY + 15, 100, 15);
		txtServerPassword.setText(oldText);
		txtServerPassword.setDefaultPasswordChar();
		textFieldList.add(txtServerPassword);

		btnDelete = new GuiButton(0, rightX - 100, topY + 150, 100, 20, "Delete");
		btnDelete.packedFGColour = -65536;
		buttonList.add(btnDelete);

		btnBack = new GuiButton(1, rightX - 100, topY + 125, 100, 20, "Back");
		buttonList.add(btnBack);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			tabContainer.setCurrentTab(parent, false);
		} else if(button == btnDelete) {
			mc.displayGuiScreen(new GuiYesNo(this, "Do you really want to delete this server configuration?", "This can't be undone, so be careful!", 0));
		}
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		if(result) {
			ConfigurationHandler.removeServerConfig(config.getAddress());
			ConfigurationHandler.saveServers();
			tabContainer.removePage(this);
		}
		Minecraft.getMinecraft().displayGuiScreen(tabContainer);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
		if(!txtAddress.getText().isEmpty() && !txtAddress.getText().equals(config.getAddress())) {
			ConfigurationHandler.removeServerConfig(config.getAddress());
			config.setAddress(txtAddress.getText());
			ConfigurationHandler.addServerConfig(config);
		}
		config.setNick(txtNick.getText());
		config.setNickServ(txtNickServName.getText(), txtNickServPassword.getText());
		config.setServerPassword(txtServerPassword.getText());
		ConfigurationHandler.saveServers();
		tabContainer.initGui();
	}

}
