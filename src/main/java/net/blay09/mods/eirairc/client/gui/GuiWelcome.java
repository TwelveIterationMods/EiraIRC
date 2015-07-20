// Copyright (c) 2015 Christopher "BlayTheNinth" Baker

package net.blay09.mods.eirairc.client.gui;

import cpw.mods.fml.client.config.GuiCheckBox;
import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.client.gui.base.GuiLinkButton;
import net.blay09.mods.eirairc.client.gui.base.list.GuiList;
import net.blay09.mods.eirairc.config.ClientGlobalConfig;
import net.blay09.mods.eirairc.config.ConfigurationHandler;
import net.blay09.mods.eirairc.config.SuggestedChannel;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.I19n;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.gui.GuiButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GuiWelcome extends EiraGuiScreen {

	private GuiList<GuiListSuggestedChannelEntry> lstChannels;
	private GuiCheckBox chkDontShowAgain;
	private GuiCheckBox chkRecommendedOnly;
	private GuiLinkButton btnSubmitChannel;

	@Override
	@SuppressWarnings("unchecked")
	public void initGui() {
		super.initGui();

		GuiLabel lblHeader = new GuiLabel("\u00a7l" + I19n.format("eirairc:gui.welcome"), menuX, menuY + 5, Globals.TEXT_COLOR);
		lblHeader.setHAlignment(GuiLabel.HAlignment.Center, menuWidth);
		labelList.add(lblHeader);

		GuiLabel lblInfo = new GuiLabel(I19n.format("eirairc:gui.welcome.suggestedChannels"), menuX, menuY + 20, Globals.TEXT_COLOR);
		lblInfo.setHAlignment(GuiLabel.HAlignment.Center, menuWidth);
		labelList.add(lblInfo);

		chkRecommendedOnly = new GuiCheckBox(0, menuX + 10, menuY + 50, I19n.format("eirairc:gui.welcome.recommendedOnly"), true);
		buttonList.add(chkRecommendedOnly);

		lstChannels = new GuiList<>(this, menuX + 10, menuY + 65, menuWidth - 20, 100, 30);
		listList.add(lstChannels);

		chkDontShowAgain = new GuiCheckBox(1, menuX + 10, menuY + menuHeight - 30, I19n.format("eirairc:gui.welcome.dontShowAgain"), false);
		buttonList.add(chkDontShowAgain);

		btnSubmitChannel = new GuiLinkButton(2, menuX + menuWidth - 85, menuY + menuHeight - 20, mc.fontRendererObj, "\u00a7n" + I19n.format("eirairc:gui.welcome.submit"));
		buttonList.add(btnSubmitChannel);

		updateList(chkRecommendedOnly.isChecked());
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(button == chkRecommendedOnly) {
			updateList(chkRecommendedOnly.isChecked());
		} else if(button == btnSubmitChannel) {
			Utils.openWebpage("https://twitter.com/BlayTheNinth");
		}
	}

	public void updateList(boolean recommendedOnly) {
		lstChannels.clear();
		String modpackId = Utils.getModpackId();
		List<SuggestedChannel> outputList = new ArrayList<>();
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
			lstChannels.addEntry(new GuiListSuggestedChannelEntry(mc.fontRendererObj, channel, altBackground));
			altBackground = !altBackground;
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		ClientGlobalConfig.showWelcomeScreen.set(!chkDontShowAgain.isChecked());
		ClientGlobalConfig.save();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		this.drawLightBackground(menuX, menuY, menuWidth, menuHeight);
		super.drawScreen(mouseX, mouseY, p_73863_3_);
	}
}
