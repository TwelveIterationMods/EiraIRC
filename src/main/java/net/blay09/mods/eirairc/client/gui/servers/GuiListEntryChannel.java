package net.blay09.mods.eirairc.client.gui.servers;

import net.blay09.mods.eirairc.client.gui.base.list.GuiListTextEntry;
import net.blay09.mods.eirairc.config.ChannelConfig;
import net.blay09.mods.eirairc.util.Globals;
import net.minecraft.client.gui.FontRenderer;

/**
 * Created by Blay09 on 10.10.2014.
 */
public class GuiListEntryChannel extends GuiListTextEntry {

	private final GuiServerConfig parent;
	private final ChannelConfig config;

	public GuiListEntryChannel(GuiServerConfig parent, FontRenderer fontRenderer, ChannelConfig config, int height) {
		super(fontRenderer, config.getName(), height, Globals.TEXT_COLOR);
		this.parent = parent;
		this.config = config;
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
}
