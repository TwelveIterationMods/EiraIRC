package net.blay09.mods.eirairc.client.gui;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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
	private GuiTextField txtNick;
	private GuiList lstChannels;
	private GuiButton btnTheme;
	private GuiButton btnBotSettings;
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

		labelList.add(new GuiLabel("Channels", rightX - 100, topY, Globals.TEXT_COLOR));

		lstChannels = new GuiList(rightX - 100, topY + 15, 100, 80, 15);
		listList.add(lstChannels);

		btnDelete = new GuiButton(0, leftX, topY + 150, 100, 20, "Delete");
		btnDelete.packedFGColour = -65536;
		buttonList.add(btnDelete);

		btnTheme = new GuiButton(1, leftX, topY + 75, 100, 20, "Configure Theme...");
		buttonList.add(btnTheme);

		btnBotSettings = new GuiButton(2, leftX, topY + 100, 100, 20, "Configure Bot...");
		buttonList.add(btnBotSettings);
	}

	@Override
	public boolean requestClose() {
		if(!txtAddress.getText().isEmpty()) {
			if(!txtAddress.getText().equals(config.getAddress())) {
				ConfigurationHandler.removeServerConfig(config.getAddress());
				config.setAddress(txtAddress.getText());
				ConfigurationHandler.addServerConfig(config);
				isNew = false;
				title = config.getAddress();
				tabContainer.initGui();
			}
			config.setNick(txtNick.getText());
		} else {
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
		} else if(button == btnDelete) {
			if(isNew) {
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
		}
		Minecraft.getMinecraft().displayGuiScreen(tabContainer);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

}
