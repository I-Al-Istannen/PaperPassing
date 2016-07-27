package me.ialistannen.paper_passing.output;

import me.ialistannen.paper_passing.logic.BinaryTree;
import me.ialistannen.paper_passing.logic.PaperPassingStudent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Formats a binary search tree
 */
public class OutputFormatter {

	/**
	 * @param tree The tree to format
	 *
	 * @return The Output formatted as a list
	 */
	public static List<String> formatList(BinaryTree<String, PaperPassingStudent> tree) {
		List<String> list = new ArrayList<>();
		int maxSize = 0;
		for (BinaryTree<String, PaperPassingStudent> binaryTree : tree.getInOrderChildren()) {
			if (binaryTree.getKey().length() > maxSize) {
				maxSize = binaryTree.getKey().length();
			}

			list.add(binaryTree.getKey() + "|| -> ||" + binaryTree.getValue().getBacking().getName());
		}

		final int finalSize = maxSize;
		list = list.stream().map(string -> matchWhitespaces(string.split("\\|\\|")[0], string.split("\\|\\|")[1],
				string.split("\\|\\|")[2], finalSize)).collect(Collectors.toList());
		return list;
	}

	/**
	 * Writes the list to System.out
	 *
	 * @param list The list to write
	 */
	public static void output(List<String> list) {
		for (String string : list) {
			System.out.println(string);
		}
	}

	/**
	 * @param leftHand        The left side
	 * @param seperator       The seperator
	 * @param rightHand       The right side
	 * @param maxLeftHandSize The max size of the left side
	 *
	 * @return The adjusted string
	 */
	private static String matchWhitespaces(String leftHand, String seperator, String rightHand, int maxLeftHandSize) {
		int difference = maxLeftHandSize - leftHand.length();
		StringBuilder builder = new StringBuilder();
		builder.append(leftHand);
		builder.append(repeat(" ", difference));
		builder.append(seperator);
		builder.append(rightHand);

		return builder.toString();
	}

	/**
	 * @param string The String to repeat
	 * @param amount The amount to repeat it for
	 *
	 * @return The repeated string
	 */
	private static String repeat(String string, int amount) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < amount; i++) {
			builder.append(string);
		}

		return builder.toString();
	}
}
