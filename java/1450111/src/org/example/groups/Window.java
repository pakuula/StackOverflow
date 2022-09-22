package org.example.groups;


import java.text.ParseException;

public class Window {
	public static final long PERIOD = 60000;
	
	public final int windowId;
	public long start;
	public long end;
	public int count = 0;
	public double sum = 0.0;
	public double lastValue = 0.0;
	
	public Window(long start, long duration, int windowId) {
		this.windowId = windowId;
		this.start = start;
		this.end = start + duration;
	}
	
	public double add(double v) throws ParseException {
		count +=1;
		sum += v;
		return value();
	}
	
	public double value() {
		if (count == 0) {
			return lastValue;
		}
		return sum / count;
	}
	
	public Item shift()  {
		return shift(1);
	}
	
	public Item shift(int n) {
		Item result = toItem();
		
		lastValue = value();		
		sum = 0.0;
		count = 0;
		start += n*PERIOD;
		end += n*PERIOD;
		
		return result;
	}
	
	public Item toItem() {
		return new Item(
				windowId,
				Util.toDateString(start),
				value()
				);
	}
	
	public boolean isIn(long t) throws ParseException {
		return (start <= t && t < end); 
	}
}
