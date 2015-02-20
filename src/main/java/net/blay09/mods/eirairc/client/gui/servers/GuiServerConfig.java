package net.blay09.mods.eirairc.client.gui.servers;

import cpw.mods.fml.client.config.GuiConfig;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.client.gui.EiraGui;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCConfig;
import net.blay09.mods.eirairc.client.gui.base.*;
import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.client.gui.base.list.GuiList;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabContainer;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabPage;
import net.blay09.mods.eirairc.client.gui.overlay.OverlayYesNo;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraftforge.common.config.ConfigElement;
import org.lwjgl.input.Keyboard;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiServerConfig extends GuiTabPage implements GuiYesNoCallback {

	private ServerConfig config;
	private GuiTextField txtAddress;
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

	private boolean isNew;
	private ChannelConfig deleteChannel;

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

		int oldSelectedIdx = -1;
		if(lstChannels != null) {
			oldSelectedIdx = lstChannels.getSelectedIdx();
		}
		lstChannels = new GuiList<GuiListEntryChannel>(rightX - 100, topY + 15, 100, 80, 20);
		for(ChannelConfig channelConfig : config.getChannelConfigs()) {
			lstChannels.addEntry(new GuiListEntryChannel(this, fontRendererObj, channelConfig, lstChannels.getEntryHeight()));
		}
		lstChannels.setSelectedIdx(oldSelectedIdx);
		listList.add(lstChannels);

		btnChannelJoinLeave = new GuiImageButton(7, rightX - 95, topY + 100, EiraGui.atlas.findRegion("button_join"));
		buttonList.add(btnChannelJoinLeave);

		btnChannelAdd = new GuiImageButton(5, rightX - 75, topY + 100, EiraGui.atlas.findRegion("button_add"));
		buttonList.add(btnChannelAdd);

		btnChannelDelete = new GuiImageButton(6, rightX - 55, topY + 100, EiraGui.atlas.findRegion("button_remove"));
		buttonList.add(btnChannelDelete);

		btnDelete = new GuiButton(0, rightX - 100, topY + 150, 100, 20, "Delete");
		btnDelete.packedFGColour = -65536;
		buttonList.add(btnDelete);

		labelList.add(new GuiLabel("Override Settings", leftX, topY + 85, Globals.TEXT_COLOR));

		btnTheme = new GuiButton(1, leftX, topY + 95, 100, 20, "Configure Theme...");
		buttonList.add(btnTheme);

		btnBotSettings = new GuiButton(2, leftX, topY + 120, 100, 20, "Configure Bot...");
		buttonList.add(btnBotSettings);

		btnOtherSettings = new GuiButton(3, leftX, topY + 145, 100, 20, "Other Settings...");
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
		} else if(button == btnChannelAdd) {
			tabContainer.setCurrentTab(new GuiChannelConfig(tabContainer, this), false);
		} else if(button == btnChannelDelete) {
			if (lstChannels.hasSelection()) {
				deleteChannel = lstChannels.getSelectedItem().getConfig();
				setOverlay(new OverlayYesNo(this, "Do you really want to delete this channel configuration?", "This can't be undone, so be careful!", 1));
			}
		} else if(button == btnChannelJoinLeave) {
			applyChanges();
			if(lstChannels.hasSelection()) {
				IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(config.getAddress());
				if(connection == null) {
					connection = Utils.connectTo(config);
				}
				ChannelConfig channelConfig = lstChannels.getSelectedItem().getConfig();
				if(connection.getChannel(channelConfig.getName()) != null) {
					connection.part(channelConfig.getName());
					lstChannels.getSelectedItem().setJoined(false);
					setChannelJoinLeaveButtonState(false);
				} else {
					connection.join(channelConfig.getName(), channelConfig.getPassword());
					lstChannels.getSelectedItem().setJoined(true);
					setChannelJoinLeaveButtonState(true);
				}
			}
		} else if(button == btnDelete) {
			if(isNew) {
				tabContainer.removePage(this);
				tabContainer.initGui();
			} else {
				setOverlay(new OverlayYesNo(this, "Do you really want to delete this server configuration?", "This can't be undone, so be careful!", 0));
			}
		}
	}

	@Override
	public void keyTyped(char unicode, int keyCode) {
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

	private void setChannelJoinLeaveButtonState(boolean state) {
		if(state) {
			btnChannelJoinLeave.setTextureRegion(EiraGui.atlas.findRegion("button_part"));
		} else {
			btnChannelJoinLeave.setTextureRegion(EiraGui.atlas.findRegion("button_join"));
		}
	}

	public void channelSelected(ChannelConfig channelConfig) {
		IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(config.getAddress());
		if(connection != null && connection.getChannel(channelConfig.getName()) != null) {
			setChannelJoinLeaveButtonState(true);
		} else {
			setChannelJoinLeaveButtonState(false);
		}
	}

	public void channelClicked(ChannelConfig channelConfig) {
		tabContainer.setCurrentTab(new GuiChannelConfig(tabContainer, this, channelConfig), false);
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		if(result) {
			if(id == 0) {
				ConfigurationHandler.removeServerConfig(config.getAddress());
				ConfigurationHandler.saveServers();
				tabContainer.removePage(this);
			} else if(id == 1) {
				config.removeChannelConfig(deleteChannel.getName());
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
