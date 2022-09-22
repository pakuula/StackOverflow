package org.example.groups;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

public class Average {
	public List<Item> items;

	private Window[] windows;
	private double lastValue = 0.0;

	private final long duration;

	public Average(long start, int numOfWindows) {
		items = new LinkedList<Item>();
		long startPeriod = Util.periodStart(start);
		long endPeriod = startPeriod + Window.PERIOD;
		
		duration = Window.PERIOD / numOfWindows;
		windows = new Window[numOfWindows];
		
		for (int i = 0; i < numOfWindows; i++) {
			windows[i] = new Window(startPeriod + duration*i, duration, i+1);
		}
		windows[numOfWindows-1].end = endPeriod;
	}
	
	private Window lastWindow() {
		return windows[windows.length-1];
	}
	
	private long periodStart() {
		return windows[0].start;
	}
	
	private long periodEnd() {
		return lastWindow().end;
	}

	private boolean isInPeriod(long t) {
		long periodStart = periodStart();
		long periodEnd = periodEnd();
		
		return (periodStart <= t && t < periodEnd);
	}

	private void finishPeriod() {
		for (Window w : windows) {
			w.lastValue = lastValue;
			Item it = w.shift();
			lastValue = it.getValue();
			items.add(it);
		}
	}

	public void add(Item it) throws ParseException {
		long t = Util.timeInMillis(it);
		while (!isInPeriod(t)) {
			finishPeriod();
		}
		int wIdx = (int) ((t - periodStart()) / duration);
		assert(wIdx < windows.length);
		windows[wIdx].add(it.getValue());
	}
	
	public List<Item> finish() {
		finishPeriod();
		return items;
	}
	
	public List<Group> groupify() {
		LinkedList<Group> result = new LinkedList<Group>();
		
		int itemsPerGroup = windows.length;
		int groupId = 1;
		for (int i = 0; i < items.size(); i+=itemsPerGroup) {
			Group g = new Group(groupId++, items.subList(i, i+itemsPerGroup));
			result.add(g);
		}
		return result;
	}
}
