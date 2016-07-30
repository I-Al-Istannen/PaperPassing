package me.ialistannen.paper_passing;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import me.ialistannen.paper_passing.logic.PaperPassingStudent;
import me.ialistannen.paper_passing.logic.PassingCalculator;
import me.ialistannen.paper_passing.model.Classroom;
import me.ialistannen.paper_passing.util.Util;
import me.ialistannen.paper_passing.view.MainWindowController;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
		if (Files.exists(Paths.get("bin/save2.save"))) {
			room = Classroom.read(Paths.get("bin/save2.save")).get(0);
		}

		List<PaperPassingStudent> resultingList = new PassingCalculator(room).getResultingList();
		resultingList = Util.getOrderedList(resultingList.get(0));

//		OutputFormatter.output(OutputFormatter.formatList(resultingList));

/*		{
			List<PaperPassingStudent> student = new ArrayList<>();
			PaperPassingStudent ts = new PaperPassingStudent(new TableStudent("Simone"), new Point2D(1, 1));
			PaperPassingStudent ts2 = new PaperPassingStudent(new TableStudent("Linda"), new Point2D(1, 1));
			PaperPassingStudent ts3 = new PaperPassingStudent(new TableStudent("Leonora"), new Point2D(1, 1));
			PaperPassingStudent ts4 = new PaperPassingStudent(new TableStudent("Tester"), new Point2D(1, 1));

			ts.setTarget(ts2);
			ts2.setTarget(ts3);
			ts3.setTarget(ts4);
			ts4.setTarget(ts);
			student.add(ts);
			student.add(ts2);
			student.add(ts3);
			student.add(ts4);

*//*
			for(int i = 0; i < 3; i++) {
				student = OutputTransformation.RIGHT.applyFunction(student);
				System.out.println("=======" + i + "======");
				OutputFormatter.output(OutputFormatter.formatList(student));
				System.out.println("=======" + " " + "=======");
//				student = Util.getOrderedList(student.get(0));
			}

//			student = Util.getOrderedList(student.get(0));
			System.out.println(student.stream().map(paperPassingStudent -> paperPassingStudent.getBacking().getName()).collect(Collectors.toList()));
			OutputFormatter.output(OutputFormatter.formatList(student));
			if ("".isEmpty()) {
				return;
			}
*//*

			CurrentStudents.getInstance().setOriginalStudents(student);
			FXMLLoader loader = new FXMLLoader(ApplyTransformationsWindowController.class.getResource("ApplyTransformationsWindow.fxml"));
			BorderPane pane = loader.load();
			ApplyTransformationsWindowController controller = loader.getController();
			Stage stage = new Stage();
			Scene scene = new Scene(pane);
			stage.setScene(scene);
			controller.setMyStage(stage);
			stage.show();
//			primaryStage.setScene(scene);
//			primaryStage.show();
			if ("".isEmpty()) {
				return;
			}
		}*/

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
