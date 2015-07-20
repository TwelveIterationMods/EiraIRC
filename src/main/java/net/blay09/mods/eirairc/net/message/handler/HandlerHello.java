// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.net.message.handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.net.EiraPlayerInfo;
import net.blay09.mods.eirairc.net.message.MessageHello;
import net.minecraft.entity.player.EntityPlayer;

public class HandlerHello implements IMessageHandler<MessageHello, IMessage> {

	@Override
	public IMessage onMessage(MessageHello packet, MessageContext ctx) {
		EntityPlayer entityPlayer = ctx.getServerHandler().playerEntity;
		
		EiraPlayerInfo playerInfo = EiraIRC.instance.getNetHandler().getPlayerInfo(entityPlayer.getCommandSenderName());
		playerInfo.modInstalled = true;
		playerInfo.modVersion = packet.getVersion();

		return null;
	}

}
