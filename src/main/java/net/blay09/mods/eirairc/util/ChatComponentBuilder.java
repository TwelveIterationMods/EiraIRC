package net.blay09.mods.eirairc.util;

import net.blay09.mods.eirairc.api.EiraIRCAPI;
import net.blay09.mods.eirairc.api.irc.IRCContext;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;

public class ChatComponentBuilder {

    private StringBuilder[] buffer = new StringBuilder[1];

    private int index = -1;

    public ChatComponentBuilder() {
        push();
    }

    public ChatComponentBuilder text(int i) {
        return text(String.valueOf(i));
    }

    public ChatComponentBuilder text(String text) {
        buffer[index].append(text);
        return this;
    }

    public ChatComponentBuilder lang(String langKey, Object... params) {
        buffer[index].append(I19n.format(langKey, params));
        return this;
    }

    public ChatComponentBuilder color(char c) {
        buffer[index].append('\u00a7').append(c);
        return this;
    }

    public void send() {
        send(null, null);
    }

    public void send(IRCContext source) {
        send(null, source);
    }

    public void send(ICommandSender sender) {
        send(sender, null);
    }

    public void send(ICommandSender sender, IRCContext source) {
        if(sender != null) {
            EiraIRCAPI.getChatHandler().addChatMessage(sender, toChatComponent(), source);
        } else {
            EiraIRCAPI.getChatHandler().addChatMessage(toChatComponent(), source);
        }
        buffer[index]= new StringBuilder();
    }

    public ChatComponentBuilder push() {
        index++;
        if(buffer.length <= index) {
            buffer = Arrays.copyOf(buffer, index);
        }
        buffer[index] = new StringBuilder();
        return this;
    }

    public String pop() {
        String result = buffer[index].toString();
        buffer[index] = null;
        index--;
        return result;
    }

    public IChatComponent toChatComponent() {
        IChatComponent rootComponent = null;
        StringBuilder textBuffer = new StringBuilder();
        StringBuilder styleBuffer = new StringBuilder();
        String result = buffer[index].toString();
        for(int i = 0; i < result.length(); i++) {
            char c = result.charAt(i);
            if(c == '\u00a7' && i + 1 < result.length()) {
                if(textBuffer.length() > 0) {
                    IChatComponent newComponent = new ChatComponentText(textBuffer.toString());
                    setStyleFromString(newComponent, styleBuffer.toString());
                    if(rootComponent == null) {
                        rootComponent = newComponent;
                    } else {
                        rootComponent.appendSibling(newComponent);
                    }
                    styleBuffer = new StringBuilder();
                    textBuffer = new StringBuilder();
                }
                styleBuffer.append('\u00a7').append(result.charAt(i + 1));
                i++;
            } else {
                textBuffer.append(c);
            }
        }
        if(textBuffer.length() > 0) {
            IChatComponent newComponent = new ChatComponentText(textBuffer.toString());
            setStyleFromString(newComponent, styleBuffer.toString());
            if(rootComponent == null) {
                rootComponent = newComponent;
            } else {
                rootComponent.appendSibling(newComponent);
            }
        }
        return rootComponent;
    }

    private void setStyleFromString(IChatComponent component, String style) {
        ChatStyle chatStyle = component.getChatStyle();
        if(style.length() > 0) {
            for(int i = 1; i < style.length(); i += 2) {
                char c = style.charAt(i);
                switch(c) {
                    case 'o': chatStyle.setItalic(true); break;
                    case 'n': chatStyle.setUnderlined(true); break;
                    case 'm': chatStyle.setStrikethrough(true); break;
                    case 'l': chatStyle.setBold(true); break;
                    case 'k': chatStyle.setObfuscated(true); break;
                    case 'r': break;
                    default:
                        chatStyle.setColor(IRCFormatting.getColorFromMCColorCode(c));
                }
            }
        }
    }

    public static ChatComponentBuilder create() {
        return new ChatComponentBuilder();
    }
}
