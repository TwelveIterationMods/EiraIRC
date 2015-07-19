package net.blay09.mods.eirairc.api;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.IChatComponent;

public interface IChatHandler {
    void addChatMessage(IChatComponent component);
    void addChatMessage(ICommandSender receiver, IChatComponent component);
}
