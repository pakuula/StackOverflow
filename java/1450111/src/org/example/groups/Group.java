package org.example.groups;

import java.util.LinkedList;
import java.util.List;

public class Group {
	private int id;
	private List<Item> items;

	public Group(int id, List<Item> items) {
		super();
		this.id = id;
		this.items = items;
	}
	public Group(int id) {
		super();
		this.id = id;
		this.items = new LinkedList<Item>();
	}

	public int getId() {
		return id;
	}

	public List<Item> getItems() {
		return items;
	}

	@Override
	public String toString() {
		return "Group [id=" + id + ", items=" + items + "]";
	}
}
