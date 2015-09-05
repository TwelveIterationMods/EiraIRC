package net.blay09.mods.eirairc.wrapper;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class CommandSender implements ICommandSender {

    private final ICommandSender sender;

    public CommandSender(ICommandSender sender) {
        this.sender = sender;
    }

    @Override
    public String getCommandSenderName() {
        return sender.getCommandSenderName();
    }

    @Override
    public IChatComponent func_145748_c_() {
        return sender.func_145748_c_();
    }

    @Override
    public void addChatMessage(IChatComponent chatComponent) {
        sender.addChatMessage(chatComponent);
    }

    @Override
    public boolean canCommandSenderUseCommand(int level, String command) {
        return sender.canCommandSenderUseCommand(level, command);
    }

    @Override
    public ChunkCoordinates getPlayerCoordinates() {
        return sender.getPlayerCoordinates();
    }

    @Override
    public World getEntityWorld() {
        return sender.getEntityWorld();
    }

    public boolean isPlayer() {
        return sender instanceof EntityPlayer;
    }

    public EntityPlayer getAsPlayer() {
        return (EntityPlayer) sender;
    }
}
