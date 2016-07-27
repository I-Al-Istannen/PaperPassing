package me.ialistannen.paper_passing.output;

import java.util.function.Function;

import me.ialistannen.paper_passing.logic.BinaryTree;
import me.ialistannen.paper_passing.logic.PaperPassingStudent;

/**
 * Changes the output tree to a different way of passing
 */
public enum OutputTransformation {

	/**
	 * Passes the papers the other way round
	 */
	LEFT(data -> {
		BinaryTree<String, PaperPassingStudent> tree = new BinaryTree<>();
		for (BinaryTree<String, PaperPassingStudent> entry : data.getChildren()) {
			System.out.println(entry.getKey() + " " + entry.getValue().getBacking().getName());
			System.out.println(data.find(entry.getKey()).get().getValue());
			tree.add(entry.getKey(), entry.getValue());
		}
		System.out.println();
		return tree;
	});

	private Function<BinaryTree<String, PaperPassingStudent>, BinaryTree<String, PaperPassingStudent>> function;

	/**
	 * @param function
	 *            The function to use
	 */
	private OutputTransformation(
			Function<BinaryTree<String, PaperPassingStudent>, BinaryTree<String, PaperPassingStudent>> function) {
		this.function = function;
	}

	/**
	 * @param data
	 *            The data to use
	 * @return The data after the transformation
	 */
	public BinaryTree<String, PaperPassingStudent> applyFunction(BinaryTree<String, PaperPassingStudent> data) {
		return function.apply(data);
	}
}
