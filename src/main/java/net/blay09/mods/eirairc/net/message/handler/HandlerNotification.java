// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.net.message.handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.net.message.MessageNotification;
import net.blay09.mods.eirairc.config.property.NotificationType;

public class HandlerNotification implements IMessageHandler<MessageNotification, IMessage> {

	@Override
	public IMessage onMessage(MessageNotification message, MessageContext ctx) {
		EiraIRC.proxy.publishNotification(NotificationType.fromId(message.getNotificationType()), message.getText());
		return null;
	}

}
