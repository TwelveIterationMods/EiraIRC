package net.blay09.mods.eirairc.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.Gui;

public class GuiList extends Gui {

	private static final int BACKGROUND_COLOR_TRANSPARENT = Integer.MIN_VALUE;
	private static final int BACKGROUND_COLOR = -16777216;
	private static final int BORDER_COLOR = Integer.MAX_VALUE;
	private static final int SELECTION_COLOR = Integer.MAX_VALUE;
	
	private final List<GuiListEntry> entries = new ArrayList<GuiListEntry>();
	
	private int xPosition;
	private int yPosition;
	private int width;
	private int height;
	
	private int entryHeight;
	private int scrollOffset;
	
	private int selectedIdx = -1;
	
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

	public void addEntry(GuiListEntry entry) {
		entries.add(entry);
	}

	public int getEntryHeight() {
		return entryHeight;
	}
}
