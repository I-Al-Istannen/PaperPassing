package me.ialistannen.paper_passing;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import me.ialistannen.paper_passing.model.Classroom;
import me.ialistannen.paper_passing.view.MainWindowController;

/**
 * The main class
 */
public class PaperPassing extends Application {

	private static PaperPassing instance;

	private Stage primaryStage;
	private MainWindowController mainWindowController;

	{
		instance = this;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;

		Classroom room = Classroom.read(PaperPassing.class.getResourceAsStream("/resources/Class10B.PP_SAVE")).get(0);

		FXMLLoader loader = new FXMLLoader(MainWindowController.class.getResource("MainWindow.fxml"));
		BorderPane pane = loader.load();
		mainWindowController = loader.getController();

		mainWindowController.getTableGrid().setData(room);

		primaryStage.setScene(new Scene(pane));

		primaryStage.show();

	}

	/**
	 * @return The primary stage
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * @return The {@link MainWindowController}
	 */
	public MainWindowController getMainWindowController() {
		return mainWindowController;
	}

	/**
	 * @return THe instance of the application
	 */
	public static PaperPassing getInstance() {
		return instance;
	}

	/**
	 * @param args The args passed to the program
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
