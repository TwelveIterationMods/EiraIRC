package net.blay09.mods.eirairc.addon;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.blay09.mods.eirairc.api.event.IRCChannelChatEvent;
import net.blay09.mods.eirairc.api.event.IRCUserJoinEvent;
import net.blay09.mods.eirairc.api.event.IRCUserLeaveEvent;
import net.blay09.mods.eirairc.api.event.RelayChat;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;


@Optional.Interface(iface = "org.dynmap.DynmapCommonAPIListener", modid = "Dynmap")
public class DynmapWebChatAddon extends DynmapCommonAPIListener {

	public static class WebChatSender implements ICommandSender {
		public final String source;
		public final String name;

		public WebChatSender(String source, String name) {
			this.source = source;
			this.name = name;
		}

		@Override
		public String getCommandSenderName() {
			return "[" + source + "]" + ((name != null && !name.isEmpty()) ? " " + name : "");
		}

		@Override
		public IChatComponent getFormattedCommandSenderName() {
			return new ChatComponentText(this.getCommandSenderName());
		}

		@Override
		public void addChatMessage(IChatComponent p_145747_1_) {}

		@Override
		public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_) {
			return false;
		}

		@Override
		public ChunkCoordinates getCommandSenderPosition() {
			return new ChunkCoordinates(0, 0, 0);
		}

		@Override
		public World getEntityWorld() {
			return MinecraftServer.getServer().getEntityWorld();
		}
	}

	private DynmapCommonAPI api;

	public DynmapWebChatAddon() {
		DynmapCommonAPIListener.register(this);
	}

	@Override
	@Optional.Method(modid = "Dynmap")
	public void apiEnabled(DynmapCommonAPI api) {
		this.api = api;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	@Optional.Method(modid = "Dynmap")
	public void apiDisabled(DynmapCommonAPI api) {
		this.api = null;
		MinecraftForge.EVENT_BUS.unregister(this);
	}

	@SubscribeEvent
	@Optional.Method(modid = "Dynmap")
	public void onChannelChat(IRCChannelChatEvent event) {
		api.postPlayerMessageToWeb(event.sender.getName(), event.sender.getName(), event.message);
	}

	@SubscribeEvent
	@Optional.Method(modid = "Dynmap")
	public void onIRCUserJoin(IRCUserJoinEvent event) {
		api.postPlayerJoinQuitToWeb(event.user.getName(), event.user.getName(), true);
	}

	@SubscribeEvent
	@Optional.Method(modid = "Dynmap")
	public void onIRCUserLeave(IRCUserLeaveEvent event) {
		api.postPlayerJoinQuitToWeb(event.user.getName(), event.user.getName(), false);
	}

	@Optional.Method(modid = "Dynmap")
	@SubscribeEvent
	public void onIRCUserQuit(IRCUserLeaveEvent event) {
		api.postPlayerJoinQuitToWeb(event.user.getName(), event.user.getName(), false);
	}

	@Override
	@Optional.Method(modid = "Dynmap")
	public boolean webChatEvent(String source, String name, String message) {
		MinecraftForge.EVENT_BUS.post(new RelayChat(new WebChatSender(source, name), message));
		return true;
	}
}
