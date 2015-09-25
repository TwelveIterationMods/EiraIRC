package net.blay09.mods.eirairc.api;

import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public interface IChatHandler {
    void addChatMessage(IChatComponent component, IRCContext source);
    void addChatMessage(ICommandSender receiver, IChatComponent component, IRCContext source);
}
