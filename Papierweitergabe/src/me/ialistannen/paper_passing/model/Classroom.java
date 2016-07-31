package me.ialistannen.paper_passing.model;

import me.ialistannen.paper_passing.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A simple classroom
 */
public class Classroom implements Serializable {

	/**
	 * The serial version id
	 */
	private static final long serialVersionUID = -8387580899780050534L;
	private StudentsGridEntry[][] data;

	/**
	 * Without any data
	 */
	public Classroom() {
	}

	/**
	 * @param data The data to use
	 */
	public Classroom(StudentsGridEntry[][] data) {
		setData(data);
	}

	/**
	 * @return The data about the classroom
	 */
	public StudentsGridEntry[][] getData() {
		return data;
	}

	/**
	 * @return The data as a list
	 */
	public List<StudentsGridEntry[]> getDataList() {
		return Arrays.asList(data);
	}

	/**
	 * @param newData The new data
	 */
	public void setData(StudentsGridEntry[][] newData) {
		data = newData;
	}

	/**
	 * @param newData The new data
	 */
	public void setDataList(List<StudentsGridEntry[]> newData) {
		data = new StudentsGridEntry[newData.size()][];
		for (int i = 0; i < newData.size(); i++) {
			StudentsGridEntry[] name = newData.get(i);
			data[i] = name;
		}
	}

	/**
	 * @param path       The path to save to
	 * @param classrooms The classrooms to save
	 */
	public static void save(Path path, Classroom... classrooms) {
		if (Files.isDirectory(path)) {
			throw new IllegalArgumentException("The path '" + path.toAbsolutePath() + "' is a directory!");
		}

		try {
			if (Files.notExists(path)) {
				Files.createDirectories(path.getParent());
				Files.createFile(path);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}


		try (ObjectOutputStream objectOut = new ObjectOutputStream(Files.newOutputStream(path, StandardOpenOption.WRITE))) {

			objectOut.writeInt(classrooms.length);
			for (Classroom classroom : classrooms) {
				objectOut.writeObject(classroom);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param path The path to save to
	 *
	 * @return All the read rooms
	 */
	public static List<Classroom> read(Path path) {
		if (Files.notExists(path) || Files.isDirectory(path)) {
			throw new IllegalArgumentException("The file does not exist or is a directory: '" + path.toAbsolutePath() + "'.");
		}

		try (InputStream inputStream = Files.newInputStream(path, StandardOpenOption.READ)) {

			return read(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
			Util.showNonBlockingErrorAlert("Error reading a file", "Error", Util.getExceptionStackTrace(e));
		}

		return Collections.emptyList();
	}

	/**
	 * Reads a (or more) classroom(s) from an input stream
	 *
	 * @param inputStream The inputStream to read from
	 *
	 * @return All the read classrooms
	 */
	public static List<Classroom> read(InputStream inputStream) {
		ArrayList<Classroom> rooms = new ArrayList<>();

		try (ObjectInputStream objectIn = new ObjectInputStream(inputStream)) {

			int amount = objectIn.readInt();
			rooms.ensureCapacity(amount);

			for (int i = 0; i < amount; i++) {
				Object read = objectIn.readObject();
				if (read instanceof Classroom) {
					rooms.add((Classroom) read);
				} else {
					System.out.println("Unknown object found: '" + read
							+ "' While reading from an stream of type '"
							+ inputStream.getClass().getSimpleName() + "'.");
				}
			}

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return rooms;
	}
}
