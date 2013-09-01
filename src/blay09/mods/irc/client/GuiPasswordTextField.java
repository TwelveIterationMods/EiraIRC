package blay09.mods.irc.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiPasswordTextField extends GuiTextField {
	
	private static final char DEFAULT_PASSWORD_CHAR = '*';

	private char passwordChar = DEFAULT_PASSWORD_CHAR;
	
	public GuiPasswordTextField(FontRenderer par1FontRenderer, int par2, int par3, int par4, int par5) {
		super(par1FontRenderer, par2, par3, par4, par5);
	}

	@Override
	public void drawTextBox() {
		String oldText = getText();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < oldText.length(); i++) {
			sb.append(passwordChar);
		}
		setText(sb.toString());
		super.drawTextBox();
		setText(oldText);
	}
	
	public void setPasswordChar(char passwordChar) {
		this.passwordChar = passwordChar;
	}
	
	public char getPasswordChar() {
		return passwordChar;
	}
}
