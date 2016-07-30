package me.ialistannen.paper_passing.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import me.ialistannen.paper_passing.logic.CurrentStudents;
import me.ialistannen.paper_passing.logic.PaperPassingStudent;
import me.ialistannen.paper_passing.output.OutputTransformation;
import me.ialistannen.paper_passing.util.Util;

import java.awt.Toolkit;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * The controller for the ApplyTransformationWindow
 */
public class ApplyTransformationsWindowController {


	@FXML
	private ListView<PaperPassingStudent> studentList;

	@FXML
	private ComboBox<OutputTransformation> transformationPicker;

	@FXML
	private Spinner<Integer> amountSpinner;


	@FXML
	private void initialize() {
		amountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

		{
			NumberFormat formatter = NumberFormat.getInstance();
			UnaryOperator<TextFormatter.Change> filter = change -> {
				if (change.isContentChange()) {
					ParsePosition parsePosition = new ParsePosition(0);
					formatter.parse(change.getControlNewText(), parsePosition);
					if (parsePosition.getIndex() == 0 || parsePosition.getIndex() < change.getControlNewText().length()) {
						Toolkit.getDefaultToolkit().beep();
						return null;
					}
				}
				return change;
			};
			TextFormatter<Integer> customFormatter = new TextFormatter<>(
					new IntegerStringConverter(), 1, filter);

			amountSpinner.getEditor().setTextFormatter(customFormatter);
		}

		transformationPicker.setConverter(new StringConverter<OutputTransformation>() {
			@Override
			public String toString(OutputTransformation object) {
				return Util.getNiceNameForConstant(object.name());
			}

			@Override
			public OutputTransformation fromString(String string) {
				return OutputTransformation.valueOf(string.toUpperCase().replace(" ", "_"));
			}
		});

		transformationPicker.getItems().addAll(OutputTransformation.values());

		studentList.setCellFactory(param -> new ListCell<PaperPassingStudent>() {
			@Override
			protected void updateItem(PaperPassingStudent item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setGraphic(null);
				} else {
					setText(item.getBacking().getName());
				}
			}
		});

		studentList.getItems().addAll(CurrentStudents.getInstance().getOriginal());
	}

	@FXML
	void onAccept(@SuppressWarnings("UnusedParameters") ActionEvent event) {
		System.out.println("ApplyTransformationsWindowController.onAccept()");
	}

	@FXML
	void onCancel(@SuppressWarnings("UnusedParameters") ActionEvent event) {
		System.out.println("ApplyTransformationsWindowController.onCancel()");
	}


	@FXML
	void onClearPreview(@SuppressWarnings("UnusedParameters") ActionEvent event) {
		studentList.getItems().setAll(CurrentStudents.getInstance().getOriginal());
	}

	@FXML
	void onPreviewAbove(@SuppressWarnings("UnusedParameters") ActionEvent event) {
		if (updateModifiedStudents()) {
			studentList.getItems().setAll(Util.getOrderedList(CurrentStudents.getInstance().getModified().get(0)));
		}
	}

	/**
	 * Applies the selected transformation
	 *
	 * @return True if the modified students were updated
	 */
	private boolean updateModifiedStudents() {
		int amount = amountSpinner.getValue();
		OutputTransformation transformation = transformationPicker.getValue();
		if (transformation == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Transformation not set");
			alert.setHeaderText("Please select a transformation by clicking on the combo box.");
			alert.show();
			return false;
		}
		List<PaperPassingStudent> list = CurrentStudents.getInstance().getOriginal();
		for (int i = 0; i < amount; i++) {
			list = transformation.applyFunction(list);
		}
		CurrentStudents.getInstance().setModified(list);

		return true;
	}
}
