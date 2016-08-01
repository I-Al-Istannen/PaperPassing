package me.ialistannen.paper_passing.output;

import com.sun.istack.internal.NotNull;
import me.ialistannen.paper_passing.logic.PaperPassingStudent;

import java.util.List;
import java.util.function.Function;

/**
 * Changes the output tree to a different way of passing
 */
public enum OutputTransformation {

	/**
	 * Passes the papers one to the right
	 */
	RIGHT(data -> {
		// if the people pass it to themselves, skip it.
		do {
			for (PaperPassingStudent student : data) {
				int targetIndex = data.indexOf(student.getTarget()) + 1;
				if (targetIndex >= data.size()) {
					targetIndex = data.size() - targetIndex;
				}

				student.setTarget(data.get(targetIndex));
			}
		} while (data.get(0).equals(data.get(0).getTarget()));


		{
			PaperPassingStudent first = data.get(0);
			data.remove(first);
			data.add(first);
		}

		return data;
	}),
	/**
	 * Passes the papers one to the left
	 */
	LEFT(data -> {
		if (data.isEmpty()) {
			return data;
		}

		// if the people pass it to themselves, skip it.
		do {
			for (PaperPassingStudent student : data) {
				int targetIndex = data.indexOf(student.getTarget()) - 1;
				if (targetIndex < 0) {
					targetIndex = data.size() + targetIndex;
				}

				student.setTarget(data.get(targetIndex));
			}
		} while (data.get(0).equals(data.get(0).getTarget()));

		{
			PaperPassingStudent last = data.get(data.size() - 1);
			data.remove(last);
			data.add(0, last);
		}

		// don't oder that list. It destroys the indices
		return data;
	});

	private final Function<List<PaperPassingStudent>, List<PaperPassingStudent>> function;

	/**
	 * @param function The function to use
	 */
	OutputTransformation(Function<List<PaperPassingStudent>, List<PaperPassingStudent>> function) {
		this.function = function;
	}

	/**
	 * @param data The data to use
	 *
	 * @return The data after the transformation
	 */
	public List<PaperPassingStudent> applyFunction(@NotNull List<PaperPassingStudent> data) {
		return function.apply(data);
	}
}
