// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.base;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiAdvancedTextField extends GuiTextField {
	
	public static final char DEFAULT_PASSWORD_CHAR = '*';
	private static final int COLOR_ENABLED = 14737632;
    private static final int COLOR_DISABLED = 7368816;
	
    private final FontRenderer fontRenderer;
	private String defaultText;
	private boolean defaultTextDisplayOnly;
	private char passwordChar = 0;
	private boolean textCentered;
	private int textOffsetX;
	private boolean enabled;
	private boolean emptyOnRightClick;

	public GuiAdvancedTextField(int id, FontRenderer fontRenderer, int par2, int par3, int par4, int par5) {
		super(id, fontRenderer, par2, par3, par4, par5);
		this.fontRenderer = fontRenderer;
		setMaxStringLength(Integer.MAX_VALUE);
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
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(emptyOnRightClick && button == 1) {
			setText("");
		}
		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		if(focused && isDefaultText()) {
			setText("");
		}
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		if(textCentered) {
			int textWidth = fontRenderer.getStringWidth(text);
			textOffsetX = getWidth() / 2 - textWidth / 2;
		}
	}

	private boolean isDefaultText() {
		return defaultText != null && (getText().isEmpty() || getText().equals(defaultText));
	}

	public String getTextOrDefault() {
		String text = super.getText();
		if(!defaultTextDisplayOnly && isDefaultText()) {
			return defaultText;
		}
		return text;
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
		} else if(!isFocused() && isDefaultText()) {
			setText(defaultText);
			setTextColor(COLOR_DISABLED);
		}
		super.drawTextBox();
		setText(oldText);
		if(!isFocused() && isDefaultText()) {
			setTextColor(COLOR_ENABLED);
		}
	}
	
	/**
	 * Minecraft code makes this ridiculously annoying to do, so it doesn't work yet.
	 */
	public void setTextCentered(boolean textCentered) {
		this.textCentered = textCentered;
	}
	
	public boolean isTextCentered() {
		return textCentered;
	}
	
	public void setDefaultText(String defaultText, boolean displayOnly) {
		this.defaultText = defaultText;
		this.defaultTextDisplayOnly = displayOnly;
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

	public void setDefaultPasswordChar() {
		setPasswordChar(DEFAULT_PASSWORD_CHAR);
	}

	public void setEmptyOnRightClick(boolean emptyOnRightClick) {
		this.emptyOnRightClick = emptyOnRightClick;
	}

	public boolean isEmptyOnRightClick() {
		return emptyOnRightClick;
	}
}
