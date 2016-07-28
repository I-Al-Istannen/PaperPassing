package me.ialistannen.paper_passing.view;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import me.ialistannen.paper_passing.PaperPassing;
import me.ialistannen.paper_passing.model.BlackBoardPlaceholder;
import me.ialistannen.paper_passing.model.BlankPlaceholder;
import me.ialistannen.paper_passing.model.Classroom;
import me.ialistannen.paper_passing.model.DeskPlaceholder;
import me.ialistannen.paper_passing.model.StudentsGridEntry;
import me.ialistannen.paper_passing.model.TableStudent;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * The controller for the table grid
 */
public class TableGridController {

	@FXML
	private TableView<StudentsGridEntry[]> tableView;

	@FXML
	private AnchorPane anchorPane;

	private ObservableList<StudentsGridEntry[]> data = FXCollections.observableArrayList();

	@FXML
	@SuppressWarnings("unused")
	private void initialize() {
		createColumns(0);

		tableView.widthProperty().addListener((observable, oldWidth, newWidth) -> resizeComponents(
				newWidth.doubleValue() - tableView.getInsets().getLeft() - tableView.getInsets().getRight()
						- tableView.getPadding().getLeft() - tableView.getPadding().getRight(),
				tableView.getHeight() - tableView.getInsets().getTop() - tableView.getInsets().getBottom()
						- tableView.getPadding().getTop() - tableView.getPadding().getBottom()));
		tableView.heightProperty().addListener((observable, oldHeight, newHeight) -> resizeComponents(
				tableView.getWidth() - tableView.getInsets().getLeft() - tableView.getInsets().getRight()
						- tableView.getPadding().getLeft() - tableView.getPadding().getRight(),
				newHeight.doubleValue() - tableView.getInsets().getTop() - tableView.getInsets().getBottom()
						- tableView.getPadding().getTop() - tableView.getPadding().getBottom()));

		data.addListener((ListChangeListener<StudentsGridEntry[]>) change -> resizeComponents(
				tableView.getWidth() - tableView.getInsets().getLeft() - tableView.getInsets().getRight()
						- tableView.getPadding().getLeft() - tableView.getPadding().getRight(),
				tableView.getHeight() - tableView.getInsets().getTop() - tableView.getInsets().getBottom()
						- tableView.getPadding().getTop() - tableView.getPadding().getBottom()));
	}

	/**
	 * Resizes the components
	 *
	 * @param width  The width
	 * @param height The height
	 */
	private void resizeComponents(double width, double height) {
		if (data.isEmpty()) {
			return;
		}
		double rows = data.size();
		double columns = data.get(0).length;

		// Magic numbers! Have fun!
		// 6 pixels total border
		width -= rows * 6;
		// 8 pixels total border
		height -= rows * 8;

		double squareWidth = width / (columns);
		double squareHeight = height / (rows);

		for (TableColumn<StudentsGridEntry[], ?> tableColumn : tableView.getColumns()) {
			tableColumn.setPrefWidth(squareWidth);
			for (int y = 0; y < data.size(); y++) {
				if (tableColumn.getCellData(y) == null) {
					continue;
				}
				Node data = (Node) tableColumn.getCellData(y);
				if (data instanceof Region) {
					((Region) data).setPrefHeight(squareHeight);
					tableView.refresh();
				}
			}
		}
	}

	/**
	 * Creates the desired amount of columns
	 *
	 * @param amount The amount of columns
	 */
	private void createColumns(int amount) {
		tableView.getColumns().clear();
		if (amount < 0) {
			throw new IllegalArgumentException("Width must be positive!");
		}

		for (int i = 0; i < amount; i++) {
			TableColumn<StudentsGridEntry[], Node> column = new TableColumn<>(Integer.toString(i));
			final int colNumber = i;
			column.setCellValueFactory(param -> param.getValue()[colNumber] != null ? param.getValue()[colNumber].nodeProperty() : new SimpleObjectProperty<>());
			column.setSortable(false);

			tableView.getColumns().add(column);
		}

		tableView.getSelectionModel().setCellSelectionEnabled(true);

		tableView.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				tableView.getSelectionModel().clearSelection();
			} else if (event.getCode() == KeyCode.B && event.isControlDown()) {
				replaceSelectedEntry(new BlackBoardPlaceholder());
			} else if (event.getCode() == KeyCode.R && event.isControlDown()) {
				replaceSelectedEntry(new BlankPlaceholder());
			} else if (event.getCode() == KeyCode.D && event.isControlDown()) {
				replaceSelectedEntry(new DeskPlaceholder());
			} else if (event.getCode() == KeyCode.S && event.isControlDown()) {
				getTableStudentFromUser().ifPresent(this::replaceSelectedEntry);
			} else if (event.getCode() == KeyCode.S && event.isAltDown()) {
				Classroom room = new Classroom();
				room.setDataList(data);
				Classroom.save(Paths.get("bin/save2.save"), room);
			}
		});

		tableView.setOnMouseClicked(event -> {
			if (event.getButton() != MouseButton.SECONDARY) {
				return;
			}
			Popup popup = new Popup();
			SplitMenuButton menu = new SplitMenuButton();
			menu.setText("Choose an item.");

			Region studentGraphic = (Region) new TableStudent("Max Mustermann").nodeProperty().get();

			Region deskGraphic = (Region) new DeskPlaceholder().nodeProperty().get();
			deskGraphic.minHeightProperty().bind(studentGraphic.heightProperty());
			deskGraphic.minWidthProperty().bind(studentGraphic.widthProperty());

			Region blackBoardGraphic = (Region) new BlackBoardPlaceholder().nodeProperty().get();
			blackBoardGraphic.minHeightProperty().bind(studentGraphic.heightProperty());
			blackBoardGraphic.minWidthProperty().bind(studentGraphic.widthProperty());

			Label deleteGraphic = new Label("");
			deleteGraphic.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			deleteGraphic.setBackground(new Background(new BackgroundFill(Color.INDIANRED, new CornerRadii(6), new Insets(0))));
			deleteGraphic.minHeightProperty().bind(studentGraphic.heightProperty());
			deleteGraphic.minWidthProperty().bind(studentGraphic.widthProperty());

			MenuItem studentItem = new MenuItem("Student", studentGraphic);
			studentItem.setOnAction(action -> getTableStudentFromUser().ifPresent(this::replaceSelectedEntry));

			MenuItem deskItem = new MenuItem("Desk", deskGraphic);
			deskItem.setOnAction(action -> replaceSelectedEntry(new DeskPlaceholder()));

			MenuItem blackBoardItem = new MenuItem("Blackboard", blackBoardGraphic);
			blackBoardItem.setOnAction(action -> replaceSelectedEntry(new BlackBoardPlaceholder()));

			MenuItem deleteItem = new MenuItem("Delete", deleteGraphic);
			deleteItem.setOnAction(action -> replaceSelectedEntry(new BlankPlaceholder()));

			menu.getItems().add(studentItem);
			menu.getItems().add(deskItem);
			menu.getItems().add(blackBoardItem);
			menu.getItems().add(deleteItem);

			popup.getContent().add(menu);
			popup.setAnchorX(event.getScreenX());
			popup.setAnchorY(event.getScreenY());
			popup.setAutoFix(true);
			popup.setAutoHide(true);
			popup.show(PaperPassing.getInstance().getPrimaryStage());
		});
	}

	/**
	 * Opens a dialog with the user, where he can input the name of the student
	 *
	 * @return The TableStudent or an empty optional if the user discarded it
	 */
	private Optional<TableStudent> getTableStudentFromUser() {
		TextInputDialog studentNameDialog = new TextInputDialog("Max Mustermann");
		studentNameDialog.setTitle("Create a new Student");
		studentNameDialog.setHeaderText("Please choose a name for the student.");
		Optional<String> nameOpt = studentNameDialog.showAndWait();
		if (!nameOpt.isPresent()) {
			return Optional.empty();
		}

		TableStudent student = new TableStudent(nameOpt.get());
		return Optional.of(student);
	}

	/**
	 * Replaces the selected entry with the passed one
	 *
	 * @param newEntry The new entry, to replace the currently selected one
	 */
	private void replaceSelectedEntry(StudentsGridEntry newEntry) {
		int column = tableView.getSelectionModel().getSelectedCells().get(0).getColumn();
		int row = tableView.getSelectionModel().getSelectedCells().get(0).getRow();
		data.get(row)[column] = newEntry;
		refresh();
		tableView.getSelectionModel().select(row, tableView.getColumns().get(column));
	}

	/**
	 * @param newData The new Data. No null values are permitted
	 */
	public void setData(Collection<StudentsGridEntry[]> newData) {
		if (!Platform.isFxApplicationThread()) {
			runOnFxThreadAndWait(() -> setData(newData));
			return;
		}
		for (StudentsGridEntry[] entry : newData) {
			if (entry == null) {
				throw new IllegalArgumentException("An entry in the array newData is null!");
			}
		}

		data.setAll(newData);
		tableView.setItems(data);
		int width = data.isEmpty() ? 0 : data.get(0).length;

		createColumns(width);

		refresh();
	}

	/**
	 * @param classroom The classroom to use
	 */
	public void setData(Classroom classroom) {
		setData(classroom.getDataList());
	}

	/**
	 * Clears the table
	 */
	public void clear() {
		setData(Collections.emptyList());
	}

	/**
	 * @param x The x coordinate of the entry
	 * @param y The y coordinate of the entry
	 *
	 * @return The entry if found
	 *
	 * @throws IllegalArgumentException if x or y is is negative or too big
	 */
	public Optional<StudentsGridEntry> getEntry(int x, int y) {
		if (y < 0 || y >= data.size()) {
			throw new IllegalArgumentException("y can't be negative or bigger than the table size (" + data.size() + "). X: " + x);
		}

		if (x < 0 || x >= data.get(y).length) {
			throw new IllegalArgumentException("x can't be negative or bigger than the amount of entries (" + data.get(x).length + ") Y: " + y);
		}

		return Optional.ofNullable(data.get(y)[x]);
	}

	/**
	 * @param x     The x coordinate of the entry
	 * @param y     The y coordinate of the entry
	 * @param entry The new entry
	 */
	public void setEntry(int x, int y, StudentsGridEntry entry) {
		if (!Platform.isFxApplicationThread()) {
			runOnFxThreadAndWait(() -> setEntry(x, y, entry));
			return;
		}
		// must have SOME data (the column count)
		if (data.isEmpty()) {
			return;
		}

		if (x < 0 || x >= data.get(0).length) {
			throw new IllegalArgumentException("x can't be negative or bigger than the column count (" + data.get(0).length + ") X='" + x + "'");
		}

		if (y < 0) {
			throw new IllegalArgumentException("y can't be negative. Y='" + y + "'");
		}

		if (data.size() <= y) {
			data.add(y, new StudentsGridEntry[data.get(0).length]);
			for (int i = 0; i < data.get(0).length; i++) {
				data.get(y)[i] = new BlankPlaceholder();
			}
		}

		data.get(y)[x] = entry;

		refresh();
	}

	/**
	 * Refreshes the current grid, to make changes in data appear correctly.
	 */
	private void refresh() {
		if (!Platform.isFxApplicationThread()) {
			runOnFxThreadAndWait(() -> refresh());
			return;
		}

		// update the data behind. Dunno why THAT is needed...
		StudentsGridEntry[] obj = data.remove(0);
		data.add(0, obj);

		// the same as above. Basically. Why is this needed?
		tableView.getColumns().get(0).setVisible(false);
		tableView.getColumns().get(0).setVisible(true);
	}

	/**
	 * Times out at 20 seconds. Uses a semaphore.
	 *
	 * @param runnable The runnable to run
	 */
	private void runOnFxThreadAndWait(Runnable runnable) {
		if (Platform.isFxApplicationThread()) {
			runnable.run();
			return;
		}
		try {
			Semaphore lock = new Semaphore(1);
			lock.acquire();
			Platform.runLater(() -> {
				runnable.run();
				lock.release();
			});
			lock.tryAcquire(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
