package net.blay09.mods.eirairc.client.gui.base.list;

import net.minecraft.client.gui.Gui;

import java.util.ArrayList;
import java.util.List;

public class GuiList<T extends GuiListEntry> extends Gui {

	private static final int BACKGROUND_COLOR_TRANSPARENT = Integer.MIN_VALUE;
	private static final int BACKGROUND_COLOR = -16777216;
	private static final int BORDER_COLOR = Integer.MAX_VALUE;
	private static final int SELECTION_COLOR = Integer.MAX_VALUE;
	private static final int DOUBLE_CLICK_TIME = 250;

	private final List<T> entries = new ArrayList<T>();
	
	private int xPosition;
	private int yPosition;
	private int width;
	private int height;
	
	private int entryHeight;
	private int selectedIdx = -1;

	private int lastClickIdx = -1;
	private long lastClickTime = 0;
	
	public GuiList(int x, int y, int width, int height, int entryHeight) {
		this.xPosition = x;
		this.yPosition = y;
		this.width = width;
		this.height = height;
		this.entryHeight = entryHeight;
	}

	public void mouseClicked(int x, int y, int button) {
		if(x < xPosition || y < yPosition || x >= xPosition + width || y >= yPosition + height) {
			return;
		}
		int relX = x - xPosition;
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
	}

	public void setSelectedIdx(int idx) {
		if(selectedIdx != -1) {
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

	public void drawList() {
		drawBackground();
		drawEntries();
		drawSelectionBorder();
	}
	
	private void drawBackground() {
		drawRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width, this.yPosition + this.height, BACKGROUND_COLOR);
		drawHorizontalLine(this.xPosition, this.xPosition + this.width, this.yPosition, BORDER_COLOR);
		drawHorizontalLine(this.xPosition, this.xPosition + this.width, this.yPosition + this.height, BORDER_COLOR);
		drawVerticalLine(this.xPosition, this.yPosition, this.yPosition + this.height, BORDER_COLOR);
		drawVerticalLine(this.xPosition + this.width, this.yPosition, this.yPosition + this.height, BORDER_COLOR);
	}
	
	private void drawEntries() {
		for(int i = 0; i < entries.size(); i++) {
			GuiListEntry entry = entries.get(i);
			entry.drawEntry(xPosition, yPosition + i * entryHeight);
		}
	}
	
	private void drawSelectionBorder() {
		if(selectedIdx == -1) {
			return;
		}
		drawHorizontalLine(this.xPosition + 1, this.xPosition + this.width - 1, this.yPosition + 1 + selectedIdx * entryHeight, SELECTION_COLOR);
		drawHorizontalLine(this.xPosition + 1, this.xPosition + this.width - 1, this.yPosition + 1 + selectedIdx * entryHeight + entryHeight, SELECTION_COLOR);
		drawVerticalLine(this.xPosition + 1, this.yPosition + 1 + selectedIdx * entryHeight, this.yPosition + entryHeight + 1 + selectedIdx * entryHeight, SELECTION_COLOR);
		drawVerticalLine(this.xPosition + this.width - 1, this.yPosition + 1 + selectedIdx * entryHeight, this.yPosition + entryHeight + 1 + selectedIdx * entryHeight, SELECTION_COLOR);
	}

	public void addEntry(T entry) {
		entry.setParentList(this);
		entries.add(entry);
	}

	public void removeEntry(T entry) {
		if(getSelectedItem() == entry) {
			selectedIdx = -1;
		}
		entries.remove(entry);
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
}
