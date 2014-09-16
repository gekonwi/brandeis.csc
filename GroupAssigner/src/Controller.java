import java.awt.EventQueue;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CancellationException;

import javax.swing.JOptionPane;

public class Controller implements MainView.MainViewController {
	public class MinDistanceToBigException extends Exception {

	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Controller c = new Controller();
		c.start();
	}

	private static final double ROW_FACTOR = 1.5;
	private static final String MAP_LINKS_ROUND2_FILE_NAME = "map_links_round2.txt";
	private static final String ROUND2_HEADLINE = "Group ID\tPerson 1\tPerson 2\tPerson 3\tYour Map";

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

		double minDistance = getMaxDistance(students) / 2;

		try {
			List<Group> groups = assignStudents(students, minDistance);
			view.setRound2Text(getRound2Text(groups));
		} catch (CancellationException e) {
			e.printStackTrace();
		}
	}

	private String getRound2Text(List<Group> groups) {
		StringBuilder sb = new StringBuilder(ROUND2_HEADLINE + "\n");
		for (int i = 0; i < groups.size(); i++) {
			Group g = groups.get(i);

			sb.append(i + 1); // group ID
			sb.append("\t");

			sb.append(g.getStudents().get(0).getName());
			sb.append("\t");

			sb.append(g.getStudents().get(1).getName());
			sb.append("\t");

			if (g.getStudents().size() == 3)
				sb.append(g.getStudents().get(2).getName());
			sb.append("\t");

			sb.append(g.getMapLink());
			sb.append("\n");
		}

		return sb.toString();
	}

	private List<Group> assignStudents(ArrayList<Student> allStudents,
			double minDistance) {
		Set<Student> unassignedStudents = new HashSet<>(allStudents);

		try {
			return tryAssigningStudents(unassignedStudents, allStudents,
					minDistance);
		} catch (MinDistanceToBigException e) {
			return assignStudents(allStudents, minDistance * 0.9);
		}
	}

	private List<Group> tryAssigningStudents(Set<Student> unassignedStudents,
			ArrayList<Student> allStudents, double minDistance)
			throws MinDistanceToBigException {

		ArrayList<Group> groups = new ArrayList<>(allStudents.size() / 2 + 1);

		LinkedList<String> links = readRound2Links();

		for (Student s1 : allStudents) {
			if (!unassignedStudents.contains(s1))
				continue;

			groups.add(assignPartner(links.poll(), s1, unassignedStudents,
					minDistance));

			if (unassignedStudents.size() == 1)
				assignLastStudentAsThird(unassignedStudents, groups);
		}

		return groups;
	}

	private Group assignPartner(String mapLink, Student s1,
			Set<Student> unassignedStudents, double minDistance)
			throws MinDistanceToBigException {

		Group group = new Group(mapLink);

		double bestMatchDistance = Double.MAX_VALUE;
		Student bestMatch = null;

		for (Student s2 : unassignedStudents) {
			if (s1 == s2)
				continue;

			double dist = getDistance(s1, s2);
			if (dist >= minDistance && dist < bestMatchDistance) {
				bestMatchDistance = dist;
				bestMatch = s2;
			}
		}

		if (bestMatch == null)
			throw new MinDistanceToBigException();

		group.getStudents().add(s1);
		group.getStudents().add(bestMatch);
		unassignedStudents.remove(s1);
		unassignedStudents.remove(bestMatch);

		return group;
	}

	private void assignLastStudentAsThird(Set<Student> unassignedStudents,
			ArrayList<Group> groups) {
		if (unassignedStudents.size() != 1)
			throw new IllegalArgumentException(
					"there must be exactly one unassigned student remaining!");

		Student lastStudent = (new ArrayList<>(unassignedStudents)).get(0);

		double maxAvDist = 0;
		Group bestMatch = null;

		for (Group g : groups) {
			double dist1 = getDistance(lastStudent, g.getStudents().get(0));
			double dist2 = getDistance(lastStudent, g.getStudents().get(1));
			double avDist = (dist1 + dist2) / 2.0;
			if (avDist > maxAvDist) {
				maxAvDist = avDist;
				bestMatch = g;
			}
		}

		bestMatch.getStudents().add(lastStudent);
	}

	private LinkedList<String> readRound2Links() {
		LinkedList<String> links = null;
		try {
			links = new LinkedList<>(Files.readAllLines(
					Paths.get(MAP_LINKS_ROUND2_FILE_NAME),
					Charset.forName("UTF-8")));
		} catch (IOException e) {
			String title = "Error";
			String msg = "The file containing the map links for round 2 could not be read in.";
			JOptionPane.showMessageDialog(view, msg, title,
					JOptionPane.INFORMATION_MESSAGE);

			throw new CancellationException();
		}
		return links;
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
		double dist = Math.sqrt(rowDif * rowDif + seatDif * seatDif);
		return dist;
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
