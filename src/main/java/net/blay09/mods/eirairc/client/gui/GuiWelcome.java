package net.blay09.mods.eirairc.client.gui;

import cpw.mods.fml.client.config.GuiCheckBox;
import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.client.gui.base.GuiLinkButton;
import net.blay09.mods.eirairc.client.gui.base.list.GuiList;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.SuggestedChannel;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.gui.GuiButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Blay09 on 20.02.2015.
 */
public class GuiWelcome extends EiraGuiScreen {

	private GuiList<GuiListSuggestedChannelEntry> lstChannels;
	private GuiCheckBox chkDontShowAgain;
	private GuiCheckBox chkRecommendedOnly;
	private GuiLinkButton btnSubmitChannel;

	@Override
	public void initGui() {
		super.initGui();

		GuiLabel lblHeader = new GuiLabel("\u00a7lWelcome to EiraIRC!", menuX, menuY + 5, Globals.TEXT_COLOR);
		lblHeader.setHAlignment(GuiLabel.HAlignment.Center, menuWidth);
		labelList.add(lblHeader);

		GuiLabel lblInfo = new GuiLabel("If you just want to find some people to talk to,\n this a list of IRC channels you should check out:", menuX, menuY + 20, Globals.TEXT_COLOR);
		lblInfo.setHAlignment(GuiLabel.HAlignment.Center, menuWidth);
		labelList.add(lblInfo);

		chkRecommendedOnly = new GuiCheckBox(0, menuX + 10, menuY + 50, "Show recommended channels only", true);
		buttonList.add(chkRecommendedOnly);

		lstChannels = new GuiList<GuiListSuggestedChannelEntry>(menuX + 10, menuY + 65, menuWidth - 20, 100, 30);
		listList.add(lstChannels);

		chkDontShowAgain = new GuiCheckBox(1, menuX + 10, menuY + menuHeight - 30, "Don't show this message again", false);
		buttonList.add(chkDontShowAgain);

		btnSubmitChannel = new GuiLinkButton(2, menuX + menuWidth - 85, menuY + menuHeight - 20, mc.fontRenderer, "\u00a7nSubmit Channel");
		buttonList.add(btnSubmitChannel);

		updateList(chkRecommendedOnly.isChecked());
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(button == chkRecommendedOnly) {
			updateList(chkRecommendedOnly.isChecked());
		} else if(button == btnSubmitChannel) {
			Utils.openWebpage("http://goo.gl/forms/2LsJiWIQmS");
		}
	}

	public void updateList(boolean recommendedOnly) {
		lstChannels.clear();
		String modpackId = Utils.getModpackId();
		List<SuggestedChannel> outputList = new ArrayList<SuggestedChannel>();
		for(SuggestedChannel channel : ConfigurationHandler.getSuggestedChannels()) {
			if(recommendedOnly) {
				if(!channel.isRecommended()) {
					continue;
				}
				if(channel.isModpackExclusive() && !channel.getModpackId().equals(modpackId)) {
					continue;
				}
			}
			channel.calculateScore(modpackId);
			outputList.add(channel);
		}

		Collections.sort(outputList, new Comparator<SuggestedChannel>() {
			@Override
			public int compare(SuggestedChannel o1, SuggestedChannel o2) {
				return Integer.compare(o2.getScore(), o1.getScore());
			}
		});

		boolean altBackground = false;
		for(SuggestedChannel channel : outputList) {
			lstChannels.addEntry(new GuiListSuggestedChannelEntry(mc.fontRenderer, channel, altBackground));
			altBackground = !altBackground;
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		ClientGlobalConfig.showWelcomeScreen = !chkDontShowAgain.isChecked();
		ClientGlobalConfig.save();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		this.drawLightBackground(menuX, menuY, menuWidth, menuHeight);
		super.drawScreen(mouseX, mouseY, p_73863_3_);
	}
}
