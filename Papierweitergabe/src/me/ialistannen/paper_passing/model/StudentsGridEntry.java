package me.ialistannen.paper_passing.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

import java.io.IOException;
import java.io.Serializable;

/**
 * An entry for the Students grid
 */
public abstract class StudentsGridEntry implements Serializable {

	private static final long serialVersionUID = 2652242165315109009L;

	transient ObjectProperty<Node> nodeProperty;

	{
		nodeProperty = new SimpleObjectProperty<>();
	}

	/**
	 * @return The node property of the node to draw
	 */
	public abstract ObjectProperty<Node> nodeProperty();

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		nodeProperty = new SimpleObjectProperty<>();
	}
}
