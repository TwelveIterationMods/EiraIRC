package net.blay09.mods.eirairc.client.gui;

import net.blay09.mods.eirairc.client.gui.base.GuiLabel;
import net.blay09.mods.eirairc.util.Globals;
import net.blay09.mods.eirairc.util.I19n;
import net.blay09.mods.eirairc.util.Utils;
import net.minecraft.client.gui.GuiButton;

import java.io.PrintWriter;
import java.io.StringWriter;


public class GuiErrorScreen extends EiraGuiScreen {

    private final String errorLangKey;
    private final Exception exception;

    private GuiButton btnClipboard;

    public GuiErrorScreen(String errorLangKey, Exception exception) {
        this.errorLangKey = errorLangKey;
        this.exception = exception;
    }

    @Override
    public void initGui() {
        labelList.add(new GuiLabel(I19n.format("eirairc:gui.error"), width / 2 - 90, height / 2 - 90, Globals.TEXT_COLOR));
        labelList.add(new GuiLabel(I19n.format(errorLangKey), width / 2 - 90, height / 2 - 70, Globals.TEXT_COLOR));

        btnClipboard = new GuiButton(0, width / 2 - 50, height / 2 + 50, I19n.format("eirairc:gui.error.toClipboard"));
        buttonList.add(btnClipboard);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button == btnClipboard) {
            StringWriter sw = new StringWriter();
            PrintWriter writer = new PrintWriter(sw);
            exception.printStackTrace(writer);
            Utils.setClipboardString(sw.toString());
            writer.close();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        drawLightBackground(menuX, menuY, menuWidth, menuHeight);

        super.drawScreen(mouseX, mouseY, par3);
    }

}
