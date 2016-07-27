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
import me.ialistannen.paper_passing.model.TableStudent;
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

//		for(int y = 0; y < room.getDataList().size(); y++) {
//			room.getData()[y][1] = new DeskPlaceholder();
//		}
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

		primaryStage.setScene(new Scene(pane));

		primaryStage.show();

		teste();
	}

	private void teste() {

		Classroom room = null;
		if (Files.exists(Paths.get("bin/save2.save"))) {
			room = Classroom.read(Paths.get("bin/save2.save")).get(0);
		}

//		StudentsGridEntry[][] contents = new StudentsGridEntry[10][10];
//
//		for (int x = 0; x < 10; x++) {
//			switch (ThreadLocalRandom.current().nextInt(3)) {
//			case 0:
//				contents[0][x] = new DeskPlaceholder();
//				break;
//			case 1:
//				contents[0][x] = new BlackBoardPlaceholder();
//				break;
//			case 2:
//				contents[0][x] = new BlankPlaceholder();
//				break;
//			}
//		}
//
//		for (int x = 1; x < 10; x++) {
//			for (int y = 0; y < 10; y++) {
//				StudentsGridEntry entry;
//				if(ThreadLocalRandom.current().nextBoolean()) {
//					entry = new TableStudent(x + " : " + y);
//				}
//				else {
//					switch (ThreadLocalRandom.current().nextInt(3)) {
//					case 0:
//						entry = new DeskPlaceholder();
//						break;
//					case 1:
//						entry = new BlackBoardPlaceholder();
//						break;
//					case 2:
//						entry = new BlankPlaceholder();
//						break;
//					default:
//						entry = new BlankPlaceholder();
//					}
//				}
//				contents[x][y] = entry;
//			}
//		}
//
//		if(room == null) {
//			room = new Classroom(contents);
//			Classroom.save(Paths.get("bin/save.save"), room);
//		}


		final Classroom finalRoom = room;

		getMainWindowController().getTableGrid().setData(finalRoom);

		new Thread(() -> {
			System.out.println(Thread.currentThread().getName());

			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}

			((TableStudent) getMainWindowController().getTableGrid().getEntry(0, 3).get()).setName("30 : 0");

			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			getMainWindowController().getTableGrid().setEntry(9, 10, new TableStudent("Your wish!!"));

			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			getMainWindowController().getTableGrid().setEntry(9, 11, new TableStudent("Your wish!!!"));

//			getMainWindowController().getTableGrid().setData(finalRoom);			
		}, "Switcher");
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
