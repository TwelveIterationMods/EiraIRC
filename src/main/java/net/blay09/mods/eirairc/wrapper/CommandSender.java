package net.blay09.mods.eirairc.wrapper;

import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandSender implements ICommandSender {

    private final ICommandSender sender;

    public CommandSender(ICommandSender sender) {
        this.sender = sender;
    }

    @Override
    public String getName() {
        return sender.getName();
    }

    public String getCommandSenderName() {
        return getName();
    }

    @Override
    public IChatComponent getDisplayName() {
        return sender.getDisplayName();
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
    public BlockPos getPosition() {
        return sender.getPosition();
    }

    @Override
    public Vec3 getPositionVector() {
        return sender.getPositionVector();
    }

    @Override
    public Entity getCommandSenderEntity() {
        return sender.getCommandSenderEntity();
    }

    @Override
    public boolean sendCommandFeedback() {
        return sender.sendCommandFeedback();
    }

    @Override
    public void func_174794_a(CommandResultStats.Type statType, int i) {
        sender.func_174794_a(statType, i);
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
