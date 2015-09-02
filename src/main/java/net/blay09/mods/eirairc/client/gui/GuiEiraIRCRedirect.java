// Copyright (c) 2015, Christopher "BlayTheNinth" Baker


package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.ConnectionManager;
import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.TrustedServer;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.I19n;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiCheckBox;

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

		GuiLabel lblTitle = new GuiLabel((serverConfig != null ? I19n.format("eirairc:gui.redirect") : I19n.format("eirairc:gui.redirect.disable")), 0, centerY - 70, Globals.TEXT_COLOR);
		lblTitle.setHAlignment(GuiLabel.HAlignment.Center, width);
		labelList.add(lblTitle);

		if(serverConfig != null) {
			GuiLabel lblServer = new GuiLabel(I19n.format("eirairc:gui.redirect.server") + " " + serverConfig.getAddress(), 0, centerY - 40, Globals.TEXT_COLOR);
			lblServer.setHAlignment(GuiLabel.HAlignment.Center, width);
			labelList.add(lblServer);

			StringBuilder sb = new StringBuilder();
			if (serverConfig.getChannelConfigs().size() == 0) {
				sb.append(I19n.format("eirairc:gui.none"));
			} else {
				for (ChannelConfig channelConfig : serverConfig.getChannelConfigs()) {
					if (sb.length() > 0) {
						sb.append(", ");
					}
					sb.append(channelConfig.getName());
				}
			}
			GuiLabel lblChannels = new GuiLabel(I19n.format("eirairc:gui.redirect.channels") + "\n" + sb.toString(), 0, centerY - 20, Globals.TEXT_COLOR);
			lblChannels.setHAlignment(GuiLabel.HAlignment.Center, width);
			labelList.add(lblChannels);
		}

		chkAlwaysAllow = new GuiCheckBox(0, centerX - 105, centerY + 30, I19n.format("eirairc:gui.redirect.trust"), false);
		buttonList.add(chkAlwaysAllow);

		btnAllow = new GuiButton(1, centerX + 5, centerY + 50, 100, 20, I19n.format("eirairc:gui.redirect.allow"));
		buttonList.add(btnAllow);

		btnReject = new GuiButton(2, centerX - 105, centerY + 50, 100, 20, I19n.format("eirairc:gui.redirect.reject"));
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
				ConfigurationHandler.saveTrustedServers();
			}
			ConnectionManager.redirectTo(serverConfig, false);
			mc.displayGuiScreen(null);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float par3) {
		drawLightBackground(menuX, menuY, menuWidth, menuHeight);

		super.drawScreen(mouseX, mouseY, par3);
	}
}
