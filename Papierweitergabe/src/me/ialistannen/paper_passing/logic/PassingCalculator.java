package me.ialistannen.paper_passing.logic;

import javafx.geometry.Point2D;
import me.ialistannen.paper_passing.model.Classroom;
import me.ialistannen.paper_passing.model.StudentsGridEntry;
import me.ialistannen.paper_passing.model.TableStudent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Calculates where the next paper should be passed to
 */
public class PassingCalculator {

	private Classroom room;

	private List<List<PaperPassingStudent>> splittedList = new ArrayList<>();

	private BinaryTree<String, PaperPassingStudent> resultingTree;

	/**
	 * @param room The room to use
	 */
	public PassingCalculator(Classroom room) {
		this.room = room;

		split();
		resultingTree = createTree();
	}

	/**
	 * @return The resulting tree
	 */
	public BinaryTree<String, PaperPassingStudent> getResultingTree() {
		return resultingTree;
	}

	private BinaryTree<String, PaperPassingStudent> createTree() {
		List<List<PaperPassingStudent>> easyList = splittedList.stream().filter(list -> list.size() > 1)
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

		Set<PaperPassingStudent> notProcessed = splittedList.stream().filter(list -> list.size() == 1)
				.flatMap(list -> list.stream()).collect(Collectors.toSet());

		easyList.stream().sequential().flatMap(Collection::stream).filter(student -> student.getPaperAmount() < 1)
				.forEach(tooFew::add);
		easyList.stream().sequential().flatMap(Collection::stream).filter(student -> student.getPaperAmount() >= 2)
				.forEach(tooMany::add);

		notProcessed.addAll(tooFew);
		notProcessed.addAll(tooMany);

		Set<PaperPassingStudent> copy = new HashSet<>(notProcessed);
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

		BinaryTree<String, PaperPassingStudent> tree = new BinaryTree<>();

		splittedList.stream().flatMap(Collection::stream)
				.sorted((o1, o2) -> o1.getBacking().getName().compareTo(o2.getBacking().getName())).forEach(st -> tree.add(st.getBacking().getName(), st.getTarget()));

//		String levelOrder = tree.toStringLevelOrder();
//		BinaryTree<String, PaperPassingStudent> newTree = new DSW<>(tree).balance();
//		String seconf = newTree.toStringLevelOrder();
//		System.out.println(levelOrder.replaceAll(",V=.+?\\]", "").replace("[T='", "").replace("']']", ""));
//		System.out.println(seconf.replaceAll(",V=.+?\\]", "").replace("[T='", "").replace("']']", ""));
		return tree;
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

	/**
	 * Splits the table in connected lines (and "dots")
	 */
	private void split() {
		StudentsGridEntry[][] data = room.getData();

		List<List<PaperPassingStudent>> horizontal = getHorizontal(new HashSet<>(), data).stream()
				.filter(list -> list.size() > 1).collect(Collectors.toList());
		List<List<PaperPassingStudent>> vertical = getVertical(horizontal.stream().flatMap(Collection::stream)
				.map(PaperPassingStudent::getBacking).collect(Collectors.toCollection(HashSet::new)), data);

		splittedList.addAll(vertical);
		splittedList.addAll(horizontal);
	}

	/**
	 * @param visited The visited list
	 * @param data    The data to use
	 *
	 * @return The horizontal connected lines
	 */
	private List<List<PaperPassingStudent>> getHorizontal(Collection<PaperPassingStudent> visited,
	                                                      StudentsGridEntry[][] data) {
		List<List<PaperPassingStudent>> horizontal = new ArrayList<>();

		List<PaperPassingStudent> tmpList = new ArrayList<>();
		for (int y = 0; y < data.length; y++) {
			for (int x = 0; x < data[y].length; x++) {
				StudentsGridEntry entry = data[y][x];
				if (visited.contains(entry)) {
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
				visited.add(student);
				tmpList.add(student);
			}
			if (tmpList.size() > 1) {
				horizontal.add(tmpList);
				tmpList = new ArrayList<>();
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

		List<PaperPassingStudent> tmpList = new ArrayList<>();
		for (int x = 0; x < data[0].length; x++) {
			for (int y = 0; y < data.length; y++) {
				StudentsGridEntry entry = data[y][x];
				if (visited.contains(entry)) {
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
				tmpList = new ArrayList<>();
			}
		}

		return horizontal;
	}
}
