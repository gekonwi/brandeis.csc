import java.util.ArrayList;

public class Group {
	private final ArrayList<Student> students = new ArrayList<>();
	private final String mapLink;

	public Group(String mapLink) {
		super();
		this.mapLink = mapLink;
	}

	public ArrayList<Student> getStudents() {
		return students;
	}

	public String getMapLink() {
		return mapLink;
	}

}
