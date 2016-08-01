package me.ialistannen.paper_passing.logic;

import javafx.geometry.Point2D;
import me.ialistannen.paper_passing.model.TableStudent;

/**
 * A student passing a paper
 */
@SuppressWarnings("WeakerAccess")
public class PaperPassingStudent {

	private int paperAmount = 1;
	private final TableStudent backing;
	private PaperPassingStudent target;
	private final Point2D location;

	/**
	 * @param backing  The {@link TableStudent} backing this
	 * @param location The location in the grid
	 */
	public PaperPassingStudent(TableStudent backing, Point2D location) {
		this.backing = backing;
		this.location = location;
	}

	/**
	 * @return The paper amount
	 */
	public int getPaperAmount() {
		return paperAmount;
	}

	/**
	 * Reduces the paper amount by one
	 */
	public void decrementPaperAmount() {
		paperAmount--;
	}

	/**
	 * Reduces the paper amount by one
	 */
	public void incrementPaperAmount() {
		paperAmount++;
	}

	/**
	 * @return The underlying student
	 */
	public TableStudent getBacking() {
		return backing;
	}

	/**
	 * @return The target or null
	 */
	public PaperPassingStudent getTarget() {
		return target;
	}

	/**
	 * @return The Location of this student
	 */
	public Point2D getLocation() {
		return location;
	}

	/**
	 * @param target The new target
	 */
	public void setTarget(PaperPassingStudent target) {
		this.target = target;
	}

	@Override
	public String toString() {
		return "PaperPassingStudent [paperAmount=" + paperAmount + ", backing=" + backing.getName() + ", target=" + (target == null ? "null" : target.getBacking().getName())
				+ ", location=" + location + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((backing == null) ? 0 : backing.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PaperPassingStudent other = (PaperPassingStudent) obj;
		if (backing == null) {
			if (other.backing != null) {
				return false;
			}
		} else if (!backing.equals(other.backing)) {
			return false;
		}
		return true;
	}
}
