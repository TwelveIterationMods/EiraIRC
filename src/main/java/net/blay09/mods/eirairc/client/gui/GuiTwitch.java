package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.client.gui.base.GuiAdvancedTextField;
import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.settings.BotStringComponent;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiTwitch extends EiraGuiScreen implements GuiYesNoCallback {

	private static final ResourceLocation twitchLogo = new ResourceLocation("eirairc", "gfx/twitch_logo.png");

	private ServerConfig config;
	private GuiTextField txtUsername;
	private GuiAdvancedTextField txtPassword;
	private GuiButton btnOAuthHelp;
	private GuiButton btnConnect;

	public GuiTwitch(GuiScreen parentScreen) {
		super(parentScreen);
		config = ConfigurationHandler.getOrCreateServerConfig(Globals.TWITCH_SERVER);
		if(ConfigurationHandler.hasServerConfig(Globals.TWITCH_SERVER)) {
			config.setNick("");
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);

		final int topX = height / 2 - 30;

		labelList.add(new GuiLabel("Twitch Username", width / 2 - 90, topX, Globals.TEXT_COLOR));

		String oldText;
		if(txtUsername != null) {
			oldText = txtUsername.getText();
		} else {
			oldText = config.getNick();
		}
		txtUsername = new GuiTextField(fontRendererObj, width / 2 - 90, topX + 15, 180, 15);
		txtUsername.setMaxStringLength(Integer.MAX_VALUE);
		txtUsername.setText(oldText);
		textFieldList.add(txtUsername);

		labelList.add(new GuiLabel("O-Auth Token", width / 2 - 90, topX + 40, Globals.TEXT_COLOR));

		if(txtPassword != null) {
			oldText = txtPassword.getText();
		} else {
			oldText = config.getServerPassword();
		}
		txtPassword = new GuiAdvancedTextField(fontRendererObj, width / 2 - 90, topX + 55, 180, 15);
		txtPassword.setMaxStringLength(Integer.MAX_VALUE);
		txtPassword.setDefaultPasswordChar();
		txtPassword.setText(oldText);
		textFieldList.add(txtPassword);

		btnOAuthHelp = new GuiButton(0, width / 2 + 94, topX + 52, 20, 20, "?");
		buttonList.add(btnOAuthHelp);

		btnConnect = new GuiButton(1, width / 2 - 100, topX + 90, "Connect");
		buttonList.add(btnConnect);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnOAuthHelp) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiConfirmOpenLink(this, Globals.TWITCH_OAUTH, 0, true));
		} else if(button == btnConnect) {
			config.setNick(txtUsername.getText());
			config.setServerPassword(txtPassword.getText());
			if(!config.getNick().isEmpty() && !config.getServerPassword().isEmpty()) {
				config.getBotSettings().setString(BotStringComponent.BotProfile, "Twitch");
				config.getOrCreateChannelConfig(config.getNick().toLowerCase());
				ConfigurationHandler.addServerConfig(config);
				Utils.connectTo(config);
			}
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

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		drawLightBackground(menuX, menuY, menuWidth, menuHeight);

		mc.renderEngine.bindTexture(twitchLogo);
		EiraGui.drawTexturedRect(menuX + menuWidth / 2 - 64, menuY + 10, 128, 43, 0, 0, 128, 43, zLevel, 128, 43);

		super.drawScreen(mouseX, mouseY, par3);
	}

}
