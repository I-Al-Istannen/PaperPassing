package me.ialistannen.paper_passing.output;

import me.ialistannen.paper_passing.logic.PaperPassingStudent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Formats a binary search tree
 */
public class OutputFormatter {

	/**
	 * @param passingStudentList The passingStudentList to format
	 *
	 * @return The Output formatted as a list
	 */
	public static List<String> formatList(List<PaperPassingStudent> passingStudentList) {
		List<String> list = new ArrayList<>();
		int maxSize = 0;
		for (PaperPassingStudent student : passingStudentList) {
			if (student.getBacking().getName().length() > maxSize) {
				maxSize = student.getBacking().getName().length();
			}

			list.add(student.getBacking().getName() + "|| -> ||" + student.getTarget().getBacking().getName());
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
	 * @param separator       The separator
	 * @param rightHand       The right side
	 * @param maxLeftHandSize The max size of the left side
	 *
	 * @return The adjusted string
	 */
	private static String matchWhitespaces(String leftHand, String separator, String rightHand, int maxLeftHandSize) {
		int difference = maxLeftHandSize - leftHand.length();

		return leftHand +
				repeat(" ", difference) +
				separator +
				rightHand;
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
