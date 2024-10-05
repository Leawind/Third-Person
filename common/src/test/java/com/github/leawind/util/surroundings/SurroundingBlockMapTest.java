package com.github.leawind.util.surroundings;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * TODO remove
 */
public class SurroundingBlockMapTest {
	@Test
	public void test () {
		Assertions.assertTrue(matchLayer(" 000111???", "000111101"));
		Assertions.assertTrue(matchLayer(" 000111???", "000111010"));
		Assertions.assertTrue(matchLayer(" 10?01??10", "100011010"));
		Assertions.assertFalse(matchLayer("000111???", "100111010"));
		Assertions.assertTrue(matchLayer(" 10?01??10", "101011001"));
		{
			String pattern;
			pattern = """
				A A A  T A T  A T A
				A A A  A F A  T T T
				A A A  T A T  A T A
				""";
			Assertions.assertTrue(matchMap(pattern, """
				T F T  T F T  T T T
				T F T  T F T  T T T
				T F T  T F T  T T T
				"""));
			Assertions.assertTrue(matchMap(pattern, """
				F F F  T F T  F T F
				F F F  F F F  T T T
				F F F  T F T  F T F
				"""));
			Assertions.assertTrue(matchMap(pattern, """
				T T T  T T T  T T T
				T T T  T F T  T T T
				T T T  T T T  T T T
				"""));
			Assertions.assertFalse(matchMap(pattern, """
				T T T  T T T  T T T
				T T T  T F T  T F T
				T T T  T T T  T F T
				"""));
			Assertions.assertFalse(matchMap(pattern, """
				T T T  T T T  T T T
				T T T  T F T  T F T
				T T T  F T T  T F T
				"""));
			Assertions.assertTrue(matchMap("""
											   | 1 1 1 | 1 1 1 | 1 1 1 |
											   | 0 0 1 | 0 0 1 | 1 1 1 |
											   | 1 1 1 | 1 1 1 | 1 1 1 |
											   """, """
											   | · · · | 1 · 1 | · 1 · |
											   | · · · | · · · | 1 1 1 |
											   | · · · | 1 · 1 | · 1 · |
											   """));
		}
	}

	static boolean matchMap (SurroundingBlockMap pattern, String mapStr) {
		var map = SurroundingBlockMap.of(mapStr);
		return pattern.match(map);
	}

	static boolean matchMap (String patternStr, String mapStr) {
		var pattern = SurroundingBlockMap.of(patternStr);
		var map     = SurroundingBlockMap.of(mapStr);
		return pattern.match(map);
	}

	static boolean matchLayer (String patternStr, String layerStr) {
		int pattern = SurroundingBlockMap.parseLayer(patternStr);
		int map     = SurroundingBlockMap.parseLayer(layerStr);
		return SurroundingBlockMap.match(pattern, map);
	}
}
