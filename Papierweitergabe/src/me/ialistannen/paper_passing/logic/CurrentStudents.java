package me.ialistannen.paper_passing.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Saves the students, which are edited currently
 */
public class CurrentStudents {
	private static CurrentStudents ourInstance = new CurrentStudents();

	private List<PaperPassingStudent> original, modified;

	/**
	 * NO instantiation :P
	 * <br>There is no reflection ;)
	 */
	private CurrentStudents() {
	}

	/**
	 * The original students
	 *
	 * @return A list with the original students in the original order. A copy.
	 */
	public List<PaperPassingStudent> getOriginal() {
		return getListClone(original);
	}

	/**
	 * Makes a deep clone of the list
	 *
	 * @param input The input list
	 *
	 * @return The cloned list
	 */
	private List<PaperPassingStudent> getListClone(List<PaperPassingStudent> input) {
		List<PaperPassingStudent> copy = new ArrayList<>();
		for (PaperPassingStudent student : input) {
			copy.add((PaperPassingStudent) student.clone());
		}

		return copy;
	}

	/**
	 * The modified students
	 *
	 * @return A list with the modified students. A copy.
	 */
	public List<PaperPassingStudent> getModified() {
		return getListClone(modified);
	}

	/**
	 * The list <b>must not be reordered!</b>
	 * <br>Otherwise the next sorting will not work.
	 * <br>Rule of thumb: Let the transformation handle this and change nothing of the ordering!
	 *
	 * @param modified The new modified student list
	 */
	public void setModified(List<PaperPassingStudent> modified) {
		this.modified = getListClone(modified);
	}

	/**
	 * @param original The new students
	 */
	public void setOriginal(List<PaperPassingStudent> original) {
		this.original = getListClone(original);
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
