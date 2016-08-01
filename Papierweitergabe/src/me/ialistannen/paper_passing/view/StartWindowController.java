package me.ialistannen.paper_passing.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

/**
 * The start window, to display at the application startup
 */
@SuppressWarnings("WeakerAccess")
public class StartWindowController {

	private Stage myStage;

	@SuppressWarnings("CanBeFinal")
	@FXML
	private ScrollPane scrollPane;

	@SuppressWarnings("CanBeFinal")
	@FXML
	private TextFlow textFlow;

	@FXML
	private void initialize() {
		showGrid();
	}

	private void addText(String text) {
		addText(text, textFlow);
	}

	private void addText(String text, TextFlow flow) {
		addAndGetText(text, flow);
	}

	private Text addAndGetText(String text, TextFlow flow) {
		Text text1 = new Text(text);
		text1.getStyleClass().add("textFlowCustom");
		flow.getChildren().add(text1);
		return text1;
	}

	@FXML
	void onClose(ActionEvent event) {
		myStage.hide();
	}

	@FXML
	void onAlgorithm(ActionEvent event) {
		showAlgorithm();
	}

	private void showAlgorithm() {
		clear();
		addText("This tries to describe the crude thing called ");
		addAndGetText('\u00BB' + " Algorithm " + '\u00AB', textFlow).getStyleClass().add("quote");
		addText(" in the rest of this text.");

		addText("\n\nWhen you click on \"Open transform window\" the following steps happen.");

		addAndGetText("\n\nCalculating the first round", textFlow).getStyleClass().add("enumeration-point");

		addText("\n1. All Horizontal or Vertical unbroken lines will be gathered");

		addText("\n2. Every student will get the next person in this line as his target");
		addText("\n\t2.1 If it is the last, he will get the first.");
		addText("\n\t2.2 During that, a \"paper count\" will be incremented, when the students gets");
		addText("\n\tand decremented, when he gives one paper away.");

		addText("\n3. Do the same with the singles, treating them as one line.");
		addText("\n4. Add the singles to the list.");

		addText("\n5. The students who have too many or too few papers will be saved in a list, ");
		addText("\nalong with the not yet processes students (not in a line)");

		addText("\n6. The list will be looped through. For every student the nearest matching student");
		addText("\nwill be found. Matching means 0 papers to 2 and vice versa.");

		addText("\n7. Now you may have multiple \"circles\", students connected with each other.");
		addText("\nYou want one however. Because of this, the circles will be merged together.");
		addText("\n\t7.1 This is done by connecting the last and first of two circles, and then");
		addText("\n\tsetting the next circle to the merged result, so that the merged one will be");
		addText("\n\tmerged again.");
		addText("\n\tThis can still be improved, to merge with the visually nearest circle.");
		addText("\n\tAn option would be to gather all possible combinations and minimise the");
		addText("\n\tcumulated distance function between them. Was too lazy for this.");
		addText("\n\t");
		addAndGetText("Feel free to suggest an algorithm though!", textFlow).setUnderline(true);

		addText("\n8. Now you have a target for every student. This can now be transformed.");

		addAndGetText("\n\nTransformations", textFlow).getStyleClass().add("enumeration-point");
		addText("\nRight:");
		addText("\n\tDo once and repeat as long as the student passes it to himself.");
		addText("\n\t1. Loop through all students.");
		addText("\n\t2. Get the index of the target student in the list and add 1.");
		addText("\n\t3. If the index is bigger than the list size - 1, set it to 0.");
		addText("\n\t4. Set the target to the student at the gathered index.");
		addText("\n\t5. Remove the first element and append it at the end.");

		addText("\nLeft:");
		addText("\n\tDo once and repeat as long as the student passes it to himself.");
		addText("\n\t1. Loop through all students.");
		addText("\n\t2. Get the index of the target student in the list and subtract 1.");
		addText("\n\t3. If the index is smaller than 0, set it to the list size - 1.");
		addText("\n\t4. Set the target to the student at the gathered index.");
		addText("\n\t5. Remove the last element and append it at the beginning.");
	}

	@FXML
	void onGrid(ActionEvent event) {
		showGrid();
	}

	private void showGrid() {
		clear();
		addText("This tries to give you an overview over the students grid.");

		addText("\n\nThe grid can be resized with the options provided in the \"");
		addAndGetText("Edit", textFlow).getStyleClass().add("menu-name");
		addText("\" menu.");
		addAndGetText("\n1. Resize", textFlow).getStyleClass().add("enumeration-point");
		addText("\n\tThis let's you (quite) freely set the size of it.");
		addAndGetText("\n2. Pack", textFlow).getStyleClass().add("enumeration-point");
		addText("\n\tThis chooses the size, so that it barely fits.");

		addText("\n\n\nThere are a few options available if you right click anywhere (in the grid):");
		addAndGetText("\n1. Student", textFlow).getStyleClass().add("enumeration-point");
		addText("   ");
		addAndGetText("\t(Shift + S)", textFlow).getStyleClass().add("keycode");
		addText("\n\tLet's you add a student to the selected cell.");
		addAndGetText("\n2. Desk", textFlow).getStyleClass().add("enumeration-point");
		addText("      ");
		addAndGetText("\t(Shift + D)", textFlow).getStyleClass().add("keycode");
		addText("\n\tSets the selected cell to a desk.");
		addAndGetText("\n3. Blackboard", textFlow).getStyleClass().add("enumeration-point");
		addAndGetText("\t(Shift + B)", textFlow).getStyleClass().add("keycode");
		addText("\n\tSets the selected cell to a blackboard.");
		addAndGetText("\n4. Delete", textFlow).getStyleClass().add("enumeration-point");
		addText("    ");
		addAndGetText("\t(Shift + R)", textFlow).getStyleClass().add("keycode");
		addText("\n\tThis (surprise!) deletes the content of a cell and makes it empty.");

		addText("\n\n\nWell, I think that was about it.");
	}

	@FXML
	void onTransformation(ActionEvent event) {
		showTransformation();
	}

	private void showTransformation() {
		clear();
		addText("This tries to explain the transformations");

		addText("\n\nThe transformations are available in the menu \"");
		addAndGetText("Transform", textFlow).getStyleClass().add("menu-name");
		addText("\"");

		addAndGetText("\n\n1. Open transform window", textFlow).getStyleClass().add("enumeration-point");
		addText("\n\tThis opens the menu where you can add transformations.");
		addText("\n\tYou can select ");
		addAndGetText("Right", textFlow).getStyleClass().add("transformation-name");
		addText(" or ");
		addAndGetText("Left", textFlow).getStyleClass().add("transformation-name");
		addText(".");
		addText("\n\tThese just pass it on to the left or the right.");
		addText("\n\tNote, that");
		addAndGetText(" Right ", textFlow).getStyleClass().add("transformation-name");
		addText(", 0 is ONE to the right, due to the way the passing is calculated.");
		addAndGetText("\n2. Open transform output", textFlow).getStyleClass().add("enumeration-point");
		addText("\n\tThis just shows the output and lets you export or print it.");
		addText("\n\tIt also allows you to choose between two styles, feel free to");
		addText("\n\tsuggest more.");

		addText("\n\n\nAnd that was probably all. I can expand this though, if you have an idea.");
	}

	private void clear() {
		textFlow.getChildren().clear();
		// scroll to top
		scrollPane.setVvalue(0);
	}

	/**
	 * Sets the stage this window uses
	 *
	 * @param stage The new stage
	 */

	void setMyStage(Stage stage) {
		this.myStage = stage;
	}
}
