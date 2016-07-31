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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import me.ialistannen.paper_passing.logic.CurrentStudents;
import me.ialistannen.paper_passing.logic.PaperPassingStudent;
import me.ialistannen.paper_passing.output.OutputFormatter;
import me.ialistannen.paper_passing.output.OutputTransformation;
import me.ialistannen.paper_passing.util.Util;

import java.awt.Toolkit;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * The controller for the ApplyTransformationWindow
 */
public class ApplyTransformationsWindowController {

	private Stage myStage;

	@FXML
	private ListView<String> studentList;

	@FXML
	private ComboBox<OutputTransformation> transformationPicker;

	@FXML
	private Spinner<Integer> amountSpinner;

	@FXML
	private void initialize() {
		amountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));

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
					new IntegerStringConverter(), 0, filter);

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

		studentList.setCellFactory(param -> new ListCell<String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setGraphic(null);
				} else {
					setText(item);
					// make the names nicely align. The Output formatter inserts the spaces
					// we make them appear correctly
					setFont(Font.font("Monospaced"));
				}
			}
		});

		setItems(OutputFormatter.formatList(CurrentStudents.getInstance().getOriginalStudents()));
	}

	@FXML
	void onAccept(ActionEvent event) {
		System.out.println("ApplyTransformationsWindowController.onAccept()");
	}

	@FXML
	void onCancel(ActionEvent event) {
		myStage.hide();
	}


	@FXML
	void onClearPreview(ActionEvent event) {
		amountSpinner.getValueFactory().setValue(0);
		transformationPicker.getSelectionModel().select(OutputTransformation.RIGHT);
		// reset to basis
		updateModifiedStudents();
		setItems(OutputFormatter.formatList(CurrentStudents.getInstance().getModified()));

		// reset amount picker to prompt text
		transformationPicker.getSelectionModel().clearSelection();
	}

	@FXML
	void onPreviewAbove(ActionEvent event) {
		if (updateModifiedStudents()) {
			setItems(OutputFormatter.formatList(CurrentStudents.getInstance().getModified()));
		}
	}

	private void setItems(List<String> items) {
		Collections.sort(items);
		studentList.getItems().setAll(items);
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
		CurrentStudents.getInstance().revertToOriginal();
		List<PaperPassingStudent> list = CurrentStudents.getInstance().getOriginalStudents();

		for (int i = 0; i < amount; i++) {
			list = transformation.applyFunction(list);
		}
		CurrentStudents.getInstance().setModifiedStudents(list);

		return true;
	}

	/**
	 * Sets the stage this class uses. Will be hidden on close, so don't pass the primary one ;)
	 *
	 * @param myStage The stage this class uses
	 */
	void setMyStage(Stage myStage) {
		this.myStage = myStage;
	}
}
