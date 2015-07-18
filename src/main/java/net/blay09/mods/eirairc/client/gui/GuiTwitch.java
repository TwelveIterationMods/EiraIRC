package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.api.IRCReplyCodes;
import net.blay09.mods.eirairc.api.event.IRCConnectEvent;
import net.blay09.mods.eirairc.api.event.IRCConnectionFailedEvent;
import net.blay09.mods.eirairc.api.event.IRCErrorEvent;
import net.blay09.mods.eirairc.client.gui.base.GuiAdvancedTextField;
import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.config.AuthManager;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.settings.BotBooleanComponent;
import net.blay09.mods.eirairc.config.settings.BotStringComponent;
import net.blay09.mods.eirairc.config.settings.GeneralBooleanComponent;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;


public class GuiTwitch extends EiraGuiScreen implements GuiYesNoCallback {

	private static final ResourceLocation twitchLogo = new ResourceLocation("eirairc", "gfx/twitch_logo.png");

	private final ServerConfig config;
	private GuiCheckBox chkAnonymous;
	private GuiTextField txtUsername;
	private GuiAdvancedTextField txtPassword;
	private GuiButton btnOAuthHelp;
	private GuiButton btnConnect;

	public GuiTwitch(GuiScreen parentScreen) {
		super(parentScreen);
		config = ConfigurationHandler.getOrCreateServerConfig(Globals.TWITCH_SERVER);
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		MinecraftForge.EVENT_BUS.register(this);

		final int topY = height / 2 - 30;

		boolean oldChecked;
		if(chkAnonymous != null) {
			oldChecked = chkAnonymous.isChecked();
		} else {
			oldChecked = config.getNick().equals("%ANONYMOUS%");
		}
		chkAnonymous = new GuiCheckBox(2, width / 2 - 90, topY, "Login anonymously (read-only)", oldChecked);
		buttonList.add(chkAnonymous);

		labelList.add(new GuiLabel("Twitch Username", width / 2 - 90, topY + 20, Globals.TEXT_COLOR));

		String oldText;
		if(txtUsername != null) {
			oldText = txtUsername.getText();
		} else {
			oldText = config.getNick();
		}
		txtUsername = new GuiTextField(0, fontRendererObj, width / 2 - 90, topY + 35, 180, 15);
		txtUsername.setMaxStringLength(Integer.MAX_VALUE);
		txtUsername.setText(oldText);
		textFieldList.add(txtUsername);

		labelList.add(new GuiLabel("O-Auth Token", width / 2 - 90, topY + 60, Globals.TEXT_COLOR));

		if(txtPassword != null) {
			oldText = txtPassword.getText();
		} else {
			oldText = AuthManager.getServerPassword(config.getIdentifier());
		}
		txtPassword = new GuiAdvancedTextField(1, fontRendererObj, width / 2 - 90, topY + 75, 180, 15);
		txtPassword.setDefaultPasswordChar();
		txtPassword.setText(oldText);
		textFieldList.add(txtPassword);

		btnOAuthHelp = new GuiButton(0, width / 2 + 94, topY + 72, 20, 20, "?");
		buttonList.add(btnOAuthHelp);

		btnConnect = new GuiButton(1, width / 2 - 100, topY + 100, "Connect");
		buttonList.add(btnConnect);

		if(chkAnonymous.isChecked()) {
			txtUsername.setEnabled(false);
			txtPassword.setEnabled(false);
			btnOAuthHelp.enabled = false;
		}
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnOAuthHelp) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiConfirmOpenLink(this, Globals.TWITCH_OAUTH, 0, true));
		} else if(button == btnConnect) {
			if(chkAnonymous.isChecked()) {
				config.setNick("%ANONYMOUS%");
				AuthManager.putServerPassword(config.getIdentifier(), null);
				config.getGeneralSettings().setBoolean(GeneralBooleanComponent.ReadOnly, true);
				config.getBotSettings().setString(BotStringComponent.MessageFormat, "Twitch");
				config.getBotSettings().setBoolean(BotBooleanComponent.RelayIRCJoinLeave, false);
				btnConnect.enabled = false;
				ConfigurationHandler.addServerConfig(config);
				Utils.connectTo(config);
			} else {
				config.setNick(txtUsername.getText());
				AuthManager.putServerPassword(config.getIdentifier(), txtPassword.getText());
				config.getGeneralSettings().setBoolean(GeneralBooleanComponent.ReadOnly, false);
				config.getBotSettings().setString(BotStringComponent.MessageFormat, "Twitch");
				config.getBotSettings().setBoolean(BotBooleanComponent.RelayIRCJoinLeave, false);
				if(!config.getNick().isEmpty() && !txtPassword.getText().isEmpty()) {
					config.getOrCreateChannelConfig("#" + config.getNick());
					btnConnect.enabled = false;
					ConfigurationHandler.addServerConfig(config);
					Utils.connectTo(config);
				}
			}
			ConfigurationHandler.saveServers();
		} else if(button == chkAnonymous) {
			if(chkAnonymous.isChecked()) {
				txtUsername.setEnabled(false);
				txtPassword.setEnabled(false);
				btnOAuthHelp.enabled = false;
			} else {
				txtUsername.setEnabled(true);
				txtPassword.setEnabled(true);
				btnOAuthHelp.enabled = true;
			}
		}
	}

	@SubscribeEvent
	public void onSuccess(IRCConnectEvent event) {
		if(event.connection.isTwitch()) {
			gotoPrevious();
		}
	}

	@SubscribeEvent
	public void onFailure(IRCConnectionFailedEvent event) {
		if(event.connection.isTwitch()) {
			btnConnect.enabled = true;
		}
	}

	@SubscribeEvent
	public void onWrongPassword(IRCErrorEvent event) {
		if(event.numeric == IRCReplyCodes.ERR_PASSWDMISMATCH && event.connection.isTwitch()) {
			btnConnect.enabled = true;
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
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		drawLightBackground(menuX, menuY, menuWidth, menuHeight);

		mc.renderEngine.bindTexture(twitchLogo);
		EiraGui.drawTexturedRect(menuX + menuWidth / 2 - 64, menuY + 10, 128, 43, 0, 0, 128, 43, zLevel, 128, 43);

		super.drawScreen(mouseX, mouseY, par3);
	}

}
