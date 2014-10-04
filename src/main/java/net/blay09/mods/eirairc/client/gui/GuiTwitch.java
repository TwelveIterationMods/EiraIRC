package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.client.gui.base.GuiAdvancedTextField;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
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

	private static final ResourceLocation twitchLogo = new ResourceLocation("eirairc", "gfx/Twitch_Logo_White.png");

	private GuiScreen parentScreen;
	private ServerConfig config;
	private GuiTextField txtUsername;
	private GuiAdvancedTextField txtPassword;
	private GuiButton btnOAuthHelp;

	public GuiTwitch(GuiScreen parentScreen) {
		this.parentScreen = parentScreen;
		config = ConfigurationHandler.getServerConfig(Globals.TWITCH_SERVER);
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);

		txtUsername = new GuiTextField(fontRendererObj, width / 2 - 90, height / 2, 180, 15);
		txtUsername.setMaxStringLength(Integer.MAX_VALUE);
		txtUsername.setText(config.getNick());
		textFieldList.add(txtUsername);

		txtPassword = new GuiAdvancedTextField(fontRendererObj, width / 2 - 90, height / 2 + 40, 180, 15);
		txtPassword.setMaxStringLength(Integer.MAX_VALUE);
		txtPassword.setDefaultPasswordChar();
		txtPassword.setText(config.getServerPassword());
		textFieldList.add(txtPassword);

		btnOAuthHelp = new GuiButton(1, width / 2 + 94, height / 2 + 37, 20, 20, "?");
		buttonList.add(btnOAuthHelp);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnOAuthHelp) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiConfirmOpenLink(this, Globals.TWITCH_OAUTH, 0, true));
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
		final int menuWidth = 300;
		final int menuHeight = 200;
		final int menuX = width / 2 - menuWidth / 2;
		final int menuY = height / 2 - menuHeight / 2;

		drawLightBackground(menuX, menuY, menuWidth, menuHeight);

		mc.renderEngine.bindTexture(twitchLogo);
		drawTexturedModalRect(menuX + menuWidth / 2 - 64, menuY + 10, 0, 0, 128, 43);

		super.drawScreen(mouseX, mouseY, par3);
	}

}
