package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ChatComponentBuilder {

    private final IChatComponent[] rootComponent;
    private final EnumChatFormatting[] nextColor;

    private int index;

    public ChatComponentBuilder() {
        this(1);
    }

    public ChatComponentBuilder(int buffer) {
        rootComponent = new IChatComponent[buffer];
        nextColor = new EnumChatFormatting[buffer];
    }

    public ChatComponentBuilder text(int i) {
        return text(String.valueOf(i));
    }

    public ChatComponentBuilder text(String text) {
        IChatComponent newComponent = new ChatComponentText(text);
        if(nextColor != null) {
            newComponent.getChatStyle().setColor(nextColor[index]);
        }
        if(rootComponent[index] != null) {
            rootComponent[index].appendSibling(newComponent);
        } else {
            rootComponent[index] = newComponent;
        }
        return this;
    }

    public ChatComponentBuilder lang(String langKey, Object... params) {
        IChatComponent newComponent = new ChatComponentText(I19n.format(langKey, params));
        if(nextColor != null) {
            newComponent.getChatStyle().setColor(nextColor[index]);
        }
        if(rootComponent[index] != null) {
            rootComponent[index].appendSibling(newComponent);
        } else {
            rootComponent[index] = newComponent;
        }
        return this;
    }

    public ChatComponentBuilder color(EnumChatFormatting color) {
        nextColor[index] = color;
        return this;
    }

    public ChatComponentBuilder color(char c) {
        nextColor[index] = IRCFormatting.getColorFromMCColorCode(c);
        return this;
    }

    public void send() {
        send(null);
    }

    public void send(ICommandSender sender) {
        if(sender != null) {
            EiraIRCAPI.getChatHandler().addChatMessage(sender, rootComponent[index]);
        } else {
            EiraIRCAPI.getChatHandler().addChatMessage(rootComponent[index]);
        }
    }

    public ChatComponentBuilder push() {
        index++;
        return this;
    }

    public IChatComponent pop() {
        IChatComponent result = rootComponent[index];
        rootComponent[index] = null;
        nextColor[index] = null;
        index--;
        return result;
    }

    public static ChatComponentBuilder create() {
        return new ChatComponentBuilder();
    }
}
