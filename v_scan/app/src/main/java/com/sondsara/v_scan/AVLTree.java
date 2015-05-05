package com.sondsara.v_scan;

/**
 * Created by chris on 5/3/15.
 */
public class AVLTree<E extends Comparable<? super E>> {
	
	private AVLNode<E> rootAbove;

	public AVLTree(){
		rootAbove = new AVLNode<E>();
	}

	public void rotate(AVLNode<E> rotateBase, AVLNode<E> rootAbove){
		int balance = rotateBase.getBalance();

		if (Math.abs(balance) < 2){
			System.out.println("No rotate");
		}

		AVLNode<E> child = (balance < 0) ? rotateBase.getLeft() : rotateBase.getRight();

		if (child == null)
			return;

		int childBalance = child.getBalance();
		AVLNode<E> grandChild = null;

		if (balance < -1 && childBalance < 0){
			if (rootAbove != this.rootAbove && rootAbove.getRight() == rotateBase)
				rootAbove.setRightNode(child);
			else
				rootAbove.setLeftNode(child);

			grandChild = child.getRight();
			child.setRightNode(rotateBase);
			rotateBase.setLeftNode(grandChild);
			return;
		}else if (balance > 1 && childBalance > 0){
			if (rootAbove != this.rootAbove && rootAbove.getRight() == rotateBase)
				rootAbove.setRightNode(child);
			else
				rootAbove.setLeftNode(child);

			grandChild = child.getLeft();
			child.setLeftNode(rotateBase);
			rotateBase.setRightNode(grandChild);
			return;
		}else if (balance < -1 && childBalance > 0){
			grandChild = child.getRight();
			rotateBase.setLeftNode(grandChild);
			child.setRightNode(grandChild.getLeft());
			grandChild.setLeftNode(child);
			rotate(rotateBase, rootAbove);
			return;
		}else if (balance < -1 && childBalance > 0){
			grandChild = child.getLeft();
			rotateBase.setRightNode(grandChild);
			child.setLeftNode(grandChild.getRight());
			grandChild.setRightNode(child);
			rotate(rotateBase, rootAbove);
			return;
		}
	}

	public void insert(E element){
		insert(element, rootAbove.getLeft());
	}

	private void insert(E element, AVLNode<E> temp){
		if (this.rootAbove.getLeft() == null){
			this.rootAbove.setLeftNode(new AVLNode<E> (element));
			return;
		}

		int compare = element.compareTo(temp.getElement());

		if (compare <= 0){
			if (temp.getLeft() == null){
				temp.setLeft(element);
				return;
			}

			insert(element, temp.getLeft());
		}else{
			if (temp.getRight() == null){
				temp.setRight(element);
				return;
			}

			insert(element, temp.getRight());
		}

		if (temp == rootAbove.getLeft()){
			rotate(rootAbove.getLeft(), rootAbove);
		}
		if (temp.getLeft() != null){
			rotate(temp.getLeft(), temp);
		}
		if (temp.getRight() != null){
			rotate(temp.getRight(), temp);
		}
	}


	public boolean contains(E element){
		AVLNode<E> temp = rootAbove.getLeft();

		while(temp != null){
			if (temp.getElement().equals(element))
				return true;

			int balance = element.compareTo(temp.getElement());
			temp = (balance < 0) ? temp.getLeft() : temp.getRight();
		}
		return false;
	}

}
