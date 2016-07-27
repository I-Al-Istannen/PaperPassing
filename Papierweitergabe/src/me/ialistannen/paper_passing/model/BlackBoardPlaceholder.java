package me.ialistannen.paper_passing.model;

import java.io.IOException;
import java.io.ObjectInputStream;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

/**
 * The place of a blackboard
 */
public class BlackBoardPlaceholder extends StudentsGridEntry {
	
	private static final long serialVersionUID = -2945158753350309093L;
	
	/**
	 * The blackboard placeholder
	 */
	public BlackBoardPlaceholder() {
		init();
	}
	
	private void init() {
		Label label = new Label("");
		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		label.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGREY, new CornerRadii(6), new Insets(0))));
		nodeProperty.set(label);
	}
	
	@Override
	public ObjectProperty<Node> nodeProperty() {
		return nodeProperty;
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		init();
	}
}
