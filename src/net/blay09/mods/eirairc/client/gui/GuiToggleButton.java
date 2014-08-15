package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.config.BotProfileImpl;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiToggleButton extends GuiButton {

	private final String textKey;
	private boolean state;
	
	public GuiToggleButton(int id, int x, int y, String textKey) {
		this(id, x, y, 200, 20, textKey);
	}

	public GuiToggleButton(int id, int x, int y, int width, int height, String textKey) {
		super(id, x, y, width, height, textKey);
		this.textKey = textKey;
		updateText();
	}
	
	public void updateText() {
		this.displayString = Utils.getLocalizedMessage(textKey, Utils.getLocalizedMessage(state ? "irc.gui.yes" : "irc.gui.no"));
	}
	
	public void setState(boolean state) {
		this.state = state;
		updateText();
	}
	
	public boolean getState() {
		return state;
	}
	
	@Override
	public boolean mousePressed(Minecraft mc, int x, int y) {
		boolean result = super.mousePressed(mc, x, y);
		if(result) {
			setState(!state);
		}
		return result;
    }
}
