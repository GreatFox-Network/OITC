package me.kahjiit.OITC.stats;

import java.util.Comparator;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ScoreComparator implements Comparator {

	Map base;

	public ScoreComparator(Map base) {
		this.base = base;
	}

	public int compare(Object a, Object b) {
		if ((Integer) base.get(a) != null) {
			if (((Integer) base.get(a)).intValue() < ((Integer) base.get(b)).intValue()) {
				return 1;
			}
			if ((Integer) base.get(a) == (Integer) base.get(b)) {
				return a.toString().compareTo(b.toString());
			}
			return -1;
		}
		return 0;
	}
}