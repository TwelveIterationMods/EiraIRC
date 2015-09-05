package net.blay09.mods.eirairc.client;

import net.blay09.mods.eirairc.util.IRCFormatting;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

public class BetterColorEntry extends GuiConfigEntries.ListEntryBase {

    private static EnumChatFormatting[] chatFormatting = EnumChatFormatting.values();

    private EnumChatFormatting beforeValue;
    private EnumChatFormatting currentValue;
    private int lastRenderY;

    public BetterColorEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
        super(owningScreen, owningEntryList, configElement);
        this.beforeValue = IRCFormatting.getColorFromName((configElement.get().toString()));
        if(beforeValue == null) {
            beforeValue = EnumChatFormatting.WHITE;
        }
        this.currentValue = beforeValue;
    }

    @Override
    public boolean isDefault() {
        return currentValue == IRCFormatting.getColorFromName(configElement.getDefault().toString());
    }

    @Override
    public void setToDefault() {
        if (enabled()) {
            currentValue = IRCFormatting.getColorFromName(configElement.getDefault().toString());
            if(currentValue == null) {
                currentValue = EnumChatFormatting.WHITE;
            }
        }
    }

    @Override
    public void keyTyped(char eventChar, int eventKey) {
    }

    @Override
    public void updateCursorCounter() {
    }

    @Override
    public void mouseClicked(int x, int y, int mouseEvent) {
        final int borderSize = 1;
        final int width = (owningEntryList.controlWidth - borderSize * 2) / 16;
        final int fullWidth = (width * 16 + borderSize * 2);
        final int height = owningEntryList.slotHeight - borderSize * 2;
        final int borderX = owningEntryList.controlX + owningEntryList.controlWidth / 2 - (width * 16 + borderSize * 2) / 2;
        int colorX = borderX + borderSize;
        int colorY = lastRenderY + borderSize;
        if (x >= colorX && y >= colorY && x < colorX + fullWidth && y < colorY + height) {
            int i = Math.min(15, (x - borderX) / width);
            currentValue = chatFormatting[i];
        }
    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected);


        this.lastRenderY = y;

        final int borderSize = 1;
        final int width = (owningEntryList.controlWidth - borderSize * 2) / 16;
        final int height = slotHeight - borderSize * 2;
        final int borderX = owningEntryList.controlX + owningEntryList.controlWidth / 2 - (width * 16 + borderSize * 2) / 2;
        final int borderColor = -765432123;
        GuiContainer.drawRect(borderX, y, borderX + width * 16 + borderSize * 2, y + slotHeight, borderColor);
        for (int i = 0; i < 16; i++) {
            int colorX = borderX + borderSize + i * width;
            int colorY = y + borderSize;
            GuiContainer.drawRect(colorX, colorY, colorX + width, colorY + height, getIntColorFromIndex(i) | (255 << 24));
            if (mouseX >= colorX && mouseX < colorX + width && mouseY >= colorY && mouseY < colorY + height) {
                GuiContainer.drawRect(colorX, colorY, colorX + width, colorY + height, getIntColorFromIndex(15) | (128 << 24));
            }
        }
        GuiContainer.drawRect(owningEntryList.controlX, y + 3, owningEntryList.controlX + owningEntryList.controlWidth, y + 3 + slotHeight - 6, getIntColorFromIndex(0) | (128 << 24));
        String s = currentValue.toString() + "Example Text";
        mc.fontRendererObj.func_175065_a(s, owningScreen.entryList.controlX + owningEntryList.controlWidth / 2 - mc.fontRendererObj.getStringWidth(s) / 2, y + slotHeight / 2 - mc.fontRendererObj.FONT_HEIGHT / 2, -1, true); // drawString
    }

    @Override
    public boolean isChanged() {
        return currentValue != beforeValue;
    }

    @Override
    public void undoChanges() {
        if (enabled()) {
            currentValue = beforeValue;
        }
    }

    @Override
    public boolean saveConfigElement() {
        if (enabled() && isChanged()) {
            configElement.set(IRCFormatting.getNameFromColor(currentValue));
            return configElement.requiresMcRestart();
        }
        return false;
    }

    @Override
    public EnumChatFormatting getCurrentValue() {
        return currentValue;
    }

    @Override
    public EnumChatFormatting[] getCurrentValues() {
        return new EnumChatFormatting[]{currentValue};
    }

    private static int getIntColorFromIndex(int colorIndex) {
        switch (colorIndex) {
            case 0:
                return 0;
            case 1:
                return 170;
            case 2:
                return 43520;
            case 3:
                return 43690;
            case 4:
                return 11141120;
            case 5:
                return 11141290;
            case 6:
                return 16755200;
            case 7:
                return 11184810;
            case 8:
                return 5592405;
            case 9:
                return 5592575;
            case 10:
                return 5635925;
            case 11:
                return 5636095;
            case 12:
                return 16733525;
            case 13:
                return 16733695;
            case 14:
                return 16777045;
            case 15:
                return 16777215;
        }
        return 16777215;
    }

}
