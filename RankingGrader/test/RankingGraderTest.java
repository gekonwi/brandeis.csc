import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class RankingGraderTest {

	private ArrayList<Solution> solutions;
	private Map<Solution, Double> grades;

	private static double DOUBLE_TOL = 0.001;

	@Before
	public void before() {
		solutions = new ArrayList<>(7);
		grades = new HashMap<Solution, Double>();
		for (int i = 0; i < 7; i++) {
			solutions.add(new Solution());
			grades.put(solutions.get(i), 100.0 - i * RankingGrader.TOLERANCE);
		}
	}

	@Test
	public void testPerfectRanking() {
		List<Solution> r = createRanking(new int[] { 0, 1, 2, 3, 4, 5, 6 });
		assertEquals(100.0, RankingGrader.grade(grades, r), DOUBLE_TOL);
	}

	@Test
	public void testSimilarlyGradedSwitched1() {
		List<Solution> r = createRanking(new int[] { 1, 0, 3, 2, 5, 4, 6 });
		assertEquals(100.0, RankingGrader.grade(grades, r), DOUBLE_TOL);
	}

	@Test
	public void testSimilarlyGradedSwitched2() {
		List<Solution> r = createRanking(new int[] { 0, 2, 1, 3, 4, 6, 5 });
		assertEquals(100.0, RankingGrader.grade(grades, r), DOUBLE_TOL);
	}

	@Test
	public void testOppositeOrder() {
		List<Solution> r = createRanking(new int[] { 6, 5, 4, 3, 2, 1, 0 });

		/*
		 * Make sure all grades have a distance bigger than the one tolerated by
		 * RankingGrader. Thus the opposite of the optimum order won't give any
		 * points since no ranking switches get tolerated.
		 */
		for (int i = 0; i < 7; i++)
			grades.put(solutions.get(i), 100.0 - i * RankingGrader.TOLERANCE
					* 1.1);

		assertEquals(0.0, RankingGrader.grade(grades, r), DOUBLE_TOL);
	}

	@Test
	public void testPunishmentProportionalToGradeDistance() {
		List<Solution> betterRank = createRanking(new int[] { 2, 1, 0, 3, 4, 5,
				6 });
		List<Solution> worseRank = createRanking(new int[] { 3, 1, 2, 0, 4, 5,
				6 });

		final double betterGrade = RankingGrader.grade(grades, betterRank);
		final double worseGrade = RankingGrader.grade(grades, worseRank);
		assertTrue(betterGrade > worseGrade);
	}

	private List<Solution> createRanking(int[] order) {
		List<Solution> r = new ArrayList<Solution>();
		for (int i : order)
			r.add(solutions.get(i));
		return r;
	}
}
