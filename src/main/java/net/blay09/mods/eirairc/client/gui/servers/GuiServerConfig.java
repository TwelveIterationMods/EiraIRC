package net.blay09.mods.eirairc.client.gui.servers;

import net.blay09.mods.eirairc.ConnectionManager;
import net.blay09.mods.eirairc.api.event.*;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.client.gui.EiraGui;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCConfig;
import net.blay09.mods.eirairc.client.gui.base.GuiAdvancedTextField;
import net.blay09.mods.eirairc.client.gui.base.GuiImageButton;
import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.client.gui.base.list.GuiList;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabContainer;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabPage;
import net.blay09.mods.eirairc.client.gui.overlay.OverlayYesNo;
import net.blay09.mods.eirairc.config.AuthManager;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.I19n;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiServerConfig extends GuiTabPage implements GuiYesNoCallback {

	private final ServerConfig config;
	private GuiAdvancedTextField txtAddress;
	private GuiAdvancedTextField txtNick;
	private GuiList<GuiListEntryChannel> lstChannels;
	private GuiImageButton btnChannelAdd;
	private GuiImageButton btnChannelDelete;
	private GuiImageButton btnChannelJoinLeave;
	private GuiButton btnTheme;
	private GuiButton btnBotSettings;
	private GuiButton btnOtherSettings;
	private GuiButton btnAdvanced;
	private GuiButton btnDelete;
	private GuiButton btnConnect;

	private boolean isNew;
	private ChannelConfig deleteChannel;

	public GuiServerConfig(GuiTabContainer tabContainer) {
		super(tabContainer, I19n.format("eirairc:gui.server.new"));
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
		title = config.getAddress().isEmpty() ? I19n.format("eirairc:gui.server.new") : config.getAddress();

		final boolean isConnected = ConnectionManager.isConnectedTo(config.getIdentifier());
		final int leftX = width / 2 - 130;
		final int rightX = width / 2 + 130;
		final int topY = height / 2 - 80;
		String oldText;

		labelList.add(new GuiLabel(I19n.format("eirairc:gui.server.address"), leftX, topY, Globals.TEXT_COLOR));

		if(txtAddress != null) {
			oldText = txtAddress.getText();
		} else {
			oldText = config.getAddress();
		}
		txtAddress = new GuiAdvancedTextField(0, fontRendererObj, leftX, topY + 15, 100, 15);
		txtAddress.setEnabled(!isConnected);
		txtAddress.setText(oldText);
		textFieldList.add(txtAddress);

		labelList.add(new GuiLabel(I19n.format("eirairc:gui.server.nick"), leftX, topY + 40, Globals.TEXT_COLOR));

		if(txtNick != null) {
			oldText = txtNick.getText();
		} else {
			oldText = config.getNick();
		}
		txtNick = new GuiAdvancedTextField(0, fontRendererObj, leftX, topY + 55, 100, 15);
		txtNick.setDefaultText(Globals.DEFAULT_NICK, false);
		txtNick.setText(oldText);
		textFieldList.add(txtNick);

		txtAddress.setNextTabField(txtNick);
		txtNick.setNextTabField(txtAddress);

		btnConnect = new GuiButton(8, rightX - 100, topY, 100, 20, "");
		if(isConnected) {
			btnConnect.displayString = I19n.format("eirairc:gui.server.disconnect");
		} else {
			btnConnect.displayString = I19n.format("eirairc:gui.server.connect");
		}
		buttonList.add(btnConnect);

		labelList.add(new GuiLabel(I19n.format("eirairc:gui.server.channels"), rightX - 100, topY + 25, Globals.TEXT_COLOR));

		int oldSelectedIdx = -1;
		if(lstChannels != null) {
			oldSelectedIdx = lstChannels.getSelectedIdx();
		}
		lstChannels = new GuiList<>(this, rightX - 100, topY + 35, 100, 60, 20);
		for(ChannelConfig channelConfig : config.getChannelConfigs()) {
			lstChannels.addEntry(new GuiListEntryChannel(this, fontRendererObj, channelConfig, lstChannels.getEntryHeight()));
		}
		if(oldSelectedIdx < lstChannels.getEntries().size()) {
			lstChannels.setSelectedIdx(oldSelectedIdx);
		}
		listList.add(lstChannels);

		btnChannelJoinLeave = new GuiImageButton(7, rightX - 95, topY + 100, EiraGui.atlas.findRegion("button_join"));
		buttonList.add(btnChannelJoinLeave);

		btnChannelAdd = new GuiImageButton(5, rightX - 75, topY + 100, EiraGui.atlas.findRegion("button_add"));
		buttonList.add(btnChannelAdd);

		btnChannelDelete = new GuiImageButton(6, rightX - 55, topY + 100, EiraGui.atlas.findRegion("button_remove"));
		buttonList.add(btnChannelDelete);

		btnDelete = new GuiButton(0, rightX - 100, topY + 150, 100, 20, I19n.format("eirairc:gui.delete"));
		btnDelete.packedFGColour = -65536;
		buttonList.add(btnDelete);

		labelList.add(new GuiLabel(I19n.format("eirairc:gui.override"), leftX, topY + 85, Globals.TEXT_COLOR));

		btnTheme = new GuiButton(1, leftX, topY + 95, 100, 20, I19n.format("eirairc:gui.override.theme"));
		buttonList.add(btnTheme);

		btnBotSettings = new GuiButton(2, leftX, topY + 120, 100, 20, I19n.format("eirairc:gui.override.bot"));
		buttonList.add(btnBotSettings);

		btnOtherSettings = new GuiButton(3, leftX, topY + 145, 100, 20, I19n.format("eirairc:gui.override.other"));
		buttonList.add(btnOtherSettings);

		btnAdvanced = new GuiButton(4, rightX - 100, topY + 125, 100, 20, I19n.format("eirairc:gui.server.advanced"));
		buttonList.add(btnAdvanced);

		MinecraftForge.EVENT_BUS.register(this);
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
			mc.displayGuiScreen(new GuiConfig(tabContainer, GuiEiraIRCConfig.getAllConfigElements(config.getTheme().pullDummyConfig()), Globals.MOD_ID, "server:" + config.getAddress(), false, false, I19n.format("eirairc:gui.config.theme", config.getAddress())));
		} else if(button == btnBotSettings) {
			mc.displayGuiScreen(new GuiConfig(tabContainer, GuiEiraIRCConfig.getAllConfigElements(config.getBotSettings().pullDummyConfig()), Globals.MOD_ID, "server:" + config.getAddress(), false, false, I19n.format("eirairc:gui.config.bot", config.getAddress())));
		} else if(button == btnOtherSettings) {
			mc.displayGuiScreen(new GuiConfig(tabContainer, GuiEiraIRCConfig.getAllConfigElements(config.getGeneralSettings().pullDummyConfig()), Globals.MOD_ID, "server:" + config.getAddress(), false, false, I19n.format("eirairc:gui.config.other", config.getAddress())));
		} else if(button == btnAdvanced) {
			tabContainer.setCurrentTab(new GuiServerConfigAdvanced(tabContainer, this), false);
		} else if(button == btnChannelAdd) {
			tabContainer.setCurrentTab(new GuiChannelConfig(tabContainer, this), false);
		} else if(button == btnChannelDelete) {
			if (lstChannels.hasSelection()) {
				deleteChannel = lstChannels.getSelectedItem().getConfig();
				setOverlay(new OverlayYesNo(this, I19n.format("eirairc:gui.channel.deleteConfirm"), I19n.format("eirairc:gui.channel.deleteNoUndo"), 1));
			}
		} else if(button == btnChannelJoinLeave) {
			applyChanges();
			if(lstChannels.hasSelection()) {
				IRCConnection connection = ConnectionManager.getConnection(config.getIdentifier());
				if(connection == null) {
					connection = ConnectionManager.connectTo(config);
				}
				if(connection != null) {
					btnChannelJoinLeave.enabled = false;
					ChannelConfig channelConfig = lstChannels.getSelectedItem().getConfig();
					if (connection.getChannel(channelConfig.getName()) != null) {
						connection.part(channelConfig.getName());
					} else {
						connection.join(channelConfig.getName(), AuthManager.getChannelPassword(channelConfig.getIdentifier()));
					}
				}
			}
		} else if(button == btnDelete) {
			if(isNew) {
				tabContainer.removePage(this);
				tabContainer.initGui();
			} else {
				setOverlay(new OverlayYesNo(this, I19n.format("eirairc:gui.server.deleteConfirm"), I19n.format("eirairc:gui.server.deleteNoUndo"), 0));
			}
		} else if(button == btnConnect) {
			IRCConnection connection = ConnectionManager.getConnection(config.getIdentifier());
			if(connection != null) {
				btnConnect.enabled = false;
				btnConnect.displayString = I19n.format("eirairc:gui.server.disconnecting");
				connection.disconnect("");
			} else {
				btnConnect.enabled = false;
				btnConnect.displayString = I19n.format("eirairc:gui.server.connecting");
				ConnectionManager.connectTo(config);
			}
		}
	}

	@SubscribeEvent
	public void onChannelJoined(IRCChannelJoinedEvent event) {
		for(GuiListEntryChannel entry : lstChannels.getEntries()) {
			if(entry.getConfig().getIdentifier().equals(event.channel.getIdentifier())) {
				entry.setJoined(true);
			}
		}
		updateButtonStates();
	}

	@SubscribeEvent
	public void onChannelLeft(IRCChannelLeftEvent event) {
		for(GuiListEntryChannel entry : lstChannels.getEntries()) {
			if(entry.getConfig().getIdentifier().equals(event.channel.getIdentifier())) {
				entry.setJoined(false);
			}
		}
		updateButtonStates();
	}

	@SubscribeEvent
	public void onDisconnect(IRCDisconnectEvent event) {
		if(event.connection.getIdentifier().equals(config.getIdentifier())) {
			txtAddress.setEnabled(true);
			btnConnect.enabled = true;
			btnConnect.displayString = I19n.format("eirairc:gui.server.connect");
			for(GuiListEntryChannel entry : lstChannels.getEntries()) {
				entry.setJoined(false);
			}
			updateButtonStates();
		}
	}

	@SubscribeEvent
	public void onConnect(IRCConnectEvent event) {
		if(event.connection.getIdentifier().equals(config.getIdentifier())) {
			txtAddress.setEnabled(false);
			txtAddress.setText(config.getAddress());
			btnConnect.enabled = true;
			btnConnect.displayString = I19n.format("eirairc:gui.server.disconnect");
		}
	}

	@SubscribeEvent
	public void onConnectionFailed(IRCConnectionFailedEvent event) {
		if(event.connection.getIdentifier().equals(config.getIdentifier())) {
			txtAddress.setEnabled(true);
			btnConnect.enabled = true;
			btnConnect.displayString = I19n.format("eirairc:gui.server.connect");
		}
	}

	@Override
	public void keyTyped(char unicode, int keyCode) throws IOException {
		super.keyTyped(unicode, keyCode);
		if(txtAddress.isFocused()) {
			boolean enabled = txtAddress.getText().length() > 0;
			btnBotSettings.enabled = enabled;
			btnAdvanced.enabled = enabled;
			btnDelete.enabled = enabled;
			btnOtherSettings.enabled = enabled;
			btnTheme.enabled = enabled;
			btnChannelAdd.enabled = enabled;
			btnChannelDelete.enabled = enabled;
			btnChannelJoinLeave.enabled = enabled;
		}
	}

	private void updateButtonStates() {
		if(lstChannels.hasSelection()) {
			IRCConnection connection = ConnectionManager.getConnection(config.getIdentifier());
			ChannelConfig channelConfig = lstChannels.getSelectedItem().getConfig();
			if(connection != null && channelConfig != null && connection.getChannel(channelConfig.getName()) != null) {
				btnChannelJoinLeave.setTextureRegion(EiraGui.atlas.findRegion("button_part"));
			} else {
				btnChannelJoinLeave.setTextureRegion(EiraGui.atlas.findRegion("button_join"));
			}
			btnChannelDelete.enabled = true;
			btnChannelJoinLeave.enabled = true;
		} else {
			btnChannelDelete.enabled = false;
			btnChannelJoinLeave.enabled = false;
			btnChannelJoinLeave.setTextureRegion(EiraGui.atlas.findRegion("button_join"));
		}
	}

	public void channelSelected(ChannelConfig channelConfig) {
		updateButtonStates();
	}

	public void channelClicked(ChannelConfig channelConfig) {
		tabContainer.setCurrentTab(new GuiChannelConfig(tabContainer, this, channelConfig), false);
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		if(result) {
			if(id == 0) {
				ServerConfig serverConfig = ConfigurationHandler.removeServerConfig(config.getAddress());
				if(serverConfig != null) {
					IRCConnection connection = ConnectionManager.getConnection(serverConfig.getIdentifier());
					if(connection != null) {
						connection.disconnect("");
					}
				}
				ConfigurationHandler.saveServers();
				tabContainer.removePage(this);
			} else if(id == 1) {
				ChannelConfig channelConfig = config.removeChannelConfig(deleteChannel.getName());
				if(channelConfig != null) {
					IRCConnection connection = ConnectionManager.getConnection(channelConfig.getServerConfig().getIdentifier());
					if(connection != null) {
						connection.part(channelConfig.getName());
					}
				}
				ConfigurationHandler.saveServers();
			}
		}
		Minecraft.getMinecraft().displayGuiScreen(tabContainer);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
		applyChanges();
		MinecraftForge.EVENT_BUS.unregister(this);
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
		// If connected, send nick change to IRC
		IRCConnection connection = ConnectionManager.getConnection(config.getIdentifier());
		if(connection != null && !connection.getNick().equals(config.getNick())) {
			connection.nick(ConfigHelper.formatNick(config.getNick()));
		}
		ConfigurationHandler.saveServers();
		title = config.getAddress();
		tabContainer.initGui();
	}
}
