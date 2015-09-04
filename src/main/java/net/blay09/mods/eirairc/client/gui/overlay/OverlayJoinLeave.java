package net.blay09.mods.eirairc.client.gui.overlay;

import net.blay09.mods.eirairc.api.config.IConfigProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.IChatComponent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class OverlayJoinLeave extends Gui {

    private static class JoinLeaveMessage {
        public final IChatComponent chatComponent;
        public int y;
        public float timeLeft;

        public JoinLeaveMessage(IChatComponent chatComponent, int y, float timeLeft) {
            this.chatComponent = chatComponent;
            this.y = y;
            this.timeLeft = timeLeft;
        }
    }

    private final List<JoinLeaveMessage> messages = new ArrayList<>();
    private final Minecraft mc;
    private final FontRenderer fontRenderer;
    private IConfigProperty<Integer> visibleTime;
    private IConfigProperty<Float> scale;

    public OverlayJoinLeave(Minecraft mc, FontRenderer fontRenderer) {
        this.mc = mc;
        this.fontRenderer = fontRenderer;
    }

    public void setVisibleTime(IConfigProperty<Integer> visibleTime) {
        this.visibleTime = visibleTime;
    }

    public void setScale(IConfigProperty<Float> scale) {
        this.scale = scale;
    }

    public void addMessage(IChatComponent component) {
        for(int i = 0; i < messages.size(); i++) {
            messages.get(i).y -= fontRenderer.FONT_HEIGHT + 2;
        }
        messages.add(new JoinLeaveMessage(component, 0, visibleTime.get()));
    }

    public void updateAndRender(float renderTickTime) {
        ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        final int height = 64;
        int guiTop = resolution.getScaledHeight() - height;
        int guiLeft = resolution.getScaledWidth();

        GL11.glPushMatrix();
        GL11.glTranslatef(guiLeft, guiTop, 0f);
        GL11.glScalef(scale.get(), scale.get(), 1f);
        GL11.glEnable(GL11.GL_BLEND);
        for(int i = messages.size() - 1; i >= 0; i--) {
            JoinLeaveMessage message = messages.get(i);
            message.timeLeft -= renderTickTime;
            int alpha = 255;
            if(message.timeLeft < visibleTime.get() / 5f) {
                alpha = (int) Math.max(11, (255f * (message.timeLeft / (visibleTime.get() / 5f))));
            }
            if(message.timeLeft <= 0) {
                messages.remove(i);
            }
            String formattedText = message.chatComponent.getFormattedText();
            fontRenderer.func_175065_a(formattedText, -fontRenderer.getStringWidth(formattedText) - 16, message.y, 16777215 | (alpha << 24), true);
        }
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

}
