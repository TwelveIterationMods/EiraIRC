// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.client.gui.servers;

import cpw.mods.fml.client.config.GuiCheckBox;
import cpw.mods.fml.client.config.GuiConfig;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCConfig;
import net.blay09.mods.eirairc.client.gui.base.GuiAdvancedTextField;
import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabContainer;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabPage;
import net.blay09.mods.eirairc.client.gui.overlay.OverlayYesNo;
import net.blay09.mods.eirairc.config.AuthManager;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.I19n;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraftforge.common.config.ConfigElement;
import org.lwjgl.input.Keyboard;

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
    private GuiButton btnOK;
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

        labelList.add(new GuiLabel(I19n.format("eirairc:gui.channel.name"), leftX, topY, Globals.TEXT_COLOR));

        if (txtName != null) {
            oldText = txtName.getText();
        } else {
            oldText = config.getName();
        }
        txtName = new GuiTextField(fontRendererObj, leftX, topY + 15, 100, 15);
        txtName.setText(oldText);
        textFieldList.add(txtName);

        labelList.add(new GuiLabel(I19n.format("eirairc:gui.channel.password"), leftX, topY + 40, Globals.TEXT_COLOR));

        if (txtPassword != null) {
            oldText = txtPassword.getText();
        } else {
            oldText = AuthManager.getChannelPassword(config.getIdentifier());
        }
        txtPassword = new GuiAdvancedTextField(fontRendererObj, leftX, topY + 55, 100, 15);
        txtPassword.setText(oldText);
        txtPassword.setDefaultPasswordChar();
        textFieldList.add(txtPassword);

        boolean oldState;
        if (chkAutoJoin != null) {
            oldState = chkAutoJoin.isChecked();
        } else {
            oldState = config.getGeneralSettings().autoJoin.get();
        }
        chkAutoJoin = new GuiCheckBox(4, leftX, topY + 75, " " + I19n.format("eirairc:gui.channel.autoJoin"), oldState);
        buttonList.add(chkAutoJoin);

        btnOK = new GuiButton(4, rightX - 100, topY + 150, 100, 20, I19n.format("eirairc:gui.save"));
        buttonList.add(btnOK);

        btnDelete = new GuiButton(0, leftX, topY + 150, 100, 20, I19n.format("eirairc:gui.delete"));
        btnDelete.packedFGColour = -65536;
        buttonList.add(btnDelete);

        labelList.add(new GuiLabel(I19n.format("eirairc:gui.override"), rightX - 100, topY + 5, Globals.TEXT_COLOR));

        btnTheme = new GuiButton(1, rightX - 100, topY + 15, 100, 20, I19n.format("eirairc:gui.override.theme"));
        buttonList.add(btnTheme);

        btnBotSettings = new GuiButton(2, rightX - 100, topY + 40, 100, 20, I19n.format("eirairc:gui.override.bot"));
        buttonList.add(btnBotSettings);

        btnOtherSettings = new GuiButton(3, rightX - 100, topY + 65, 100, 20, I19n.format("eirairc:gui.override.other"));
        buttonList.add(btnOtherSettings);
    }

    public boolean isNew() {
        return isNew;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void actionPerformed(GuiButton button) {
        if (button == btnTheme) {
            mc.displayGuiScreen(new GuiConfig(tabContainer, GuiEiraIRCConfig.getAllConfigElements(config.getTheme().pullDummyConfig()), Globals.MOD_ID, "channel:" + config.getIdentifier(), false, false, I19n.format("eirairc:gui.config.theme", config.getName())));
        } else if (button == btnBotSettings) {
            mc.displayGuiScreen(new GuiConfig(tabContainer, GuiEiraIRCConfig.getAllConfigElements(config.getBotSettings().pullDummyConfig()), Globals.MOD_ID, "channel:" + config.getIdentifier(), false, false, I19n.format("eirairc:gui.config.bot", config.getName())));
        } else if (button == btnOtherSettings) {
            mc.displayGuiScreen(new GuiConfig(tabContainer, GuiEiraIRCConfig.getAllConfigElements(config.getGeneralSettings().pullDummyConfig()), Globals.MOD_ID, "channel:" + config.getIdentifier(), false, false, I19n.format("eirairc:gui.config.other", config.getName())));
        } else if (button == btnDelete) {
            if (isNew) {
                tabContainer.removePage(this);
                tabContainer.initGui();
            } else {
                setOverlay(new OverlayYesNo(this, I19n.format("eirairc:gui.channel.deleteConfirm"), I19n.format("eirairc:gui.channel.deleteNoUndo"), 0));
            }
        } else if (button == btnOK) {
            gotoPrevious();
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        switch (id) {
            case 0:
                if (result) {
                    serverConfig.removeChannelConfig(config.getName());
                    ConfigurationHandler.saveServers();
                    tabContainer.setCurrentTab(parent, false);
                }
                break;
            case 1:
                if (result) {
                    serverConfig.getBotSettings().messageFormat.set("Classic");
                    ConfigurationHandler.saveServers();
                    tabContainer.setCurrentTab(parent, false);
                } else {
                    tabContainer.setCurrentTab(parent, false);
                }
                break;
        }
    }

    @Override
    public boolean requestClose() {
        if (overlay == null && serverConfig.getChannelConfigs().size() >= 2 && !serverConfig.getBotSettings().getMessageFormat().mcChannelMessage.contains("{CHANNEL}")) {
            setOverlay((new OverlayYesNo(this, I19n.format("eirairc:gui.channel.multiChannel"), I19n.format("eirairc:gui.channel.suggestClassic"), 1)));
            return false;
        }
        return true;
    }


    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        applyChanges();
    }

    public void applyChanges() {
        if (!txtName.getText().isEmpty() && !txtName.getText().equals(config.getName())) {
            if (Character.isAlphabetic(txtName.getText().charAt(0))) {
                txtName.setText("#" + txtName.getText());
            }
            serverConfig.removeChannelConfig(config.getName());
            config.setName(txtName.getText());
            AuthManager.putChannelPassword(config.getIdentifier(), txtPassword.getText());
            config.getGeneralSettings().autoJoin.set(chkAutoJoin.isChecked());
            serverConfig.addChannelConfig(config);
            isNew = false;
        }
        ConfigurationHandler.saveServers();
    }
}
