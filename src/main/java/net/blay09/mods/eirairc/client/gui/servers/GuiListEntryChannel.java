package net.blay09.mods.eirairc.client.gui.servers;

import net.blay09.mods.eirairc.EiraIRC;
import net.blay09.mods.eirairc.api.irc.IRCConnection;
import net.blay09.mods.eirairc.client.graphics.TextureRegion;
import net.blay09.mods.eirairc.client.gui.EiraGui;
import net.blay09.mods.eirairc.client.gui.base.list.GuiListTextEntry;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.util.Globals;
import net.minecraft.client.gui.FontRenderer;


public class GuiListEntryChannel extends GuiListTextEntry {

	private final GuiServerConfig parent;
	private final ChannelConfig config;
	private TextureRegion icon;

	public GuiListEntryChannel(GuiServerConfig parent, FontRenderer fontRenderer, ChannelConfig config, int height) {
		super(fontRenderer, config.getName(), height, Globals.TEXT_COLOR);
		this.parent = parent;
		this.config = config;

		IRCConnection connection = EiraIRC.instance.getConnectionManager().getConnection(parent.getServerConfig().getIdentifier());
		setJoined(connection != null && connection.getChannel(config.getName()) != null);
	}

	public ChannelConfig getConfig() {
		return config;
	}

	@Override
	public void setSelected(boolean selected) {
		super.setSelected(selected);

		if(selected) {
			parent.channelSelected(config);
		}
	}

	@Override
	public void onDoubleClick() {
		parent.channelClicked(config);
	}

	public void setJoined(boolean isJoined) {
		if(isJoined) {
			icon = EiraGui.atlas.findRegion("icon_active");
		} else {
			icon = null;
		}
	}

	@Override
	public void drawEntry(int x, int y) {
		super.drawEntry(x + 11, y);

		if(icon != null) {
			icon.draw(x + 3, y + height / 2 - 6);
		}
	}
}
