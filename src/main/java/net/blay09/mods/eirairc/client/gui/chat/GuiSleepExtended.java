package net.blay09.mods.eirairc.client.gui.chat;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiSleepMP;

public class GuiSleepExtended extends GuiSleepMP {

    private GuiChatExtension extension;
    private String defaultInputText;

    public GuiSleepExtended() {
        this("");
    }

    public GuiSleepExtended(String defaultInputText) {
        this.defaultInputText = defaultInputText;
    }

    @Override
    public void initGui() {
        super.initGui();
        inputField.setText(defaultInputText);

        extension = new GuiChatExtension(this, inputField);
        extension.initGui(buttonList);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        extension.onGuiClosed();
    }

    @Override
    public void actionPerformed(GuiButton button) {
        super.actionPerformed(button);

        extension.actionPerformed(button);
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if(extension.confirmClicked(result, id)) {
            super.confirmClicked(result, id);
        }
    }

    @Override
    protected void keyTyped(char unicode, int keyCode) {
        if(extension.keyTyped(unicode, keyCode)) {
            super.keyTyped(unicode, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if(extension.mouseClicked(mouseX, mouseY, button)) {
            super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void drawScreen(int i, int j, float k) {
        extension.preRender();
        super.drawScreen(i, j, k);
        extension.postRender();
    }

    @Override
    public void autocompletePlayerNames() {
        if(extension.autocompletePlayernames()) {
            super.autocompletePlayerNames();
        }
    }

}
