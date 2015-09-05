package net.blay09.mods.eirairc.wrapper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.io.File;

public class MinecraftWrapper {

    @SideOnly(Side.CLIENT)
    public static File getDataDir() {
        return Minecraft.getMinecraft().mcDataDir;
    }

    @SideOnly(Side.CLIENT)
    public static int getDisplayWidth() {
        return Minecraft.getMinecraft().displayWidth;
    }

    @SideOnly(Side.CLIENT)
    public static int getDisplayHeight() {
        return Minecraft.getMinecraft().displayHeight;
    }

    @SideOnly(Side.CLIENT)
    public static EntityClientPlayerMP getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    @SideOnly(Side.CLIENT)
    public static World getClientWorld() {
        return Minecraft.getMinecraft().theWorld;
    }

    public static void executeCommand(ICommandSender sender, String command) {
        MinecraftServer.getServer().getCommandManager().executeCommand(sender, command);
    }
}
