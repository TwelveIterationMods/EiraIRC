package net.blay09.mods.eirairc.client.gui;

import cpw.mods.fml.client.config.GuiCheckBox;
import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.config.LocalConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;

public class GuiModpackConfirmation extends EiraGuiScreen {

    private ResourceLocation logoTexture = new ResourceLocation("eirairc", "gfx/eirairc_logo.png");

    private GuiLabel lblInfo;
    private GuiButton btnEnable;
    private GuiButton btnDisable;
    private GuiCheckBox chkDontShowAgain;

    @Override
    public void initGui() {
        super.initGui();

        String infoText = "";
        File infoTextFile = new File(mc.mcDataDir, "config/eirairc/modpack-confirmation.txt");
        if(infoTextFile.exists()) {
            try {
                infoText = FileUtils.readFileToString(infoTextFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ResourceLocation infoTextResource = new ResourceLocation("eirairc", "modpack-confirmation.txt");
            try {
                infoText = IOUtils.toString(mc.getResourceManager().getResource(infoTextResource).getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lblInfo = new GuiLabel(infoText, 0, height / 2, -1);
        lblInfo.posY -= lblInfo.getHeight() / 2f + fontRendererObj.FONT_HEIGHT;
        lblInfo.setHAlignment(GuiLabel.HAlignment.Center, width);
        labelList.add(lblInfo);

        btnEnable = new GuiButton(0, width / 2 - 160, height / 2 + 80, 150, 20, "\u00a72Enable this feature");
        buttonList.add(btnEnable);
        btnDisable = new GuiButton(1, width / 2 + 10, height / 2 + 80, 150, 20, "\u00a7cDisable this feature");
        buttonList.add(btnDisable);
        chkDontShowAgain = new GuiCheckBox(2, width / 2, height / 2 + 50, " Don't show this again", LocalConfig.disableModpackConfirmation.get());
        chkDontShowAgain.xPosition -= chkDontShowAgain.getButtonWidth() / 2;
        buttonList.add(chkDontShowAgain);

        allowSideClickClose = false;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);

        if(button == chkDontShowAgain) {
            LocalConfig.disableModpackConfirmation.set(chkDontShowAgain.isChecked());
            LocalConfig.save();
        } else if(button == btnEnable) {
            LocalConfig.disableModpackIRC.set(false);
            LocalConfig.save();
            mc.displayGuiScreen(new GuiMainMenu());
        } else if(button == btnDisable) {
            LocalConfig.disableModpackIRC.set(true);
            LocalConfig.save();
            mc.displayGuiScreen(new GuiMainMenu());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float delta) {
        drawDefaultBackground();

        final int logoWidth = 145;
        final int logoHeight = 60;
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glEnable(GL11.GL_BLEND);
        mc.renderEngine.bindTexture(logoTexture);
        drawTexturedModalRect(width / 2 - logoWidth / 2, 0, 0, 0, logoWidth, logoHeight);
        GL11.glDisable(GL11.GL_BLEND);

        super.drawScreen(mouseX, mouseY, delta);
    }
}
