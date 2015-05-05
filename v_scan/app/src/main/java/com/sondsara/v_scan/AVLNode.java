package com.sondsara.v_scan;

/**
 * Created by chris on 5/3/15.
 */
public class AVLNode<E extends Comparable<? super E>> {
    private AVLNode<E> left, right;
    private E element;

    public AVLNode(){
        this(null);
    }

    public AVLNode(E element){
        this.element = element;
        left = right = null;
    }

    public E getElement(){ return element; }
    public void setElement(E element) { this.element = element; }

    public AVLNode<E>getLeft(){return left;}
    public AVLNode<E>getRight(){return right;}

    public void setLeft(E element){
        if (left == null)
            this.left = new AVLNode<E>(element);
        else
            this.left.setElement(element);
    }

    public void setRight(E element){
        if (right == null)
            this.right = new AVLNode<E>(element);
        else
            this.right.setElement(element);
    }
    public void setLeftNode(AVLNode<E> temp){
		this.left = temp;
	}

    public void setRightNode(AVLNode<E> temp){
		this.right = temp;
	}

	public int getBalance(){
		int leftHeight = (left == null)?0:left.height();
		int rightHeight = (right == null)?0:right.height();

		return rightHeight - leftHeight;
	}	

	private int height(){
		int leftHeight = (left == null)?0:left.height();
		int rightHeight = (right == null)?0:right.height();

		return 1 + Math.max(leftHeight, rightHeight);
	}

	public String toString(){
		return assemble(this, 0);
	}

	private String assemble(AVLNode<E> temp, int offset){
		String ret = "";
		for (int i = 0; i < offset; i++)
			ret += "\t";

		ret += temp.getElement() + "\n";

		if (temp.getLeft() != null){
			ret += "Left: " + assemble(temp.getLeft(), offset + 1);
		}
		
		if (temp.getRight() != null){
			ret += "Right: " + assemble(temp.getRight(), offset + 1);
		}
		return ret;
	}
}
