package net.blay09.mods.eirairc.client.gui.base.list;

import net.blay09.mods.eirairc.client.gui.EiraGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiList<T extends GuiListEntry> extends Gui {

	private static final int BACKGROUND_COLOR_TRANSPARENT = Integer.MIN_VALUE;
	private static final int BACKGROUND_COLOR = -16777216;
	private static final int BORDER_COLOR = Integer.MAX_VALUE;
	private static final int SELECTION_COLOR = Integer.MAX_VALUE;
	private static final int DOUBLE_CLICK_TIME = 250;
	private static final float TOOLTIP_TIME = 30;

	private final List<T> entries = new ArrayList<T>();
	private final EiraGuiScreen parentScreen;

	private int xPosition;
	private int yPosition;
	private int width;
	private int height;
	
	private int entryHeight;
	private int scrollOffset;
	private int lastWheelDelta;
	private int selectedIdx = -1;

	private int lastClickIdx = -1;
	private long lastClickTime = 0;

	private GuiListEntry hoverObject;
	private float hoverTime;

	public GuiList(EiraGuiScreen parentScreen, int x, int y, int width, int height, int entryHeight) {
		this.parentScreen = parentScreen;
		this.xPosition = x;
		this.yPosition = y;
		this.width = width;
		this.height = height;
		this.entryHeight = entryHeight;
	}

	public boolean mouseClicked(int x, int y, int button) {
		if(x < xPosition || y < yPosition || x >= xPosition + width || y >= yPosition + height) {
			return false;
		}
		int relY = y - yPosition;
		if(button == 0) {
			int clickedIdx = relY / entryHeight;
			if(clickedIdx >= 0 && clickedIdx < entries.size()) {
				setSelectedIdx(relY / entryHeight);
				long now = System.currentTimeMillis();
				if(lastClickIdx == clickedIdx) {
					if (now - lastClickTime <= DOUBLE_CLICK_TIME) {
						getSelectedItem().onDoubleClick();
					}
				}
				lastClickTime = now;
				lastClickIdx = clickedIdx;
			} else {
				setSelectedIdx(-1);
			}
		}
		return false;
	}

	public void setSelectedIdx(int idx) {
		if(selectedIdx != -1 && selectedIdx < entries.size()) {
			entries.get(selectedIdx).setSelected(false);
		}
		selectedIdx = idx;
		if(selectedIdx >= 0 && selectedIdx < entries.size()) {
			entries.get(selectedIdx).setSelected(true);
		}
	}

	public T getSelectedItem() {
		if(selectedIdx >= 0 && selectedIdx < entries.size()) {
			return entries.get(selectedIdx);
		}
		return null;
	}

	public int getSelectedIdx() {
		return selectedIdx;
	}

	public void drawList(int mouseX, int mouseY) {
		if(lastWheelDelta != 0) {
			if(mouseX >= xPosition && mouseX < xPosition + width && mouseY >= yPosition && mouseY < yPosition + height) {
				int max = entries.size() * entryHeight - height;
				scrollOffset = MathHelper.clamp_int(scrollOffset + lastWheelDelta / 8, max * -1, 0);
			}
			lastWheelDelta = 0;
		}

		drawBackground();
		List<String> tooltipList = null;
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		float scale = scaledResolution.getScaleFactor();
		GL11.glScissor(0, (int) (mc.displayHeight - (yPosition + height) * scale), (int) ((width + xPosition) * scale), (int) (height * scale));
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		for(int i = 0; i < entries.size(); i++) {
			GuiListEntry entry = entries.get(i);
			int entryY = yPosition + i * entryHeight + scrollOffset;
			entry.drawEntry(xPosition, entryY);
			if(!entry.getTooltipText().isEmpty() && mouseX >= xPosition && mouseX < xPosition + width && mouseY >= entryY && mouseY < entryY + entryHeight) {
				if(entry != hoverObject) {
					hoverObject = entry;
					hoverTime = 0f;
				}
				hoverTime++;
				if(hoverTime > TOOLTIP_TIME) {
					tooltipList = entry.getTooltipText();
				}
			}
		}
		drawSelectionBorder();
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		if(tooltipList != null) {
			parentScreen.drawTooltip(tooltipList, mouseX, mouseY);
		}
	}

	public void mouseWheelMoved(int delta) {
		lastWheelDelta = delta;
	}

	private void drawBackground() {
		drawRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width, this.yPosition + this.height, BACKGROUND_COLOR);
		drawHorizontalLine(this.xPosition, this.xPosition + this.width, this.yPosition, BORDER_COLOR);
		drawHorizontalLine(this.xPosition, this.xPosition + this.width, this.yPosition + this.height, BORDER_COLOR);
		drawVerticalLine(this.xPosition, this.yPosition, this.yPosition + this.height, BORDER_COLOR);
		drawVerticalLine(this.xPosition + this.width, this.yPosition, this.yPosition + this.height, BORDER_COLOR);
	}
	
	private void drawSelectionBorder() {
		if(selectedIdx == -1) {
			return;
		}
		drawHorizontalLine(this.xPosition + 1, this.xPosition + this.width - 1, this.yPosition + 1 + selectedIdx * entryHeight + scrollOffset, SELECTION_COLOR);
		drawHorizontalLine(this.xPosition + 1, this.xPosition + this.width - 1, this.yPosition + 1 + selectedIdx * entryHeight + entryHeight + scrollOffset, SELECTION_COLOR);
		drawVerticalLine(this.xPosition + 1, this.yPosition + 1 + selectedIdx * entryHeight + scrollOffset, this.yPosition + entryHeight + 1 + selectedIdx * entryHeight + scrollOffset, SELECTION_COLOR);
		drawVerticalLine(this.xPosition + this.width - 1, this.yPosition + 1 + selectedIdx * entryHeight + scrollOffset, this.yPosition + entryHeight + 1 + selectedIdx * entryHeight + scrollOffset, SELECTION_COLOR);
	}

	public void addEntry(T entry) {
		entry.setParentList(this);
		entries.add(entry);
	}

	public int getEntryHeight() {
		return entryHeight;
	}

	public boolean hasSelection() {
		return (selectedIdx >= 0 && selectedIdx < entries.size());
	}

	public void clear() {
		entries.clear();
		selectedIdx = -1;
		lastClickIdx = -1;
		lastClickTime = 0;
	}

	public int getWidth() {
		return width;
	}

	public List<T> getEntries() {
		return entries;
	}
}
