package net.blay09.mods.eirairc.client.gui.servers;

import net.blay09.mods.eirairc.ConnectionManager;
import net.blay09.mods.eirairc.api.event.IRCConnectEvent;
import net.blay09.mods.eirairc.api.event.IRCConnectionFailedEvent;
import net.blay09.mods.eirairc.api.event.IRCDisconnectEvent;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.client.gui.base.GuiAdvancedTextField;
import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabContainer;
import net.blay09.mods.eirairc.client.gui.base.tab.GuiTabPage;
import net.blay09.mods.eirairc.client.gui.overlay.OverlayYesNo;
import net.blay09.mods.eirairc.config.AuthManager;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.irc.IRCConnectionImpl;
import net.blay09.mods.eirairc.util.ConfigHelper;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.I19n;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.nio.charset.Charset;

public class GuiServerConfigAdvanced extends GuiTabPage implements GuiYesNoCallback {

    private final GuiServerConfig parent;
    private final ServerConfig config;

    private GuiAdvancedTextField txtAddress;
    private GuiAdvancedTextField txtNick;
    private GuiAdvancedTextField txtNickServName;
    private GuiAdvancedTextField txtNickServPassword;
    private GuiAdvancedTextField txtServerPassword;
    private GuiAdvancedTextField txtCharset;
    private GuiCheckBox chkSSL;
    private GuiCheckBox chkAutoConnect;
    private GuiButton btnBack;
    private GuiButton btnDelete;

    public GuiServerConfigAdvanced(GuiTabContainer tabContainer, GuiServerConfig parent) {
        super(tabContainer, parent);
        this.parent = parent;
        config = parent.getServerConfig();
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        MinecraftForge.EVENT_BUS.register(this);
        allowSideClickClose = false;

        final boolean isConnected = ConnectionManager.isConnectedTo(config.getIdentifier());
        final int leftX = width / 2 - 130;
        final int rightX = width / 2 + 130;
        final int topY = height / 2 - 80;
        String oldText;

        labelList.add(new GuiLabel(I19n.format("eirairc:gui.server.address"), leftX, topY, Globals.TEXT_COLOR));

        if (txtAddress != null) {
            oldText = txtAddress.getText();
        } else {
            oldText = config.getAddress();
        }
        txtAddress = new GuiAdvancedTextField(0, fontRendererObj, leftX, topY + 15, 100, 15);
        txtAddress.setEnabled(!isConnected);
        txtAddress.setText(oldText);
        textFieldList.add(txtAddress);

        labelList.add(new GuiLabel(I19n.format("eirairc:gui.server.nick"), leftX, topY + 40, Globals.TEXT_COLOR));

        if (txtNick != null) {
            oldText = txtNick.getText();
        } else {
            oldText = config.getNick();
        }
        txtNick = new GuiAdvancedTextField(1, fontRendererObj, leftX, topY + 55, 100, 15);
        txtNick.setDefaultText(Globals.DEFAULT_NICK, false);
        txtNick.setText(oldText);
        textFieldList.add(txtNick);

        labelList.add(new GuiLabel(I19n.format("eirairc:gui.server.nickServName"), leftX, topY + 80, Globals.TEXT_COLOR));

        AuthManager.NickServData nickServData = AuthManager.getNickServData(config.getIdentifier());
        if (txtNickServName != null) {
            oldText = txtNickServName.getText();
        } else {
            oldText = nickServData != null ? nickServData.username : "";
        }
        txtNickServName = new GuiAdvancedTextField(2, fontRendererObj, leftX, topY + 95, 100, 15);
        txtNickServName.setText(oldText);
        textFieldList.add(txtNickServName);

        labelList.add(new GuiLabel(I19n.format("eirairc:gui.server.nickServPassword"), leftX, topY + 120, Globals.TEXT_COLOR));

        if (txtNickServPassword != null) {
            oldText = txtNickServPassword.getText();
        } else {
            oldText = nickServData != null ? nickServData.password : "";
        }
        txtNickServPassword = new GuiAdvancedTextField(3, fontRendererObj, leftX, topY + 135, 100, 15);
        txtNickServPassword.setText(oldText);
        txtNickServPassword.setDefaultPasswordChar();
        textFieldList.add(txtNickServPassword);

        labelList.add(new GuiLabel(I19n.format("eirairc:gui.server.serverPassword"), rightX - 100, topY, Globals.TEXT_COLOR));

        if (txtServerPassword != null) {
            oldText = txtServerPassword.getText();
        } else {
            oldText = AuthManager.getServerPassword(config.getIdentifier());
        }
        txtServerPassword = new GuiAdvancedTextField(4, fontRendererObj, rightX - 100, topY + 15, 100, 15);
        txtServerPassword.setEnabled(!isConnected);
        txtServerPassword.setText(oldText);
        txtServerPassword.setEnabled(!isConnected);
        txtServerPassword.setDefaultPasswordChar();
        textFieldList.add(txtServerPassword);

        labelList.add(new GuiLabel(I19n.format("eirairc:gui.server.charset"), rightX - 100, topY + 40, Globals.TEXT_COLOR));

        if (txtCharset != null) {
            oldText = txtCharset.getText();
        } else {
            oldText = config.getCharset();
        }
        txtCharset = new GuiAdvancedTextField(5, fontRendererObj, rightX - 100, topY + 55, 100, 15);
        txtCharset.setEnabled(!isConnected);
        txtCharset.setDefaultText(Globals.DEFAULT_CHARSET, false);
        txtCharset.setEnabled(!isConnected);
        txtCharset.setText(oldText);
        textFieldList.add(txtCharset);

        txtAddress.setNextTabField(txtNick);
        txtNick.setNextTabField(txtNickServName);
        txtNickServName.setNextTabField(txtNickServPassword);
        txtNickServPassword.setNextTabField(txtServerPassword);
        txtServerPassword.setNextTabField(txtCharset);
        txtCharset.setNextTabField(txtAddress);

        boolean oldState;
        if (chkSSL != null) {
            oldState = chkSSL.isChecked();
        } else {
            oldState = config.isSSL();
        }
        chkSSL = new GuiCheckBox(2, rightX - 100, topY + 80, " " + I19n.format("eirairc:gui.server.useSSL"), oldState);
        chkSSL.enabled = !isConnected;
        buttonList.add(chkSSL);

        if (chkAutoConnect != null) {
            oldState = chkAutoConnect.isChecked();
        } else {
            oldState = config.getGeneralSettings().autoJoin.get();
        }
        chkAutoConnect = new GuiCheckBox(3, rightX - 100, topY + 100, " " + I19n.format("eirairc:gui.server.autoConnect"), oldState);
        buttonList.add(chkAutoConnect);

        btnDelete = new GuiButton(0, rightX - 100, topY + 150, 100, 20, I19n.format("eirairc:gui.delete"));
        btnDelete.packedFGColour = -65536;
        buttonList.add(btnDelete);

        btnBack = new GuiButton(1, rightX - 100, topY + 125, 100, 20, I19n.format("eirairc:gui.back"));
        buttonList.add(btnBack);
    }

    @Override
    public boolean requestClose() {
        if (!Charset.isSupported(txtCharset.getTextOrDefault())) {
            txtCharset.setFocused(true);
            txtCharset.setTextColor(-65536);
            return false;
        }
        return true;
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button == btnBack) {
            tabContainer.setCurrentTab(parent, false);
        } else if (button == btnDelete) {
            setOverlay(new OverlayYesNo(this, I19n.format("eirairc:gui.server.deleteConfirm"), I19n.format("eirairc:gui.server.deleteNoUndo"), 0));
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if (result) {
            ConfigurationHandler.removeServerConfig(config.getAddress());
            ConfigurationHandler.saveServers();
            tabContainer.removePage(this);
        }
    }

    @SubscribeEvent
    public void onConnect(IRCConnectEvent event) {
        if (event.connection.getIdentifier().equals(config.getIdentifier())) {
            txtAddress.setEnabled(false);
            txtServerPassword.setEnabled(false);
            txtCharset.setEnabled(false);
            chkSSL.enabled = false;
            txtAddress.setText(config.getAddress());
            txtServerPassword.setText(AuthManager.getServerPassword(config.getIdentifier()));
            txtCharset.setText(config.getCharset());
            chkSSL.setIsChecked(config.isSSL());
        }
    }

    @SubscribeEvent
    public void onDisconnect(IRCDisconnectEvent event) {
        if (event.connection.getIdentifier().equals(config.getIdentifier())) {
            txtAddress.setEnabled(true);
            txtServerPassword.setEnabled(true);
            txtCharset.setEnabled(true);
            chkSSL.enabled = true;
        }
    }

    @SubscribeEvent
    public void onConnectionFailed(IRCConnectionFailedEvent event) {
        if (event.connection.getIdentifier().equals(config.getIdentifier())) {
            txtAddress.setEnabled(true);
            txtServerPassword.setEnabled(true);
            txtCharset.setEnabled(true);
            chkSSL.enabled = true;
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        MinecraftForge.EVENT_BUS.unregister(this);
        if (!txtAddress.getText().isEmpty() && !txtAddress.getText().equals(config.getAddress())) {
            ConfigurationHandler.removeServerConfig(config.getAddress());
            config.setAddress(txtAddress.getText());
            ConfigurationHandler.addServerConfig(config);
        }

        config.setNick(txtNick.getTextOrDefault());
        // If connected, send nick change to IRC
        IRCConnection connection = ConnectionManager.getConnection(config.getIdentifier());
        if (connection != null && !connection.getNick().equals(config.getNick())) {
            connection.nick(ConfigHelper.formatNick(config.getNick()));
        }
        AuthManager.putNickServData(config.getIdentifier(), txtNickServName.getText(), txtNickServPassword.getText());
        // If connected, identify with nickserv
        if (connection != null) {
            ((IRCConnectionImpl) connection).nickServIdentify();
        }
        AuthManager.putServerPassword(config.getIdentifier(), txtServerPassword.getText());
        config.setIsSSL(chkSSL.isChecked());
        config.getGeneralSettings().autoJoin.set(chkAutoConnect.isChecked());
        config.setCharset(txtCharset.getTextOrDefault());
        ConfigurationHandler.saveServers();
        tabContainer.initGui();
    }

}
