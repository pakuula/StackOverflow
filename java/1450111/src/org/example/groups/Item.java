package org.example.groups;

public class Item {
	private int id;
	private String time;
	private double value;

	public Item(int id, String time, double value) {
		super();
		this.id = id;
		this.time = time;
		this.value = value;
	}

	public String getTime() {
		return time;
	}

	public int getId() {
		return id;
	}

	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return  "Item [id=" + id + ", time=" + time + ", value=" + String.format("%.2f", value) + "]";
	}
}
