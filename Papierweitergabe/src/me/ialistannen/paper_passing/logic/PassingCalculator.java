package me.ialistannen.paper_passing.logic;

import javafx.geometry.Point2D;
import me.ialistannen.paper_passing.model.Classroom;
import me.ialistannen.paper_passing.model.StudentsGridEntry;
import me.ialistannen.paper_passing.model.TableStudent;
import me.ialistannen.paper_passing.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static me.ialistannen.paper_passing.util.Util.getOrderedList;

/**
 * Calculates where the next paper should be passed to
 */
public class PassingCalculator {

	private final Classroom room;

	private final List<List<PaperPassingStudent>> splitList = new ArrayList<>();

	private final List<PaperPassingStudent> resultingList;

	/**
	 * @param room The room to use
	 */
	public PassingCalculator(Classroom room) {
		this.room = room;

		split();
		resultingList = createRightList();
	}

	/**
	 * @return The resulting List
	 */
	public List<PaperPassingStudent> getResultingList() {
		return resultingList;
	}

	/**
	 * Creates the list, if all students passed it one to the right
	 *
	 * @return A List with all the students in order
	 */
	private List<PaperPassingStudent> createRightList() {
		List<List<PaperPassingStudent>> easyList = splitList.stream().filter(list -> list.size() > 1)
				.collect(Collectors.toList());

		Set<PaperPassingStudent> tooFew = new HashSet<>(), tooMany = new HashSet<>();
		for (List<PaperPassingStudent> studentList : easyList) {
			for (int i = 0; i < studentList.size(); i++) {
				PaperPassingStudent student = studentList.get(i);

				int targetIndex = i + 1;
				if (targetIndex < studentList.size()) {
					student.decrementPaperAmount();

					PaperPassingStudent target = studentList.get(targetIndex);

					student.setTarget(target);

					target.incrementPaperAmount();
				}
			}
		}

		// Doesn't work with this: SHOULD WORK
		/*
		* Student | | Student | | Student
		* Where "| |" is an empty space
		*/

		List<PaperPassingStudent> notProcessed = getSingles(room.getData());

		splitList.add(new ArrayList<>(notProcessed));

		for (int i = 0; i < notProcessed.size(); i++) {
			PaperPassingStudent student = notProcessed.get(i);
			student.decrementPaperAmount();

			int targetIndex = i == notProcessed.size() - 1 ? 0 : i + 1;
			student.setTarget(notProcessed.get(targetIndex));

			notProcessed.get(targetIndex).incrementPaperAmount();
		}

		easyList.add(new ArrayList<>(notProcessed));
		notProcessed.clear();

		easyList.stream().sequential().flatMap(Collection::stream).filter(student -> student.getPaperAmount() < 1)
				.forEach(tooFew::add);
		easyList.stream().sequential().flatMap(Collection::stream).filter(student -> student.getPaperAmount() >= 2)
				.forEach(tooMany::add);

		notProcessed.addAll(tooFew);
		notProcessed.addAll(tooMany);

		Set<PaperPassingStudent> copy = new HashSet<>(notProcessed);
		// create the passing circles. May be more than one. Just sets the targets
		for (PaperPassingStudent student : notProcessed) {

			if (!copy.contains(student)) {
				continue;
			}

			int desiredPaperAmount = 1;
			if (student.getPaperAmount() == 0) {
				desiredPaperAmount = 2;
			} else if (student.getPaperAmount() == 2) {
				desiredPaperAmount = 0;
			}

			Optional<PaperPassingStudent> partner = findNearestFittingNode(student, copy, desiredPaperAmount);
			if (partner.isPresent()) {
				if (partner.get().getPaperAmount() > student.getPaperAmount()) {
					partner.get().setTarget(student);
				} else if (student.getPaperAmount() > partner.get().getPaperAmount()) {
					student.setTarget(partner.get());
				}
				copy.remove(student);
				copy.remove(partner.get());
			} else {
				System.out.println("no partner for: " + student.getBacking().getName());
			}
		}

		List<List<PaperPassingStudent>> circles = getCircles(splitList.stream().flatMap(Collection::stream).collect(Collectors.toList()));

		// connect them!
		for (int i = 0; i < circles.size(); i++) {
			List<PaperPassingStudent> firstList = circles.get(i);
			// has more elements
			if (circles.size() > i + 1 && !firstList.isEmpty()) {
				List<PaperPassingStudent> secondList = circles.get(i + 1);
				if (secondList.isEmpty()) {
					continue;
				}
				PaperPassingStudent firstFromFirst = firstList.get(0);
				PaperPassingStudent lastFromFirst = firstList.get(firstList.size() - 1);
				PaperPassingStudent firstFromSecond = secondList.get(0);
				PaperPassingStudent lastFromSecond = secondList.get(secondList.size() - 1);

				lastFromFirst.setTarget(firstFromSecond);
				lastFromSecond.setTarget(firstFromFirst);

				List<PaperPassingStudent> merged = Util.getOrderedList(firstFromFirst);
				circles.set(i + 1, merged);
			}
		}


		if (splitList.isEmpty() || splitList.get(0).isEmpty()) {
			return Collections.emptyList();
		}
		// create the ordered List. Cosmetics :P
		return getOrderedList(splitList.get(0).get(0));
	}

	/**
	 * @param student            The student to search a partner for
	 * @param list               The list with students
	 * @param desiredPaperAmount The paper amount the partners should have
	 *
	 * @return The students if one was found
	 */
	private Optional<PaperPassingStudent> findNearestFittingNode(PaperPassingStudent student,
	                                                             Collection<PaperPassingStudent> list, int desiredPaperAmount) {
		double minDistance = Double.MAX_VALUE;
		PaperPassingStudent foundStudent = null;
		for (PaperPassingStudent paperPassingStudent : list) {
			double distance = student.getLocation().distance(paperPassingStudent.getLocation());
			if (paperPassingStudent.getPaperAmount() != desiredPaperAmount) {
				continue;
			}
			if (distance < minDistance) {
				minDistance = distance;
				foundStudent = paperPassingStudent;
			}
		}

		return Optional.ofNullable(foundStudent);
	}

	private List<List<PaperPassingStudent>> getCircles(List<PaperPassingStudent> input) {
		List<List<PaperPassingStudent>> lists = new ArrayList<>();
		List<PaperPassingStudent> tmp = new ArrayList<>();
		for (int i = 0; i < input.size(); i++) {
			PaperPassingStudent paperPassingStudent = input.get(i);
			while (!tmp.contains(paperPassingStudent)) {
				tmp.add(paperPassingStudent);
				paperPassingStudent = paperPassingStudent.getTarget();
			}
			input.removeAll(tmp);
			i = -1;
			lists.add(tmp);
			// reassign to not clear the original list (pass by reference)
			tmp = new ArrayList<>();
		}

		return lists;
	}

	/**
	 * Splits the table in connected lines (and "dots")
	 */
	private void split() {
		StudentsGridEntry[][] data = room.getData();

		List<List<PaperPassingStudent>> horizontal = getHorizontal(data);
		List<List<PaperPassingStudent>> vertical = getVertical(
				horizontal.stream()
						.flatMap(Collection::stream)
						.map(PaperPassingStudent::getBacking)
						.collect(Collectors.toCollection(HashSet::new))
				, data);

		splitList.addAll(vertical);
		splitList.addAll(horizontal);
	}

	/**
	 * All the single students
	 *
	 * @param data The data to use
	 *
	 * @return The Singles
	 */
	private List<PaperPassingStudent> getSingles(StudentsGridEntry[][] data) {
		Set<PaperPassingStudent> pairs = splitList.stream().flatMap(Collection::stream).collect(Collectors.toSet());
		List<PaperPassingStudent> singles = new ArrayList<>();
		singles.addAll(getAll(data));
		singles.removeAll(pairs);
		return singles;
	}

	private Set<PaperPassingStudent> getAll(StudentsGridEntry[][] data) {
		Set<PaperPassingStudent> students = new HashSet<>();
		for (int y = 0; y < data.length; y++) {
			for (int x = 0; x < data[y].length; x++) {
				StudentsGridEntry entry = data[y][x];

				if (entry instanceof TableStudent) {
					PaperPassingStudent student = new PaperPassingStudent((TableStudent) entry, new Point2D(x, y));
					students.add(student);
				}
			}
		}

		return students;
	}

	/**
	 * @param data The data to use
	 *
	 * @return The horizontal connected lines
	 */
	private List<List<PaperPassingStudent>> getHorizontal(StudentsGridEntry[][] data) {
		List<List<PaperPassingStudent>> horizontal = new ArrayList<>();

		for (int y = 0; y < data.length; y++) {
			List<PaperPassingStudent> tmpList = new ArrayList<>();
			for (int x = 0; x < data[y].length; x++) {
				StudentsGridEntry entry = data[y][x];
				if (!(entry instanceof TableStudent)) {
					if (tmpList.size() > 1) {
						horizontal.add(tmpList);
					}
					tmpList = new ArrayList<>();
					continue;
				}
				PaperPassingStudent student = new PaperPassingStudent((TableStudent) entry, new Point2D(x, y));
				tmpList.add(student);
			}
			if (tmpList.size() > 1) {
				horizontal.add(tmpList);
			}
		}

		return horizontal;
	}

	/**
	 * @param visited The visited list
	 * @param data    The data to use
	 *
	 * @return The vertical connected lines
	 */
	private List<List<PaperPassingStudent>> getVertical(Collection<TableStudent> visited, StudentsGridEntry[][] data) {
		List<List<PaperPassingStudent>> horizontal = new ArrayList<>();

		for (int x = 0; x < data[0].length; x++) {
			List<PaperPassingStudent> tmpList = new ArrayList<>();
			for (int y = 0; y < data.length; y++) {
				StudentsGridEntry entry = data[y][x];
				if (entry instanceof TableStudent && visited.contains(entry)) {
					continue;
				}
				if (!(entry instanceof TableStudent)) {
					if (tmpList.size() > 1) {
						horizontal.add(tmpList);
					}
					tmpList = new ArrayList<>();
					continue;
				}
				PaperPassingStudent student = new PaperPassingStudent((TableStudent) entry, new Point2D(x, y));
				visited.add((TableStudent) entry);
				tmpList.add(student);
			}
			if (tmpList.size() > 1) {
				horizontal.add(tmpList);
			}
		}

		return horizontal;
	}
}
