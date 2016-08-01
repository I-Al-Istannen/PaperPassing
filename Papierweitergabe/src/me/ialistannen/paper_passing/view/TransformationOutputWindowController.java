package me.ialistannen.paper_passing.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import me.ialistannen.paper_passing.PaperPassing;
import me.ialistannen.paper_passing.logic.CurrentStudents;
import me.ialistannen.paper_passing.logic.PaperPassingStudent;
import me.ialistannen.paper_passing.output.OutputFormatter;
import me.ialistannen.paper_passing.util.Util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A controller for the output window
 */
@SuppressWarnings("WeakerAccess") // For the fxml public is nicer
public class TransformationOutputWindowController {

	@FXML
	private BorderPane borderPane;

	@SuppressWarnings("CanBeFinal")
	@FXML
	private TextArea centerTextArea;

	@FXML
	private void initialize() {
		centerTextArea.setFont(Font.font("Monospaced"));
		centerTextArea.setWrapText(true);

		onOutputTypeList(null);
	}

	@FXML
	void onExport(ActionEvent event) {
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*.txt"));
		chooser.setSelectedExtensionFilter(chooser.getExtensionFilters().get(0));
		File file = chooser.showSaveDialog(PaperPassing.getInstance().getPrimaryStage());

		if (file == null) {
			return;
		}

		try {
			Files.write(file.toPath(),
					Arrays.asList(centerTextArea.getText().split("\n")),
					StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Saved file");
			{
				Text start = new Text("Successfully saved the text to \""), end = new Text("\".");
				Hyperlink link = new Hyperlink(file.getAbsolutePath());
				link.setOnAction(action -> {
					try {
						PaperPassing.getInstance().getHostServices()
								.showDocument(file.getAbsoluteFile().toURI().toURL().toString());
					} catch (MalformedURLException e) {
						// Don't know when this can happen. The file is valid.
						e.printStackTrace();
						Util.showNonBlockingErrorAlert("Couldn't open file, as the URL is invalid.",
								"Error while opening the file",
								Util.getExceptionStackTrace(e));
					}
				});
				TextFlow textFlow = new TextFlow(start, link, end);
				alert.getDialogPane().setHeader(textFlow);
			}
			alert.show();
		} catch (IOException e) {
			e.printStackTrace();
			Util.showNonBlockingErrorAlert("Couldn't save file", "Saving gone wrong", Util.getExceptionStackTrace(e));
		}
	}

	@FXML
	void onOutputTypeConnectedList(ActionEvent event) {
		String text =
				OutputFormatter.formatList(
						Util.getOrderedList(CurrentStudents.getInstance().getModified().get(0))
				).stream()
						.collect(Collectors.joining("§§"));
		// remove duplicates
		// otherwise it would look like this:
		// 5 -> 6§§6 -> 7§§7 -> 0§§0 -> 1§§1 -> 2§§2 -> 3§§3 -> 4§§4 -> 5
		text = text.replaceAll("§§.+?(?=->)", " ");
		setText(Collections.singletonList(text));
	}

	@FXML
	void onOutputTypeList(ActionEvent event) {
		List<PaperPassingStudent> studentList = CurrentStudents.getInstance().getModified();
		setText(OutputFormatter.formatList(studentList));
	}

	@FXML
	void onPrint(ActionEvent e) {
		// New thread as advised
		new Thread(() -> {
			PrinterJob job = PrinterJob.createPrinterJob();
			job.getJobSettings().setJobName("Paper Passing Printing results");
			if (!job.showPrintDialog(PaperPassing.getInstance().getPrimaryStage())) {
				return;
			}
			Label text = new Label(centerTextArea.getText());
			Util.blockingRunOnFxThreadAnd(() -> {
				text.setFont(centerTextArea.getFont());
				text.setWrapText(true);
				text.setPrefSize(centerTextArea.getWidth(), centerTextArea.getHeight());

				text.setPrefSize(job.getPrinter().getDefaultPageLayout().getPrintableWidth() - 20, job.getPrinter().getDefaultPageLayout().getPrintableHeight() - 20);

				// pre-render the text to make it appear completely on the print
				Alert alert = new Alert(Alert.AlertType.INFORMATION);

				alert.getDialogPane().setContent(text);
				alert.show();
				alert.hide();
			});

			boolean success = job.printPage(text);
			if (success) {
				job.endJob();
			}
			Platform.runLater(() -> {
				if (success) {
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
					alert.setTitle("Submitted Job");
					alert.setHeaderText("The print job was successfully submitted to the Operating System!");
					alert.show();
				} else {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Couldn't submit job");
					alert.setHeaderText("An error occurred while submitting the print job to the Operating System!");
					alert.show();
				}
			});
		}, "Printer").start();
	}

	/**
	 * Sets the text. Thread safe.
	 */
	private void setText(List<String> text) {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> setText(text));
			return;
		}
		centerTextArea.clear();
		for (String s : text) {
			centerTextArea.appendText(s + "\n");
		}
	}
}
