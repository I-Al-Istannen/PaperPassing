package me.ialistannen.paper_passing.model;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

/**
 * A student class, to be displayed in a tableview
 */
public class TableStudent extends StudentsGridEntry {

	private static final long serialVersionUID = -1048803162594841325L;

	private StringProperty name;

	/**
	 * @param name
	 *            The name of the student
	 */
	public TableStudent(StringProperty name) {
		this.name = name;
		
		updateNode();
	}

	/**
	 * @param name
	 *            The name of the student
	 */
	public TableStudent(String name) {
		this(new SimpleStringProperty(name));
	}

	/**
	 * @return The name of the student
	 */
	public String getName() {
		return name.get();
	}

	/**
	 * @param name
	 *            The new name
	 */
	public void setName(String name) {
		setName(new SimpleStringProperty(name));
	}

	/**
	 * @param name
	 *            The new name
	 */
	public void setName(StringProperty name) {
		if (name.get().trim().isEmpty()) {
			throw new IllegalArgumentException("Name can not be empty!");
		}
		this.name = name;
		updateNode();
	}

	/**
	 * @return The name property
	 */
	public StringProperty nameProperty() {
		return name;
	}

	/**
	 * Updates the node
	 */
	private void updateNode() {
		Label label = new Label(getName());
		label.setFont(Font.font("Monospaced", FontPosture.REGULAR, 15));
		label.setBackground(new Background(new BackgroundFill(Color.AQUA, new CornerRadii(6), new Insets(0))));

		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		nodeProperty.set(label);
	}

	/**
	 * @return The node property
	 */
	@Override
	public ObjectProperty<Node> nodeProperty() {
		return nodeProperty;
	}

	@Override
	public String toString() {
		return "TableStudent [name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.get().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TableStudent other = (TableStudent) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.get().equals(other.name.get()))
			return false;
		return true;
	}

	/**
	 * Serialize this instance.
	 * 
	 * @param out
	 *            Target to which this instance is written.
	 * @throws IOException
	 *             Thrown if exception occurs during serialization.
	 */
	private void writeObject(final ObjectOutputStream out) throws IOException {
		out.writeUTF(getName());
	}

	/**
	 * Deserialize this instance from input stream.
	 * 
	 * @param in
	 *            Input Stream from which this instance is to be deserialized.
	 * @throws IOException
	 *             Thrown if error occurs in deserialization.
	 * @throws ClassNotFoundException
	 *             Thrown if expected class is not found.
	 */
	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
		setName(in.readUTF());
	}

	/*
	 * Used for the Serializer. So NOT unused, just not actively invoked! 
	 */
	@SuppressWarnings("unused")
	private void readObjectNoData() throws ObjectStreamException {
		throw new InvalidObjectException("Stream data required");
	}
}
