package net.blay09.mods.eirairc.net.message.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.eirairc.client.gui.GuiEiraIRCRedirect;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.net.message.MessageRedirect;
import net.minecraft.client.Minecraft;

/**
 * Created by Christopher on 23.12.2014.
 */
public class HandlerRedirect implements IMessageHandler<MessageRedirect, IMessage> {

    @Override
    public IMessage onMessage(MessageRedirect message, MessageContext ctx) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(message.getRedirectConfig(), JsonObject.class);
        ServerConfig serverConfig = ServerConfig.loadFromJson(jsonObject);
        serverConfig.setIsRemote(true);
        // TODO check if always redirect
        Minecraft.getMinecraft().displayGuiScreen(new GuiEiraIRCRedirect(serverConfig));
        return null;
    }
}
