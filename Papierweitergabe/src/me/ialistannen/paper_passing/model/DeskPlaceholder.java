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
 * Where a desk is present, but no student
 */
public class DeskPlaceholder extends StudentsGridEntry {

	private static final long serialVersionUID = 1847499652035538593L;

	/**
	 * The desk placeholder
	 */
	public DeskPlaceholder() {
		init();
	}

	@Override
	public ObjectProperty<Node> nodeProperty() {
		return nodeProperty;
	}
	
	private void init() {
		Label label = new Label("");
		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		label.setBackground(new Background(new BackgroundFill(Color.BURLYWOOD, new CornerRadii(6), new Insets(0))));
		nodeProperty.set(label);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		init();
	}
}
