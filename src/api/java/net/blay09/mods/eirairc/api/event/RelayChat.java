package net.blay09.mods.eirairc.api.event;

import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.eventhandler.Event;


public class RelayChat extends Event {

	public final IRCContext target;
	public final ICommandSender sender;
	public final String message;
	public final boolean isEmote;
	public final boolean isNotice;

	public RelayChat(ICommandSender sender, String message) {
		this(sender, message, false, false, null);
	}

	public RelayChat(ICommandSender sender, String message, boolean isEmote) {
		this(sender, message, isEmote, false, null);
	}

	public RelayChat(ICommandSender sender, String message, boolean isEmote, boolean isNotice) {
		this(sender, message, isEmote, isNotice, null);
	}

	public RelayChat(ICommandSender sender, String message, boolean isEmote, boolean isNotice, IRCContext target) {
		this.sender = sender;
		this.message = message;
		this.isEmote = isEmote;
		this.isNotice = isNotice;
		this.target = target;
	}

}
