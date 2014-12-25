// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui;

import cpw.mods.fml.client.config.GuiCheckBox;
import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.TrustedServer;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.gui.GuiButton;

public class GuiEiraIRCRedirect extends EiraGuiScreen {

	private final ServerConfig serverConfig;

	private GuiCheckBox chkAlwaysAllow;
	private GuiButton btnAllow;
	private GuiButton btnReject;

	public GuiEiraIRCRedirect(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	@Override
	public void initGui() {
		super.initGui();

		final int centerX = width / 2;
		final int centerY = height / 2;

		labelList.add(new GuiLabel("The server wants to redirect your EiraIRC to the following channel(s):", centerX, centerY, Globals.TEXT_COLOR));

		labelList.add(new GuiLabel("Server: " + serverConfig.getAddress(), centerX, centerY, Globals.TEXT_COLOR));
		StringBuilder sb = new StringBuilder();
		if(serverConfig.getChannelConfigs().size() == 0) {
			sb.append("None");
		} else {
			for(ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
				if(sb.length() > 0) {
					sb.append(", ");
				}
				sb.append(channelConfig.getName());
			}
		}
		labelList.add(new GuiLabel("Channel: " + sb.toString(), centerX, centerY, Globals.TEXT_COLOR));

		chkAlwaysAllow = new GuiCheckBox(0, centerX, centerY, "Always allow this server to redirect me", false);
		buttonList.add(chkAlwaysAllow);

		btnAllow = new GuiButton(1, centerX, centerY, "Allow");
		buttonList.add(btnAllow);

		btnReject = new GuiButton(2, centerX, centerY, "Reject");
		buttonList.add(btnReject);

	}

	@Override
	public void actionPerformed(GuiButton button) {
		if(button == chkAlwaysAllow) {
			btnReject.enabled = !chkAlwaysAllow.isChecked();
		} else if(button == btnReject) {
			mc.displayGuiScreen(null);
		} else if(button == btnAllow) {
			if(chkAlwaysAllow.isChecked()) {
				TrustedServer server = ConfigurationHandler.getOrCreateTrustedServer(Utils.getServerAddress());
				server.setAllowRedirect(true);
				ConfigurationHandler.addTrustedServer(server);
			}
			Utils.redirectTo(serverConfig, false);
			mc.displayGuiScreen(null);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		drawLightBackground(menuX, menuY, menuWidth, menuHeight);

		super.drawScreen(mouseX, mouseY, par3);
	}
}
