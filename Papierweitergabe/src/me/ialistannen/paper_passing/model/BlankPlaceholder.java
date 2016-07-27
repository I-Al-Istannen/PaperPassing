package me.ialistannen.paper_passing.model;

import java.io.IOException;
import java.io.ObjectInputStream;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 * Just blank white
 */
public class BlankPlaceholder extends StudentsGridEntry {

	private static final long serialVersionUID = -5613261194351463760L;
	
	/**
	 * The blackboard placeholder
	 */
	public BlankPlaceholder() {
		init();
	}
	
	private void init() {
		Label label = new Label("");
		
		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
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
