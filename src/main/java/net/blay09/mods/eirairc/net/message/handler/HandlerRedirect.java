package net.blay09.mods.eirairc.net.message.handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.eirairc.net.message.MessageRedirect;

/**
 * Created by Christopher on 23.12.2014.
 */
public class HandlerRedirect implements IMessageHandler<MessageRedirect, IMessage> {

    @Override
    public IMessage onMessage(MessageRedirect message, MessageContext ctx) {

        // TODO show confirmation GUI

        return null;
    }
}
