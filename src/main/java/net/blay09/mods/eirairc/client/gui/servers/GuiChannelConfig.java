package net.blay09.mods.eirairc.client.gui.servers;

import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.GuiConfig;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCConfig;
import net.blay09.mods.eirairc.client.gui.base.GuiAdvancedTextField;
import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabContainer;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabPage;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.settings.GeneralBooleanComponent;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraftforge.common.config.ConfigElement;
import org.lwjgl.input.Keyboard;

/**
 * Created by Blay09 on 04.10.2014.
 */
public class GuiChannelConfig extends GuiTabPage implements GuiYesNoCallback {

	private final GuiServerConfig parent;
	private final ServerConfig serverConfig;
	private final ChannelConfig config;

	private GuiTextField txtName;
	private GuiAdvancedTextField txtPassword;
	private GuiCheckBox chkAutoJoin;

	private GuiButton btnTheme;
	private GuiButton btnBotSettings;
	private GuiButton btnOtherSettings;
	private GuiButton btnDelete;

	private boolean isNew;

	public GuiChannelConfig(GuiTabContainer tabContainer, GuiServerConfig parent) {
		super(tabContainer, parent);
		this.parent = parent;
		this.serverConfig = parent.getServerConfig();
		this.config = new ChannelConfig(serverConfig);
		isNew = true;
	}

	public GuiChannelConfig(GuiTabContainer tabContainer, GuiServerConfig parent, ChannelConfig config) {
		super(tabContainer, parent);
		this.parent = parent;
		this.serverConfig = parent.getServerConfig();
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

		labelList.add(new GuiLabel("Channel Name", leftX, topY, Globals.TEXT_COLOR));

		if(txtName != null) {
			oldText = txtName.getText();
		} else {
			oldText = config.getName();
		}
		txtName = new GuiTextField(fontRendererObj, leftX, topY + 15, 100, 15);
		txtName.setText(oldText);
		textFieldList.add(txtName);

		labelList.add(new GuiLabel("Channel Password", leftX, topY + 40, Globals.TEXT_COLOR));

		if(txtPassword != null) {
			oldText = txtPassword.getText();
		} else {
			oldText = config.getPassword();
		}
		txtPassword = new GuiAdvancedTextField(fontRendererObj, leftX, topY + 55, 100, 15);
		txtPassword.setText(oldText);
		txtPassword.setDefaultPasswordChar();
		textFieldList.add(txtPassword);

		boolean oldState;
		if(chkAutoJoin != null) {
			oldState = chkAutoJoin.isChecked();
		} else {
			oldState = config.getGeneralSettings().getBoolean(GeneralBooleanComponent.AutoJoin);
		}
		chkAutoJoin = new GuiCheckBox(4, leftX, topY + 60, " Auto Join", oldState);
		buttonList.add(chkAutoJoin);

		btnDelete = new GuiButton(0, rightX - 100, topY + 150, 100, 20, "Delete");
		btnDelete.packedFGColour = -65536;
		buttonList.add(btnDelete);

		labelList.add(new GuiLabel("Override Settings", rightX - 100, topY + 5, Globals.TEXT_COLOR));

		btnTheme = new GuiButton(1, rightX - 100, topY + 15, 100, 20, "Configure Theme...");
		buttonList.add(btnTheme);

		btnBotSettings = new GuiButton(2, rightX - 100, topY + 40, 100, 20, "Configure Bot...");
		buttonList.add(btnBotSettings);

		btnOtherSettings = new GuiButton(3, rightX - 100, topY + 65, 100, 20, "Other Settings...");
		buttonList.add(btnOtherSettings);
	}

	public boolean isNew() {
		return isNew;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void actionPerformed(GuiButton button) {
		if(button == btnTheme) {
			mc.displayGuiScreen(new GuiConfig(tabContainer, GuiEiraIRCConfig.getThemeConfigElements(config.getTheme().pullDummyConfig().getCategory("theme"), false), Globals.MOD_ID, "channel:" + config.getIdentifier(), false, false, "Theme (" + config.getName() + ")"));
		} else if(button == btnBotSettings) {
			mc.displayGuiScreen(new GuiConfig(tabContainer, new ConfigElement(config.getBotSettings().pullDummyConfig().getCategory("bot")).getChildElements(), Globals.MOD_ID, "channel:" + config.getIdentifier(), false, false, "Bot Settings (" + config.getName() + ")"));
		} else if(button == btnOtherSettings) {
			mc.displayGuiScreen(new GuiConfig(tabContainer, new ConfigElement(config.getGeneralSettings().pullDummyConfig().getCategory("settings")).getChildElements(), Globals.MOD_ID, "channel:" + config.getIdentifier(), false, false, "Other Settings (" + config.getName() + ")"));
		} else if(button == btnDelete) {
			if(isNew) {
				tabContainer.removePage(this);
				tabContainer.initGui();
			} else {
				mc.displayGuiScreen(new GuiYesNo(this, "Do you really want to delete this channel configuration?", "This can't be undone, so be careful!", 0));
			}
		}
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		if(result) {
			if(id == 0) {
				serverConfig.removeChannelConfig(config.getName());
				ConfigurationHandler.saveServers();
				tabContainer.setCurrentTab(parent, false);
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

	public void applyChanges() {
		if(!txtName.getText().isEmpty() && !txtName.getText().equals(config.getName())) {
			char prefix = txtName.getText().charAt(0);
			switch(prefix) {
				case '&':
				case '#':
				case '!':
				case '+':
				case '.':
				case '~':
					break;
				default:
					txtName.setText("#" + txtName.getText());
			}
			serverConfig.removeChannelConfig(config.getName());
			config.setName(txtName.getText());
			config.setPassword(txtPassword.getText());
			config.getGeneralSettings().setBoolean(GeneralBooleanComponent.AutoJoin, chkAutoJoin.isChecked());
			serverConfig.addChannelConfig(config);
			isNew = false;
		}
		ConfigurationHandler.saveServers();
	}
}
