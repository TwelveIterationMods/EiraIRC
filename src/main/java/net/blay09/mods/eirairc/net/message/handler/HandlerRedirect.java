package net.blay09.mods.eirairc.net.message.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.eirairc.ConnectionManager;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCRedirect;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.TrustedServer;
import net.blay09.mods.eirairc.net.message.MessageRedirect;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;

public class HandlerRedirect implements IMessageHandler<MessageRedirect, IMessage> {

    @Override
    public IMessage onMessage(MessageRedirect message, MessageContext ctx) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(message.getRedirectConfig(), JsonObject.class);
        ServerConfig serverConfig = ServerConfig.loadFromJson(jsonObject);
        serverConfig.setIsRemote(true);

        TrustedServer server = ConfigurationHandler.getOrCreateTrustedServer(Utils.getServerAddress());
        if(server.isAllowRedirect()) {
            ConnectionManager.redirectTo(serverConfig, server.isRedirectSolo());
        } else {
            Minecraft.getMinecraft().displayGuiScreen(new GuiEiraIRCRedirect(serverConfig));
        }
        return null;
    }
}
