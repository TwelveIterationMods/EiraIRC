// Copyright (c) 2014, Christopher "blay09" Baker
// All rights reserved.

package net.blay09.mods.eirairc.client.gui.settings;

import java.util.List;

import net.blay09.mods.eirairc.client.gui.base.GuiAdvancedTextField;
import net.blay09.mods.eirairc.client.gui.base.GuiToggleButton;
import net.blay09.mods.eirairc.config.base.BotProfileImpl;
import net.blay09.mods.eirairc.config.base.MessageFormatConfig;
import net.blay09.mods.eirairc.handler.ConfigurationHandler;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

public class GuiBotProfiles extends GuiScreen {

	private static final int BUTTON_WIDTH = 170;
	private static final int BUTTON_HEIGHT = 20;
	
	private class GuiBotProfileToggleButton extends GuiToggleButton {

		private String option;
		
		public GuiBotProfileToggleButton(int id, int x, int y, String textKey, String option) {
			super(id, x, y, textKey);
			this.option = option;
		}

		public GuiBotProfileToggleButton(int id, int x, int y, int width, int height, String textKey, String option) {
			super(id, x, y, width, height, textKey);
			this.option = option;
		}
		
		@Override
		public void setState(boolean state) {
			super.setState(state);
		}
		
	}
	
	private GuiButton btnPrevProfile;
	private GuiButton btnCurrentProfile;
	private GuiAdvancedTextField txtCurrentProfile;
	private GuiButton btnNextProfile;
	private GuiButton btnNewProfile;
	private GuiButton btnDeleteProfile;
	private GuiButton btnCopyProfile;
	
	private GuiButton btnMuted;
	private GuiButton btnReadOnly;
	private GuiButton btnDisplayFormat;
	
	private GuiButton btnCommands;
	private GuiButton btnBack;
	
	private final GuiScreen parentScreen;
	private final String customName;
	private final List<BotProfileImpl> profileList;
	private BotProfileImpl currentProfile;
	private int currentIdx;
	
	private final List<MessageFormatConfig> displayFormatList;
	private int currentDisplayFormatIdx;
	
	public GuiBotProfiles(GuiScreen parentScreen) {
		this(parentScreen, "custom");
	}
	
	public GuiBotProfiles(GuiScreen parentScreen, String customName) {
		this(parentScreen, customName, !ConfigurationHandler.getBotProfiles().isEmpty() ? ConfigurationHandler.getBotProfiles().get(0) : null);
	}
	
	public GuiBotProfiles(GuiScreen parentScreen, String customName, BotProfileImpl initialProfile) {
		this.parentScreen = parentScreen;
		this.customName = customName;
		displayFormatList = ConfigurationHandler.getDisplayFormats();
		profileList = ConfigurationHandler.getBotProfiles();
		if(initialProfile != null) {
			for(int i = 0; i < profileList.size(); i++) {
				if(profileList.get(i) == initialProfile) {
					currentIdx = i;
					currentProfile = initialProfile;
					break;
				}
			}
		}
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		int leftX = width /  2 - 172;
		int rightX = width / 2 + 2;
		int topY = height / 2 - 35;
		
		// Profile Browser
		
		btnCurrentProfile = new GuiButton(1, width / 2 - 50, height / 2 - 90, 100, BUTTON_HEIGHT, "");
		btnCurrentProfile.enabled = true;
		buttonList.add(btnCurrentProfile);
		
		txtCurrentProfile = new GuiAdvancedTextField(fontRendererObj, btnCurrentProfile.xPosition + 5, btnCurrentProfile.yPosition + BUTTON_HEIGHT / 2 - fontRendererObj.FONT_HEIGHT / 2, 100, BUTTON_HEIGHT);
		txtCurrentProfile.setEnableBackgroundDrawing(false);
		txtCurrentProfile.setTextCentered(true);
		txtCurrentProfile.setEnabled(false);
		
		btnPrevProfile = new GuiButton(2, width / 2 - 72, height / 2 - 90, 20, BUTTON_HEIGHT, "<");
		buttonList.add(btnPrevProfile);
		
		btnNextProfile = new GuiButton(3, width / 2 + 52, height / 2 - 90, 20, BUTTON_HEIGHT, ">");
		buttonList.add(btnNextProfile);
		
		btnNewProfile = new GuiButton(4, width / 2 + 74, height / 2 - 90, 20, BUTTON_HEIGHT, "+");
		buttonList.add(btnNewProfile);
		
		btnDeleteProfile = new GuiButton(5, width / 2 - 94, height / 2 - 90, 20, BUTTON_HEIGHT, "-");
		buttonList.add(btnDeleteProfile);
		
		btnCopyProfile = new GuiButton(6, width / 2 + 96, height / 2 - 90, 20, BUTTON_HEIGHT, "*");
		buttonList.add(btnCopyProfile);
		
		// Other Options
		
		btnMuted = new GuiButton(17, leftX, topY - 25, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnMuted);
		
		btnReadOnly = new GuiButton(18, rightX, topY - 25, BUTTON_WIDTH, BUTTON_HEIGHT, "");
		buttonList.add(btnReadOnly);
		
		btnDisplayFormat = new GuiButton(15, rightX, topY + 75, BUTTON_WIDTH, BUTTON_HEIGHT, "Display Format: ???");
		buttonList.add(btnDisplayFormat);
		
		btnCommands = new GuiButton(16, rightX, topY + 100, BUTTON_WIDTH, BUTTON_HEIGHT, "Configure Commands...");
		buttonList.add(btnCommands);
		
		btnBack = new GuiButton(0, width / 2 - 100, topY + 130, 200, 20, Utils.getLocalizedMessage("irc.gui.back"));
		buttonList.add(btnBack);
		
		if(currentProfile != null) {
			loadFromProfile(currentProfile);
		}
	}
	
	private void updateButtonText() {
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == btnBack) {
			if(parentScreen instanceof GuiChannelConfig) {
				((GuiChannelConfig) parentScreen).setBotProfile(currentProfile.getName());
			} else if(parentScreen instanceof GuiServerConfig) {
				((GuiServerConfig) parentScreen).setBotProfile(currentProfile.getName());
			}
			Minecraft.getMinecraft().displayGuiScreen(parentScreen);
			return;
		} else if(button == btnCurrentProfile) {
			enableNameEdit(true);
		} else if(button == btnPrevProfile) {
			if(profileList.isEmpty()) {
				return;
			}
			nextProfile(-1);
		} else if(button == btnNextProfile) {
			if(profileList.isEmpty()) {
				return;
			}
			nextProfile(1);
		} else if(button == btnNewProfile) {
			currentIdx = profileList.size();
			currentProfile = new BotProfileImpl(ConfigurationHandler.getBotProfileDir(), customName);
			ConfigurationHandler.addBotProfile(currentProfile);
			loadFromProfile(currentProfile);
		} else if(button == btnDeleteProfile) {
			if(profileList.isEmpty()) {
				return;
			}
			if(currentProfile.isDefaultProfile()) {
				return;
			}
			ConfigurationHandler.removeBotProfile(currentProfile);
			ConfigurationHandler.findDefaultBotProfile();
			nextProfile(-1);
			return;
		} else if(button == btnCopyProfile) {
			currentIdx = profileList.size();
			currentProfile = new BotProfileImpl(currentProfile, currentProfile.getName() + "_copy");
			ConfigurationHandler.addBotProfile(currentProfile);
			loadFromProfile(currentProfile);
		} else if(button == btnDisplayFormat) {
			currentDisplayFormatIdx++;
			if(currentDisplayFormatIdx >= displayFormatList.size()) {
				currentDisplayFormatIdx = 0;
			}
		} else if(button == btnCommands) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiBotCommands(this, currentProfile));
			return;
		} else if(button == btnMuted) {
		} else if(button == btnReadOnly) {
		} else if(currentProfile != null && currentProfile.isDefaultProfile()) {
			currentIdx = profileList.size();
			currentProfile = new BotProfileImpl(currentProfile, currentProfile.getName() + "_copy");
			ConfigurationHandler.addBotProfile(currentProfile);
			loadFromProfile(currentProfile);
			actionPerformed(button);
			return;
		} else if(currentProfile == null) {
			return;
		}
		if(currentProfile != null) {
			currentProfile.save();
		}
		updateButtonText();
	}
	
	private void nextProfile(int dir) {
		if(profileList.isEmpty()) {
			return;
		}
		currentIdx += dir;
		if(currentIdx < 0) {
			currentIdx = profileList.size() - 1;
		} else if(currentIdx >= profileList.size()) {
			currentIdx = 0;
		}
		currentProfile = profileList.get(currentIdx);
		loadFromProfile(currentProfile);
	}
	
	public void loadFromProfile(BotProfileImpl profile) {
		btnCurrentProfile.displayString = profile.getName();
		btnCurrentProfile.enabled = !profile.isDefaultProfile();
		btnDeleteProfile.enabled = !profile.isDefaultProfile();
		for(int i = 0; i < displayFormatList.size(); i++) {
		}
		updateButtonText();
	}
	
	public void enableNameEdit(boolean enabled) {
		if(currentProfile.isDefaultProfile()) {
			return;
		}
		if(enabled && btnCurrentProfile.enabled) {
			txtCurrentProfile.setText(currentProfile.getName());
			txtCurrentProfile.setEnabled(true);
			btnCurrentProfile.displayString = "";
			btnCurrentProfile.enabled = false;
		} else if(!btnCurrentProfile.enabled) {
			ConfigurationHandler.renameBotProfile(currentProfile, txtCurrentProfile.getText());
			btnCurrentProfile.displayString = currentProfile.getName();
			btnCurrentProfile.enabled = true;
			txtCurrentProfile.setEnabled(false);
		}
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		txtCurrentProfile.updateCursorCounter();
	}
	
	@Override
	public void mouseClicked(int par1, int par2, int par3) {
		txtCurrentProfile.mouseClicked(par1, par2, par3);
		if(txtCurrentProfile.isEnabled() && !txtCurrentProfile.isFocused()) {
			enableNameEdit(false);
		}
		super.mouseClicked(par1, par2, par3);
	}
	
	@Override
	public void keyTyped(char unicode, int keyCode) {
		super.keyTyped(unicode, keyCode);
		if(keyCode == Keyboard.KEY_RETURN) {
			enableNameEdit(false);
			return;
		}
		if(txtCurrentProfile.textboxKeyTyped(unicode, keyCode)) {
			return;
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawBackground(0);
		this.drawCenteredString(fontRendererObj, Utils.getLocalizedMessage("irc.gui.botSettings"), width / 2, height / 2 - 110, Globals.TEXT_COLOR);
		super.drawScreen(par1, par2, par3);
		if(txtCurrentProfile.isEnabled()) {
			txtCurrentProfile.drawTextBox();
		}
	}
}
