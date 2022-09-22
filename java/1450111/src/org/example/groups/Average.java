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
		long intervalStart = Util.periodStart(start);
		long intervalEnd = intervalStart + Window.PERIOD;
		
		duration = Window.PERIOD / numOfWindows;
		windows = new Window[numOfWindows];
		
		for (int i = 0; i < numOfWindows; i++) {
			windows[i] = new Window(intervalStart + duration*i, duration, i+1);
		}
		windows[numOfWindows-1].end = intervalEnd;
	}
	
	private long intervalStart() {
		return windows[0].start;
	}
	
	private long intervalEnd() {
		return intervalStart() + Window.PERIOD;
	}

	private boolean isIn(long t) {
		return (intervalStart() <= t && t < intervalEnd());
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
		while (!isIn(t)) {
			finishPeriod();
		}
		int wIdx = (int) ((t - intervalStart()) / duration);
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
