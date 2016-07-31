package me.ialistannen.paper_passing.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import me.ialistannen.paper_passing.PaperPassing;

/**
 * The about window
 */
public class AboutWindowController {

	private Stage myStage;

	@FXML
	private BorderPane borderPane;

	@FXML
	private TextFlow textFlow;

	@FXML
	private TextFlow pictureDescriptionFlow;

	@FXML
	private Label headerLabel;

	@SuppressWarnings("SpellCheckingInspection")
	@FXML
	private void initialize() {
		headerLabel.setText("About this program");

		addText("This program was developed by I Al Istannen for a german teacher essentially for fun. " +
				"It will probably never be used, but who cares :)");
		addText("\n\nThe code for it can be found on my");

		addHyperlink("github", "https://github.com/I-Al-Istannen/PaperPassing");
		addText(".");

		addText("\n\nIf you have any suggestions, feel free to open an issue (or pull request)");
		addHyperlink("here", "https://github.com/I-Al-Istannen/PaperPassing/issues");
		addText("or send me an e-mail, if you know how :P");

		{
			Text firstPart = addAndGetText("\n\n\n\nWish you the best at your new school!", textFlow);
			Text secondPart = addAndGetText("\nHope you stay in touch with the other two :)", textFlow);
			firstPart.setUnderline(true);
			secondPart.setUnderline(true);
		}

		addText("The picture was made by", pictureDescriptionFlow);
		addHyperlink("Raakile", "http://raakile.deviantart.com/", pictureDescriptionFlow);
		addText("and released under a", pictureDescriptionFlow);
		addHyperlink("Creative Commons License", "https://creativecommons.org/licenses/by-nc-nd/3.0/", pictureDescriptionFlow);
		addText("\nYou can find it", pictureDescriptionFlow);
		addHyperlink("here", "http://raakile.deviantart.com/art/Black-Mage-356147620", pictureDescriptionFlow);
		addText(".", pictureDescriptionFlow);
		addText("\nI hope this fulfilles the mentioned license, but it should.", pictureDescriptionFlow);
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

	private void addHyperlink(String text, String url) {
		addHyperlink(text, url, textFlow);
	}

	private void addHyperlink(String text, String url, TextFlow flow) {
		Hyperlink hyperlink = new Hyperlink(text);
		hyperlink.setOnAction(event -> PaperPassing.getInstance().getHostServices().showDocument(url));
		hyperlink.getStyleClass().add("textFlowCustom");
		flow.getChildren().add(hyperlink);
	}

	@FXML
	void onClose(ActionEvent event) {
		myStage.close();
	}

	/**
	 * @param myStage
	 */
	public void setMyStage(Stage myStage) {
		this.myStage = myStage;
	}
}
