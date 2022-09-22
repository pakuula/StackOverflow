package org.example.groups;

import java.text.ParseException;
import java.util.List;

public class Main {
	public static void main(String[] args) throws ParseException {
		List<Item> items = List.of(
		        new Item(1, "19/09/2020 1:03:00 AM", 1.0),
		        new Item(2, "19/09/2020 1:03:03 AM", 1.3),
		        new Item(3, "19/09/2020 1:03:15 AM", 1.1),
		        new Item(4, "19/09/2020 1:03:47 AM", 1.2),
		        new Item(5, "19/09/2020 1:03:57 AM", 1.6),
		        new Item(6, "19/09/2020 1:04:04 AM", 1.8),
		        new Item(7, "19/09/2020 1:04:43 AM", 1.9),
		        new Item(8, "19/09/2020 1:04:44 AM", 2.1),
		        new Item(9, "19/09/2020 1:05:30 AM", 1.8),
		        new Item(10, "19/09/2020 1:05:46 AM", 2.3)
		);
		long start = Util.timeInMillis(items.get(0));
		start = Util.periodStart(start);
		
		Average av = new Average(start, 3);
		for (Item it : items) {
			av.add(it);
		}

		var lst = av.finish();
		for (Item it : lst) {
			System.out.println(it.toString());
		}
		
		for (Group g : av.groupify()) {
			System.out.println(g.toString());
		}
	}
}
