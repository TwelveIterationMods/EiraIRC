package net.blay09.mods.eirairc.client.gui;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabContainer;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabPage;
import net.blay09.mods.eirairc.config.ServerConfig;
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
public class GuiServerConfig extends GuiTabPage {

	private ServerConfig config;
	private GuiTextField txtAddress;
	private GuiTextField txtNick;
	private GuiButton btnTheme;
	private GuiButton btnBotSettings;
	private GuiButton btnGeneral;
	private GuiButton btnAdvanced;

	public GuiServerConfig(GuiTabContainer tabContainer) {
		super(tabContainer, "<new>");
		this.config = new ServerConfig();
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
		final int topX = height / 2 - 80;
		String oldText;

		labelList.add(new GuiLabel("Address", leftX, topX, Globals.TEXT_COLOR));

		if(txtAddress != null) {
			oldText = txtAddress.getText();
		} else {
			oldText = config.getAddress();
		}
		txtAddress = new GuiTextField(fontRendererObj, leftX, topX + 15, 100, 15);
		txtAddress.setText(oldText);
		textFieldList.add(txtAddress);

		labelList.add(new GuiLabel("Nick", leftX, topX + 40, Globals.TEXT_COLOR));

		if(txtNick != null) {
			oldText = txtNick.getText();
		} else {
			oldText = config.getNick();
		}
		txtNick = new GuiTextField(fontRendererObj, leftX, topX + 55, 100, 15);
		txtNick.setText(oldText);
		textFieldList.add(txtNick);

		btnAdvanced = new GuiButton(0, rightX - 210, topX + 150, 100, 20, "Advanced");
		buttonList.add(btnAdvanced);

		btnTheme = new GuiButton(1, rightX - 210, topX + 100, 100, 20, "Configure Theme");
		buttonList.add(btnTheme);
	}

	@Override
	public boolean requestClose() {
		if(!txtAddress.getText().isEmpty()) {
			if(!txtAddress.getText().equals(config.getAddress())) {
				ConfigurationHandler.removeServerConfig(config.getAddress());
				config.setAddress(txtAddress.getText());
				ConfigurationHandler.addServerConfig(config);
			}
			config.setNick(txtNick.getText());
		}
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void actionPerformed(GuiButton button) {
		if(button == btnTheme) {
			String configId = "server:" + config.getAddress();
			mc.displayGuiScreen(new GuiConfig(tabContainer, GuiEiraIRCConfig.getThemeConfigElements(config.getTheme().pullDummyConfig(configId).getCategory("theme"), false), "eirairc", configId, false, false, "Theme (" + config.getAddress() + ")"));
		}
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
