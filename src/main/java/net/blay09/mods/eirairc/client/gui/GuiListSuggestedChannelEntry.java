package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.api.IRCConnection;
import net.blay09.mods.eirairc.client.gui.base.list.GuiListEntry;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.ServerConfig;
import net.blay09.mods.eirairc.config.SuggestedChannel;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

/**
 * Created by Blay09 on 20.02.2015.
 */
public class GuiListSuggestedChannelEntry extends GuiListEntry {

	private static final int TEXT_MARGIN = 5;

	private final FontRenderer fontRenderer;
	private final SuggestedChannel channel;

	public GuiListSuggestedChannelEntry(FontRenderer fontRenderer, SuggestedChannel channel) {
		this.fontRenderer = fontRenderer;
		this.channel = channel;
	}

	@Override
	public void onDoubleClick() {
		super.onDoubleClick();
		ServerConfig serverConfig = ConfigurationHandler.getOrCreateServerConfig(channel.getServerName());
		serverConfig.getOrCreateChannelConfig(channel.getChannelName());
		IRCConnection connection = Utils.connectTo(serverConfig);
		connection.join(channel.getChannelName(), null);
		Minecraft.getMinecraft().displayGuiScreen(null);
	}

	@Override
	public void drawEntry(int x, int y) {
		int currentX = x + TEXT_MARGIN;
		int currentY = y + TEXT_MARGIN;
		String s = channel.getChannelName();
		fontRenderer.drawStringWithShadow(s, currentX, currentY, Globals.TEXT_COLOR);
		currentX += fontRenderer.getStringWidth(s) + TEXT_MARGIN;
		s = " \u00a7o(" + channel.getServerName() + ")";
		fontRenderer.drawString(s, currentX, currentY, Globals.TEXT_COLOR);
		currentX += fontRenderer.getStringWidth(s) + TEXT_MARGIN;
		if(channel.getScore() > 0 && channel.isRecommended()) {
			s = "\u00a72recommended";
			fontRenderer.drawStringWithShadow(s, x + parentList.getWidth() - fontRenderer.getStringWidth(s) - TEXT_MARGIN, currentY, Globals.TEXT_COLOR);
		}
		currentY += 15;
		currentX = x + TEXT_MARGIN + TEXT_MARGIN;
		if(!channel.getModpackName().isEmpty()) {
			s = channel.getModpackName();
			if(channel.getScore() == Integer.MIN_VALUE) {
				s = "\u00a74" + s;
			}
			fontRenderer.drawString(s, currentX, currentY, Globals.TEXT_COLOR);
		}
	}

}
