package me.ialistannen.paper_passing.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import me.ialistannen.paper_passing.PaperPassing;
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

	@FXML
	void onExport(ActionEvent event) {
		Classroom room = PaperPassing.getInstance().getMainWindowController().getTableGrid().getClassRoom();
		startSaving(room);
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
	 *
	 * @return True if the room was saved
	 */
	private boolean save(Classroom room, Path target) {
		Classroom.save(target, room);
		if (Files.exists(target)) {
			Alert successful = new Alert(Alert.AlertType.INFORMATION);
			successful.setTitle("Saved");
			successful.setHeaderText("Successfully saved!");
			successful.show();

			lastSaveLocation = target;
			return true;
		} else {
			Alert unSuccessful = new Alert(Alert.AlertType.INFORMATION);
			unSuccessful.setTitle("Not Saved");
			unSuccessful.setHeaderText("Couldn't save the classroom!");
			unSuccessful.show();

			return false;
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


	/**
	 * @return The table grid controller
	 */
	public TableGridController getTableGrid() {
		return tableGrid;
	}
}
