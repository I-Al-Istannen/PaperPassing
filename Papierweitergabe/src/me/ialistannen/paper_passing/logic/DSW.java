package me.ialistannen.paper_passing.logic;

/**
 * An implementation of the "Day-Stout-Warren" algorithm to balance a tree.
 * <br>Heavily borrowed from WikiPedia, but I think I understood the way it works :P
 * 
 * @param <T>
 *            The key of the tree
 * @param <V>
 *            The value of the key
 */
public class DSW<T extends Comparable<T>, V> {

	private BinaryTree<T, V> root;

	/**
	 * @param root
	 *            The tree root node
	 */
	public DSW(BinaryTree<T, V> root) {
		this.root = root;
	}

	/**
	 * Balances the tree.
	 * 
	 * @return The new root. Use it.
	 */
	public BinaryTree<T, V> balance() {
		BinaryTree<T, V> virtualRoot = new BinaryTree<>(null, null);
		virtualRoot.setRight(root);

		treeToVine(virtualRoot);
		int treeSize = 1;
		{
			BinaryTree<T, V> tmp = virtualRoot;
			while ((tmp = tmp.getRight()) != null) {
				treeSize++;
			}
		}

		vineToTree(virtualRoot, treeSize);

		root = virtualRoot.getRight();

		return root;
	}

	/**
	 * Makes it effectively a linked list.
	 * 
	 * Puts the current node as the right child of the left one. Then moves the
	 * left child's right child between the currents right and the current. Then
	 * repeats this, with the left node as the current. Then, when there are no
	 * more left nodes, traverses further in the list.
	 * 
	 * @param root
	 *            The pseudo root
	 */
	private void treeToVine(BinaryTree<T, V> root) {
		BinaryTree<T, V> tail = root;
		BinaryTree<T, V> rest = tail.getRight();

		while (rest != null) {
			if (rest.getLeft() == null) {
				tail = rest;
				rest = rest.getRight();
			} else {
				BinaryTree<T, V> temp = rest.getLeft();
				rest.setLeft(temp.getRight());
				temp.setRight(rest);

				rest = temp;
				tail.setRight(temp);
			}
		}
	}

	private void vineToTree(BinaryTree<T, V> root, int size) {
		int leaves = size - getM(size);
		compress(root, leaves);
		size = size - leaves;
		while (size > 1) {
			compress(root, size / 2);
			size /= 2;
		}
	}

	private void compress(BinaryTree<T, V> root, int count) {
		BinaryTree<T, V> scanner = root;

		for (int i = 0; i < count; i++) {
			BinaryTree<T, V> child = scanner.getRight();
			scanner.setRight(child.getRight());
			scanner = scanner.getRight();
			child.setRight(scanner.getLeft());
			scanner.setLeft(child);
		}
	}

	/**
	 * @param size
	 *            The size of the tree
	 * @return M.
	 */
	private int getM(int size) {
		size++;
		return (int) Math.pow(2, Math.floor(Math.log(size) / Math.log(2))) - 1;
	}
}