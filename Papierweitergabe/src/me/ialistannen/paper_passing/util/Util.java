package me.ialistannen.paper_passing.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import me.ialistannen.paper_passing.PaperPassing;
import me.ialistannen.paper_passing.logic.PaperPassingStudent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;

/**
 * Some static utility methods
 */
public class Util {

	/**
	 * Tries to order the list, so that each student is followed by his target
	 * <br>E.g:
	 * <br>Leo -> Linda
	 * <br>Linda -> Simone
	 * <br>==> Leo, Linda, Simone
	 *
	 * @param start The start student
	 *
	 * @return The sorted list, If the start contains multiple circles, this list will not be complete!
	 */
	public static List<PaperPassingStudent> getOrderedList(PaperPassingStudent start) {
		List<PaperPassingStudent> ordered = new ArrayList<>();
		while (!ordered.contains(start)) {
			ordered.add(start);
			start = start.getTarget();
		}

		return ordered;
	}

	/**
	 * Replaces "_" with spaces and writes in upper case after a space.
	 * <br>E.g.
	 * <br>DIAMOND_SWORD ==> "Diamond Sword"
	 *
	 * @param constant The constant to format
	 *
	 * @return The formatted String
	 */
	public static String getNiceNameForConstant(String constant) {
		StringBuilder builder = new StringBuilder();
		boolean upperCase = true;
		for (char c : constant.toCharArray()) {
			if (c == '_') {
				upperCase = true;
				builder.append(" ");
				continue;
			}
			if (upperCase) {
				builder.append(Character.toUpperCase(c));
				upperCase = false;
			} else {
				builder.append(Character.toLowerCase(c));
			}
		}

		return builder.toString();
	}

	/**
	 * Shows a confirmation dialog blocking the entire application
	 *
	 * @param header The header text
	 * @param title  The title
	 *
	 * @return True if the user accepted.
	 */
	public static boolean showConfirmationDialog(String header, String title) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.initOwner(PaperPassing.getInstance().getPrimaryStage());
		Optional<ButtonType> buttonType = alert.showAndWait();
		return buttonType.isPresent() && buttonType.get() == ButtonType.OK;
	}

	/**
	 * The default file chooser with the default file extension
	 *
	 * @return The {@link FileChooser}
	 */
	public static FileChooser getDefaultFileChooser() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Paper passer save files", "*.PP_SAVE"));
		fileChooser.setSelectedExtensionFilter(fileChooser.getExtensionFilters().get(0));
		return fileChooser;
	}

	/**
	 * Shows a non blocking error dialog with the specified header, title and expandable content
	 *
	 * @param header            The header text
	 * @param title             The title
	 * @param expandableContent The expandable content
	 */
	public static void showNonBlockingErrorAlert(String header, String title, String expandableContent) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		ScrollPane scrollPane = new ScrollPane(new Text(expandableContent));
		alert.getDialogPane().setExpandableContent(scrollPane);
		alert.initOwner(PaperPassing.getInstance().getPrimaryStage());
		alert.show();
	}

	/**
	 * Converts the StackTrace to a String
	 *
	 * @param e The exception to get the stacktrace for
	 *
	 * @return The Stacktrace as a String
	 */
	public static String getExceptionStackTrace(Exception e) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		return stringWriter.toString();
	}

	/**
	 * Runs a runnable on the fx thread and waits for the completion
	 *
	 * @param runnable The runnable to run
	 */
	public static void blockingRunOnFxThreadAnd(Runnable runnable) {
		if (Platform.isFxApplicationThread()) {
			runnable.run();
			return;
		}

		try {
			Semaphore semaphore = new Semaphore(1);
			semaphore.acquire();
			Platform.runLater(() -> {
				runnable.run();
				semaphore.release();
			});
			semaphore.acquireUninterruptibly();
		} catch (InterruptedException e) {
			e.printStackTrace();
			showNonBlockingErrorAlert("Error running blocking FXThread", "Error :/", getExceptionStackTrace(e));
		}
	}
}
