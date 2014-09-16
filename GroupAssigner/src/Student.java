public class Student {
	private final String name;
	private final int row;
	private final int seat;

	public Student(String name, int row, int seat) {
		super();
		this.name = name;
		this.row = row;
		this.seat = seat;
	}

	public String getName() {
		return name;
	}

	public int getRow() {
		return row;
	}

	public int getSeat() {
		return seat;
	}
}
