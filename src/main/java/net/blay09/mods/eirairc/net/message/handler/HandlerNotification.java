// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.net.message.handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.net.message.MessageNotification;
import net.blay09.mods.eirairc.util.NotificationType;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerNotification implements IMessageHandler<MessageNotification, IMessage> {

	@Override
	public IMessage onMessage(MessageNotification message, MessageContext ctx) {
		EiraIRC.proxy.publishNotification(NotificationType.fromId(message.getNotificationType()), message.getText());
		return null;
	}

}
