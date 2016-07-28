package me.ialistannen.paper_passing;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import me.ialistannen.paper_passing.logic.BinaryTree;
import me.ialistannen.paper_passing.logic.PaperPassingStudent;
import me.ialistannen.paper_passing.logic.PassingCalculator;
import me.ialistannen.paper_passing.model.Classroom;
import me.ialistannen.paper_passing.output.OutputFormatter;
import me.ialistannen.paper_passing.output.OutputTransformation;
import me.ialistannen.paper_passing.view.MainWindowController;

import java.nio.file.Files;
import java.nio.file.Paths;

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

		Classroom room = null;
		Files.list(Paths.get("bin").toAbsolutePath()).forEach(System.out::println);
		if (Files.exists(Paths.get("bin/save2.save"))) {
			room = Classroom.read(Paths.get("bin/save2.save")).get(0);
		}

		Classroom.save(Paths.get("bin/save2.save"), room);
		BinaryTree<String, PaperPassingStudent> tree = new PassingCalculator(room).getResultingTree();
		OutputFormatter.output(OutputFormatter.formatList(tree));
		System.out.println();
		System.out.println("+ =========== +");
		System.out.println();
		OutputFormatter.output(OutputFormatter.formatList(OutputTransformation.LEFT.applyFunction(tree)));

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
