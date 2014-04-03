// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiDefaultTextField extends GuiTextField {
	
	private int enabledColor = 14737632;
    private int disabledColor = 7368816;
	
	private String defaultText = "";
	
	public GuiDefaultTextField(FontRenderer par1FontRenderer, int par2, int par3, int par4, int par5) {
		super(par1FontRenderer, par2, par3, par4, par5);
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		if(!focused) {
			if(getText().isEmpty() || getText().equals(defaultText)) {
				setText(defaultText);
				setTextColor(disabledColor);
			}
		} else {
			if(getText().equals(defaultText)) {
				setText("");
			}
			setTextColor(enabledColor);
		}
	}
	
	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
		if(getText().isEmpty()) {
			setText(defaultText);
			setTextColor(disabledColor);
		}
	}
	
	public String getDefaultText() {
		return defaultText;
	}
}
