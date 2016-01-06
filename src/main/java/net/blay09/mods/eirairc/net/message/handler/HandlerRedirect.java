package net.blay09.mods.eirairc.net.message.handler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.net.message.MessageRedirect;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerRedirect implements IMessageHandler<MessageRedirect, IMessage> {

    @Override
    public IMessage onMessage(MessageRedirect message, MessageContext ctx) {
        EiraIRC.proxy.addScheduledTask(() -> {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(message.getRedirectConfig(), JsonObject.class);
            ServerConfig serverConfig = ServerConfig.loadFromJson(jsonObject);
            serverConfig.setIsRemote(true);

            EiraIRC.proxy.handleRedirect(serverConfig);
        });
        return null;
    }
}
