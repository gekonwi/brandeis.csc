import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Controller implements MainView.MainViewController {
	public class MinDistanceToBigException extends Exception {

		private static final long serialVersionUID = 1L;

	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Controller c = new Controller();
		c.start();
	}

	private static final double ROW_FACTOR = 1.5;
	private static final String ROUND2_HEADLINE = "Group ID\tPerson 1\tPerson 2\tPerson 3";
	private static final int OUTPUT_ROWS = 50;

	private MainView view;

	public Controller() {
		super();
	}

	private void start() {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					view = new MainView(Controller.this);
					view.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void generateRound2BtnPressed() {
		ArrayList<Student> students = new ArrayList<>(readInStudents());
		System.out.println("read in " + students.size() + " students");

		double minDistance = getMaxDistance(students) / 2;
		System.out.println("minDistance: " + minDistance);

		List<List<Student>> groups = assignStudents(students, minDistance);
		view.setRound2Text(getRound2Text(groups));
	}

	private String getRound2Text(List<List<Student>> groups) {
		StringBuilder sb = new StringBuilder(ROUND2_HEADLINE + "\n");
		for (int i = 0; i < groups.size(); i++) {
			List<Student> g = groups.get(i);

			sb.append((i + 1) + "\t"); // group ID
			sb.append(g.get(0).getName() + "\t");
			sb.append(g.get(1).getName() + "\t");

			if (g.size() == 3)
				sb.append(g.get(2).getName());
			sb.append("\n");
		}

		addBlankRows(sb, OUTPUT_ROWS - groups.size());

		return sb.toString();
	}

	private void addBlankRows(StringBuilder sb, int rowsCount) {
		for (int i = 0; i < rowsCount; i++)
			sb.append("\t\t\t\n");
	}

	private List<List<Student>> assignStudents(ArrayList<Student> allStudents,
			double minDistance) {
		System.out.println("\nattempting to assign students with minDistance: "
				+ minDistance);

		Set<Student> unassignedStudents = new HashSet<>(allStudents);

		try {
			return tryAssigningStudents(unassignedStudents, allStudents,
					minDistance);
		} catch (MinDistanceToBigException e) {
			return assignStudents(allStudents, minDistance * 0.9);
		}
	}

	private List<List<Student>> tryAssigningStudents(
			Set<Student> unassignedStudents, ArrayList<Student> allStudents,
			double minDistance) throws MinDistanceToBigException {

		ArrayList<List<Student>> groups = new ArrayList<>(
				allStudents.size() / 2 + 1);

		for (Student s1 : allStudents) {
			if (!unassignedStudents.contains(s1))
				continue;

			groups.add(assignPartner(s1, unassignedStudents, minDistance));

			if (unassignedStudents.size() == 1)
				assignLastStudentAsThird(unassignedStudents, groups);
		}

		return groups;
	}

	private List<Student> assignPartner(Student s1,
			Set<Student> unassignedStudents, double minDistance)
			throws MinDistanceToBigException {
		System.out.println("\t assigning a partner for: " + s1);

		List<Student> group = new ArrayList<>();

		double bestMatchDistance = Double.MAX_VALUE;
		Student bestMatch = null;

		for (Student s2 : unassignedStudents) {
			if (s1 == s2)
				continue;

			double dist = getDistance(s1, s2);
			if (dist >= minDistance && dist < bestMatchDistance) {
				bestMatchDistance = dist;
				bestMatch = s2;

				System.out.println(String.format(
						"\t\t potential match: %s (distance: %f)", bestMatch,
						bestMatchDistance));
			}
		}

		if (bestMatch == null)
			throw new MinDistanceToBigException();

		group.add(s1);
		group.add(bestMatch);
		unassignedStudents.remove(s1);
		unassignedStudents.remove(bestMatch);

		System.out.println(String.format(
				"\t\t => assigned best match: %s (distance: %f)", bestMatch,
				getDistance(s1, bestMatch)));

		return group;
	}

	private void assignLastStudentAsThird(Set<Student> unassignedStudents,
			List<List<Student>> groups) {
		if (unassignedStudents.size() != 1)
			throw new IllegalArgumentException(
					"there must be exactly one unassigned student remaining!");

		Student lastStudent = (new ArrayList<>(unassignedStudents)).get(0);

		System.out.println("assigning last student as a third to some group: "
				+ lastStudent);

		double maxAvDist = 0;
		List<Student> bestGroupMatch = null;

		for (List<Student> g : groups) {
			double dist1 = getDistance(lastStudent, g.get(0));
			double dist2 = getDistance(lastStudent, g.get(1));
			double avDist = (dist1 + dist2) / 2.0;
			if (avDist > maxAvDist) {
				maxAvDist = avDist;
				bestGroupMatch = g;

				System.out.println(String.format(
						"\t potential match: %s (average distance: %f)",
						bestGroupMatch, maxAvDist));
			}
		}

		System.out.println(String.format(
				"\t => last student assigned to: %s (average distance: %f)",
				bestGroupMatch, maxAvDist));

		bestGroupMatch.add(lastStudent);
		unassignedStudents.remove(lastStudent);
	}

	private double getMaxDistance(List<Student> students) {
		double maxDistance = 0;

		for (int i = 0; i < students.size() - 1; i++)
			for (int j = i + 1; j < students.size(); j++) {
				Student s1 = students.get(i), s2 = students.get(j);
				double dist = getDistance(s1, s2);
				if (dist > maxDistance)
					maxDistance = dist;
			}

		return maxDistance;
	}

	private double getDistance(Student s1, Student s2) {
		double rowDif = (s2.getRow() - s1.getRow()) * ROW_FACTOR;
		double seatDif = s2.getSeat() - s1.getSeat();
		return Math.sqrt(rowDif * rowDif + seatDif * seatDif);
	}

	private Set<Student> readInStudents() {
		Set<Student> students = new HashSet<>();

		Scanner sc = null;
		try {
			sc = new Scanner(view.getRound1Text());
			sc.nextLine(); // ignore headline

			while (sc.hasNext()) {
				String[] row = sc.nextLine().split("\t");

				if (row[1].isEmpty())
					break;

				students.add(createStudent(row, 1));
				students.add(createStudent(row, 2));

				if (!row[3].isEmpty())
					students.add(createStudent(row, 3));
			}
		} finally {
			if (sc != null)
				sc.close();
		}

		return students;
	}

	/**
	 * Creates a student from the given spreadsheet row.
	 * 
	 * @param row
	 *            the spreadsheet row, split up in cells
	 * @param no
	 *            the number of the student (1, 2, or 3)
	 * @return a student with data from the given row
	 */
	private Student createStudent(String[] row, int no) {
		return new Student(row[no], Integer.parseInt(row[4]),
				Integer.parseInt(row[5]) + no - 1);
	}
}
