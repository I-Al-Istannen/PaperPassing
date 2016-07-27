package me.ialistannen.paper_passing.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

/**
 * The controller for the main window
 */
public class MainWindowController {
	
    @FXML
    private BorderPane borderPane;
	
    private TableGridController tableGrid;
    
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
	
	/**
	 * @return The table grid controller
	 */
	public TableGridController getTableGrid() {
		return tableGrid;
	}
}
