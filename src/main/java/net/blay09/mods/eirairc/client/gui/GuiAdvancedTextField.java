// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiAdvancedTextField extends GuiTextField {
	
	private static final char DEFAULT_PASSWORD_CHAR = '*';
	private static final int COLOR_ENABLED = 14737632;
    private static final int COLOR_DISABLED = 7368816;
	
    private final FontRenderer fontRenderer;
	private String defaultText;
	private char passwordChar = 0;
	private boolean textCentered;
	private int textOffsetX;
	private boolean enabled;
	
	public GuiAdvancedTextField(FontRenderer par1FontRenderer, int par2, int par3, int par4, int par5) {
		super(par1FontRenderer, par2, par3, par4, par5);
		this.fontRenderer = par1FontRenderer;
	}

	@Override
	public boolean textboxKeyTyped(char unicode, int keyCode) {
		boolean result = super.textboxKeyTyped(unicode, keyCode);
		if(textCentered) {
			int textWidth = fontRenderer.getStringWidth(getText());
			textOffsetX = getWidth() / 2 - textWidth / 2;
		}
		return result;
	}
	
	@Override
	public void setText(String text) {
		super.setText(text);
		if(textCentered) {
			int textWidth = fontRenderer.getStringWidth(text);
			textOffsetX = getWidth() / 2 - textWidth / 2;
		}
	}
	
	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		if(defaultText != null) {
			if(!focused) {
				if(getText().isEmpty() || getText().equals(defaultText)) {
					setText(defaultText);
					setTextColor(COLOR_DISABLED);
				}
			} else {
				if(getText().equals(defaultText)) {
					setText("");
				}
				setTextColor(COLOR_ENABLED);
			}
		}
	}
	
	@Override
	public void drawTextBox() {
		String oldText = getText();
		if(passwordChar > 0) {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < oldText.length(); i++) {
				sb.append(passwordChar);
			}
			setText(sb.toString());
		}
		super.drawTextBox();
		if(passwordChar > 0) {
			setText(oldText);
		}
	}
	
	/**
	 * Minecraft code makes this ridiculously annoying to do, so it doesn't work yet.
	 * @param textCentered
	 */
	public void setTextCentered(boolean textCentered) {
		this.textCentered = textCentered;
	}
	
	public boolean isTextCentered() {
		return textCentered;
	}
	
	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
		if(getText().isEmpty()) {
			setText(defaultText);
			setTextColor(COLOR_DISABLED);
		}
	}
	
	public String getDefaultText() {
		return defaultText;
	}
	
	public void setPasswordChar(char passwordChar) {
		this.passwordChar = passwordChar;
	}
	
	public char getPasswordChar() {
		return passwordChar;
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
}
