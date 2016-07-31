package me.ialistannen.paper_passing.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

/**
 * Let's the user resize the grid
 */
public class ResizeDialogController {

	private Stage myStage;

	private int columns = -1, rows = -1;

	private boolean cancelled = false;

	@FXML
	private Slider columnSlider;

	@FXML
	private Slider rowSlider;

	@FXML
	void onClose(ActionEvent event) {
		cancelled = true;
		myStage.close();
	}

	@FXML
	void onOkay(ActionEvent event) {
		columns = (int) Math.round(columnSlider.getValue());
		rows = (int) Math.round(rowSlider.getValue());

		myStage.close();
	}

	/**
	 * The column amount or -1 for abortion
	 *
	 * @return The column amount
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * The row amount or -1 for abortion
	 *
	 * @return The row amount
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @return True if this dialog was cancelled
	 */
	public boolean wasCancelled() {
		return cancelled;
	}

	/**
	 * Sets the stage this dialog uses
	 *
	 * @param myStage The stage
	 */
	public void setMyStage(Stage myStage) {
		this.myStage = myStage;
		this.myStage.setOnCloseRequest(event -> {
			cancelled = true;
		});
	}
}
