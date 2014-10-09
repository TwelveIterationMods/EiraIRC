package net.blay09.mods.eirairc.client.gui;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.blay09.mods.eirairc.client.gui.base.GuiAdvancedTextField;
import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.client.gui.base.GuiList;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabContainer;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabPage;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.SharedGlobalConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import org.lwjgl.input.Keyboard;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiServerConfig extends GuiTabPage implements GuiYesNoCallback {

	private ServerConfig config;
	private GuiTextField txtAddress;
	private GuiAdvancedTextField txtNick;
	private GuiList lstChannels;
	private GuiButton btnTheme;
	private GuiButton btnBotSettings;
	private GuiButton btnOtherSettings;
	private GuiButton btnAdvanced;
	private GuiButton btnDelete;

	private boolean isNew;

	public GuiServerConfig(GuiTabContainer tabContainer) {
		super(tabContainer, "<new>");
		this.config = new ServerConfig();
		isNew = true;
	}

	public GuiServerConfig(GuiTabContainer tabContainer, ServerConfig config) {
		super(tabContainer, config.getAddress());
		this.config = config;
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		allowSideClickClose = false;
		title = config.getAddress().isEmpty() ? "<new>" : config.getAddress();

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
		txtNick = new GuiAdvancedTextField(fontRendererObj, leftX, topY + 55, 100, 15);
		txtNick.setDefaultText(Globals.DEFAULT_NICK, false);
		txtNick.setText(oldText);
		textFieldList.add(txtNick);

		labelList.add(new GuiLabel("Channels", rightX - 100, topY, Globals.TEXT_COLOR));

		lstChannels = new GuiList(rightX - 100, topY + 15, 100, 80, 15);
		listList.add(lstChannels);

		btnDelete = new GuiButton(0, rightX - 100, topY + 150, 100, 20, "Delete");
		btnDelete.packedFGColour = -65536;
		buttonList.add(btnDelete);

		btnTheme = new GuiButton(1, leftX, topY + 85, 100, 20, "Configure Theme...");
		buttonList.add(btnTheme);

		btnBotSettings = new GuiButton(2, leftX, topY + 110, 100, 20, "Configure Bot...");
		buttonList.add(btnBotSettings);

		btnOtherSettings = new GuiButton(3, leftX, topY + 135, 100, 20, "Other Settings...");
		buttonList.add(btnOtherSettings);

		btnAdvanced = new GuiButton(4, rightX - 100, topY + 125, 100, 20, "Advanced");
		buttonList.add(btnAdvanced);
	}

	@Override
	public boolean requestClose() {
		if(txtAddress.getText().isEmpty() && isNew) {
			tabContainer.removePage(this);
			tabContainer.initGui();
		}
		return true;
	}

	public boolean isNew() {
		return isNew;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void actionPerformed(GuiButton button) {
		if(button == btnTheme) {
			mc.displayGuiScreen(new GuiConfig(tabContainer, GuiEiraIRCConfig.getThemeConfigElements(config.getTheme().pullDummyConfig().getCategory("theme"), false), Globals.MOD_ID, "server:" + config.getAddress(), false, false, "Theme (" + config.getAddress() + ")"));
		} else if(button == btnBotSettings) {
			mc.displayGuiScreen(new GuiConfig(tabContainer, new ConfigElement(config.getBotSettings().pullDummyConfig().getCategory("bot")).getChildElements(), Globals.MOD_ID, "server:" + config.getAddress(), false, false, "Bot Settings (" + config.getAddress() + ")"));
		} else if(button == btnOtherSettings) {
			mc.displayGuiScreen(new GuiConfig(tabContainer, new ConfigElement(config.getGeneralSettings().pullDummyConfig().getCategory("settings")).getChildElements(), Globals.MOD_ID, "server:" + config.getAddress(), false, false, "Other Settings (" + config.getAddress() + ")"));
		} else if(button == btnAdvanced) {
			tabContainer.setCurrentTab(new GuiServerConfigAdvanced(tabContainer, this), false);
		} else if(button == btnDelete) {
			if(isNew) {
				tabContainer.removePage(this);
				tabContainer.initGui();
			} else {
				mc.displayGuiScreen(new GuiYesNo(this, "Do you really want to delete this server configuration?", "This can't be undone, so be careful!", 0));
			}
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
		applyChanges();
	}

	public ServerConfig getServerConfig() {
		return config;
	}

	public void applyChanges() {
		if(!txtAddress.getText().isEmpty() && !txtAddress.getText().equals(config.getAddress())) {
			ConfigurationHandler.removeServerConfig(config.getAddress());
			config.setAddress(txtAddress.getText());
			ConfigurationHandler.addServerConfig(config);
			isNew = false;
		}
		config.setNick(txtNick.getTextOrDefault());
		ConfigurationHandler.saveServers();
		title = config.getAddress();
		tabContainer.initGui();
	}
}
