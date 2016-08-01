package me.ialistannen.paper_passing.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Saves the students, which are edited currently
 */
public class CurrentStudents {
	private static final CurrentStudents ourInstance = new CurrentStudents();

	private List<PaperPassingStudent> modified;
	private Map<String, PaperPassingStudent> originalStudents;
	private Map<String, String> originalState;

	/**
	 * NO instantiation :P
	 * <br>There is no reflection ;)
	 */
	private CurrentStudents() {
	}

	/**
	 * Sets the current students
	 * <br>A snapshot will be taken and used to allow restoring of the <b>target</b>.
	 *
	 * @param students The students to use
	 */
	public void setOriginalStudents(List<PaperPassingStudent> students) {
		this.originalStudents = new LinkedHashMap<>();
		for (PaperPassingStudent student : students) {
			originalStudents.put(student.getBacking().getName(), student);
		}

		this.modified = new ArrayList<>(students);

		snapshot();
	}

	/**
	 * @return The original students.
	 */
	public List<PaperPassingStudent> getOriginalStudents() {
		return new ArrayList<>(originalStudents.values().stream().collect(Collectors.toList()));
	}

	/**
	 * Sets the modified students. No snapshot will be taken
	 *
	 * @param students The modified students
	 */
	public void setModifiedStudents(List<PaperPassingStudent> students) {
		modified = new ArrayList<>(students);
	}

	/**
	 * @return The modified students.
	 */
	public List<PaperPassingStudent> getModified() {
		return modified == null ? null : new ArrayList<>(modified);
	}


	/**
	 * Restores the relations to the last snapshot
	 */
	public void revertToOriginal() {
		for (PaperPassingStudent student : getOriginalStudents()) {
			String target = originalState.get(student.getBacking().getName());
			if (target == null) {
				student.setTarget(null);
			} else {
				student.setTarget(originalStudents.get(target));
			}
		}
	}

	private void snapshot() {
		originalState = new HashMap<>();
		for (PaperPassingStudent student : getOriginalStudents()) {
			String targetName;
			if (student.getTarget() == null) {
				targetName = null;
			} else {
				targetName = student.getTarget().getBacking().getName();
			}
			originalState.put(student.getBacking().getName(), targetName);
		}

	}

	/**
	 * An instance of this class
	 *
	 * @return The instance
	 */
	public static CurrentStudents getInstance() {
		return ourInstance;
	}
}
