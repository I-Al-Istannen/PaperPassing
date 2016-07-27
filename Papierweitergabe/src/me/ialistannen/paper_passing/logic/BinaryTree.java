package me.ialistannen.paper_passing.logic;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @param <K> The key
 * @param <V> The value
 */
public class BinaryTree<K extends Comparable<K>, V> {

	private K key;
	private V value;
	private BinaryTree<K, V> left, right;
	
	/**
	 * Default constructor
	 */
	public BinaryTree() {}
	
	/**
	 * @param key The key
	 * @param value The value
	 */
	public BinaryTree(K key, V value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * @return The right child
	 */
	public BinaryTree<K, V> getRight() {
		return right;
	}
	
	/**
	 * @param right The right child
	 */
	protected void setRight(BinaryTree<K, V> right) {
		this.right = right;
	}
	
	/**
	 * @return The left child
	 */
	public BinaryTree<K, V> getLeft() {
		return left;
	}
	
	/**
	 * @param left The keft child
	 */
	protected void setLeft(BinaryTree<K, V> left) {
		this.left = left;
	}
	
	/**
	 * @return The key
	 */
	public K getKey() {
		return key;
	}
	
	/**
	 * @return The value
	 */
	public V getValue() {
		return value;
	}
	
	/**
	 * @param key The key to search
	 * @return The binarytree with this value or an empty Optional
	 */
	public Optional<BinaryTree<K, V>> find(K key) {
		if(this.key.compareTo(key) == 0) {
			return Optional.of(this);
		}
		
		Optional<BinaryTree<K, V>> toReturn = Optional.empty();
		if(left != null) {
			toReturn = left.find(key);
		}

		if(toReturn.isPresent()) {
			return toReturn;
		}
		
		if(right != null) {
			return right.find(key);
		}
		
		return Optional.empty();
	}
	
	/**
	 * Replaces the value if the key is already in the map
	 * 
	 * @param key The key to add
	 * @param value The value to add
	 */
	public void add(K key, V value) {
		if(this.key == null && this.value == null) {
			this.key = key;
			this.value = value;
			return;
		}
		if(key.compareTo(this.key) < 0) {
			if(left == null) {
				left = new BinaryTree<>(key, value);
			}
			else {
				left.add(key, value);
			}
		}
		else if(key.compareTo(this.key) > 0) {
			if(right == null) {
				right = new BinaryTree<>(key, value);
			}
			else {
				right.add(key, value);
			}
		}
		else {
			this.value = value;
		}
	}
	
	/**
	 * Recursive over the whole tree
	 * 
	 * @return All the children
	 */
	public List<BinaryTree<K, V>> getChildren() {
		List<BinaryTree<K, V>> list = new LinkedList<>();
		
		if(getLeft() != null) {
			list.add(getLeft());
			list.addAll(getLeft().getChildren());
		}
		if(getRight() != null) {
			list.add(getRight());
			list.addAll(getRight().getChildren());
		}
		
		return list;
	}
	
	/**
	 * Recursive over the whole tree
	 * 
	 * @return All the children. Uses "in order" traversal 
	 */
	public List<BinaryTree<K, V>> getInOrderChildren() {
		List<BinaryTree<K, V>> list = new LinkedList<>();
		
		if(getLeft() != null) {
			list.addAll(getLeft().getInOrderChildren());
		}
		
		list.add(this);
		
		if(getRight() != null) {
			list.addAll(getRight().getInOrderChildren());
		}
		
		return list;
	}
	
	/**
	 * @return A preorder string
	 */
	public String toStringPostOrder() {
		StringBuilder string = new StringBuilder();
		if(left != null) {
			string.append(left.toStringPostOrder());
		}
		if(right != null) {
			string.append(right.toStringPostOrder());
		}
		
		string.append(this.toString());
		
		return string.toString();
	}
	
	/**
	 * @return The String in level order
	 */
	public String toStringLevelOrder() {
		StringBuilder builder = new StringBuilder();
		Deque<BinaryTree<K, V>> queue = new ArrayDeque<>();
	
		queue.add(this);
		
		BinaryTree<K, V> currentNode;
		while(((currentNode = queue.pollFirst()) != null)) {
			builder.append(", ");
			builder.append(currentNode);
			
			if(currentNode.getLeft() != null) {
				queue.add(currentNode.getLeft());
			}
			if(currentNode.getRight() != null) {
				queue.add(currentNode.getRight());
			}
		}

		return builder.toString().replaceFirst(", ", "");
	}
	
	/**
	 * @return The String in pre order
	 */
	public String toStringPreOrder() {
		StringBuilder string = new StringBuilder();
		
		string.append(this.toString());
		
		if(left != null) {
			string.append(left.toStringPostOrder());
		}
		if(right != null) {
			string.append(right.toStringPostOrder());
		}
				
		return string.toString();
	}
	
	/**
	 * @return The String in order
	 */
	public String toStringInOrder() {
		StringBuilder builder = new StringBuilder();
		
		if(getLeft() != null) {
			builder.append(getLeft().toStringInOrder());
		}
		builder.append(this);
		if(getRight() != null) {
			builder.append(getRight().toStringInOrder());
		}
		
		return builder.toString();
	}
	
	@Override
	public String toString() {
		return "[T='" + getKey() + "',V='" + getValue() + "']";
	}
}
