package me.ialistannen.paper_passing.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.ialistannen.paper_passing.PaperPassing;
import me.ialistannen.paper_passing.logic.CurrentStudents;
import me.ialistannen.paper_passing.logic.PassingCalculator;
import me.ialistannen.paper_passing.model.Classroom;
import me.ialistannen.paper_passing.util.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * The controller for the main window
 */
public class MainWindowController {

	@FXML
	private BorderPane borderPane;

	private TableGridController tableGrid;

	private Path lastSaveLocation;

	@FXML
	private void initialize() {
		loadTableGrid();
	}

	/**
	 * Loads the table grid
	 */
	private void loadTableGrid() {
		try {
			FXMLLoader loader = new FXMLLoader(TableGridController.class.getResource("TableGrid.fxml"));
			Parent pane = loader.load();
			tableGrid = loader.getController();

			borderPane.setCenter(pane);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void onExit(ActionEvent event) {
		System.exit(0);
	}

	/**
	 * Asks the user if and where to save the room
	 *
	 * @param room The room to save
	 */
	private void startSaving(Classroom room) {
		FileChooser fileChooser = Util.getDefaultFileChooser();
		File result = fileChooser.showSaveDialog(PaperPassing.getInstance().getPrimaryStage());

		if (result == null) {
			return;
		}

		save(room, result.toPath());
	}

	/**
	 * Saves the room
	 *
	 * @param room   The room to save
	 * @param target The path to save to
	 */
	private void save(Classroom room, Path target) {
		Classroom.save(target, room);
		if (Files.exists(target)) {
			Alert successful = new Alert(Alert.AlertType.INFORMATION);
			successful.setTitle("Saved");
			successful.setHeaderText("Successfully saved!");
			successful.show();

			lastSaveLocation = target;
		} else {
			Alert unSuccessful = new Alert(Alert.AlertType.INFORMATION);
			unSuccessful.setTitle("Not Saved");
			unSuccessful.setHeaderText("Couldn't save the classroom!");
			unSuccessful.show();
		}
	}

	@FXML
	void onAbout(ActionEvent event) {
		System.out.println("MainWindowController.onAbout()");
	}

	@FXML
	void onImport(ActionEvent event) {
		if (!Util.showConfirmationDialog("Importing discards any unsaved changes. Do you want to continue?", "Importing")) {
			return;
		}
		FileChooser chooser = Util.getDefaultFileChooser();
		File result = chooser.showOpenDialog(PaperPassing.getInstance().getPrimaryStage());
		if (result == null) {
			return;
		}
		List<Classroom> roomList = Classroom.read(result.toPath());
		if (roomList.isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("Couldn't read any rooms from this save :/");
			alert.setTitle("Couldn't read. May need a german teacher :)");
			alert.show();
			return;
		}
		if (roomList.size() > 1) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setHeaderText("Loaded multiple saves. I will use the first one!");
			alert.setTitle("Multiple saves found");
			alert.showAndWait();
		}
		Classroom room = roomList.get(0);
		lastSaveLocation = result.toPath();
		getTableGrid().setData(room);
	}

	@FXML
	void onSave(ActionEvent event) {
		Classroom room = getTableGrid().getClassRoom();
		if (lastSaveLocation != null) {
			save(room, lastSaveLocation);
		} else {
			startSaving(getTableGrid().getClassRoom());
		}
	}

	@FXML
	void onSaveAs(ActionEvent event) {
		startSaving(getTableGrid().getClassRoom());
	}

	@FXML
	void onOpenTransformWindow(ActionEvent event) {
		// set the current students and calculate the passing right
		PassingCalculator passingCalculator = new PassingCalculator(getTableGrid().getClassRoom());
		CurrentStudents.getInstance().setOriginalStudents(passingCalculator.getResultingList());
		try {
			FXMLLoader loader = new FXMLLoader(ApplyTransformationsWindowController.class.getResource("ApplyTransformationsWindow.fxml"));
			BorderPane pane = loader.load();
			ApplyTransformationsWindowController controller = loader.getController();

			Stage stage = new Stage();
			stage.initOwner(PaperPassing.getInstance().getPrimaryStage());
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setScene(new Scene(pane));

			controller.setMyStage(stage);

			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			Util.showNonBlockingErrorAlert("An error occurred while opening the transformation window", "An error occurred", Util.getExceptionStackTrace(e));
		}
	}

	@FXML
	void onOpenTransformOutput(ActionEvent event) {
		openTransformationOutput();
	}

	@FXML
	void onPack(ActionEvent event) {
		getTableGrid().pack();
	}

	@FXML
	void onResize(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(ResizeDialogController.class.getResource("ResizeDialog.fxml"));
			BorderPane pane = loader.load();
			ResizeDialogController controller = loader.getController();

			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(PaperPassing.getInstance().getPrimaryStage());
			stage.setScene(new Scene(pane));

			controller.setMyStage(stage);

			stage.showAndWait();

			if (!controller.wasCancelled()) {
				getTableGrid().resize(controller.getColumns(), controller.getRows());
			}
		} catch (IOException e) {
			e.printStackTrace();
			Util.showNonBlockingErrorAlert("Can't load the resize dialog", "Error", Util.getExceptionStackTrace(e));
		}
	}

	/**
	 * Opens the transformation output window. Must be called from the Fx Thread
	 */
	void openTransformationOutput() {
		if (CurrentStudents.getInstance().getModified() == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setHeaderText("No transformation done. Please do this first");
			alert.setTitle("Not transformed");
			alert.initOwner(PaperPassing.getInstance().getPrimaryStage());
			alert.show();
			return;
		}

		try {
			FXMLLoader loader = new FXMLLoader(TransformationOutputWindowController.class.getResource("TransformationOutputWindow.fxml"));
			BorderPane pane = loader.load();
			Stage stage = new Stage();
			stage.setScene(new Scene(pane));
			stage.initOwner(PaperPassing.getInstance().getPrimaryStage());
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			Util.showNonBlockingErrorAlert("Error opening the transformation output window", "Error", Util.getExceptionStackTrace(e));
		}
	}

	/**
	 * @return The table grid controller
	 */
	public TableGridController getTableGrid() {
		return tableGrid;
	}
}
