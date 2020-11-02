package cs3114.J2.DS;

// On my honor:
//
// - I have not discussed the Java language code in my program with
// anyone other than my instructor or the teaching assistants
// assigned to this course.
//
// - I have not used Java language code obtained from another student,
// or any other unauthorized source, including the Internet, either
// modified or unmodified.
//
// - If any Java language code or documentation used in my program
// was obtained from an allowed source, such as a text book or course
// notes, that has been clearly noted with a proper citation in
// the comments of my program.
//
// - I have not designed this program in such a way as to defeat or
// interfere with the normal operation of the grading code.
//
// Dana Gurland
// dgurland@vt.edu
import java.util.Vector;
import cs3114.J2.DS.BST.BinaryNode;

public class BST<T extends Comparable<? super T>> {
    class BinaryNode {

        // Constructors
        // Initialize a childless binary node.
        // Pre: elem is not null
        // Post: (in the new node)
        // element == elem
        // left == right == null
        public BinaryNode(T theElement) {
            if (theElement != null) {
                this.element = theElement;
                this.left = null;
                this.right = null;
            }
        }


        // Initialize a binary node with children.
        // Pre: elem is not null
        // Post: (in the new node)
        // element == elem
        // left == lt, right == rt
        public BinaryNode(T theElement, BinaryNode lt, BinaryNode rt) {
            if (theElement != null) {
                this.element = theElement;
                this.left = lt;
                this.right = rt;
            }
        }

        // The following members have package access to support the test
        // harness.
        T element; // The data in the node
        BinaryNode left; // Pointer to the left child
        BinaryNode right; // Pointer to the right child
    }

    // The following members have package access to support the test harness.
    BinaryNode root; // pointer to root node, if present
    BinaryNode pool; // pointer to first node in the pool
    int pSize; // size limit for node pool
    int currSize;


    // Initialize empty BST with no node pool.
    // Pre: none
    // Post: (in the new tree)
    // root == null, pool == null, pSize = 0
    public BST() {
        // complete this method
        this.root = null;
        this.pool = null;
        this.pSize = 0;
        this.currSize = 0;
    }


    // Initialize empty BST with a node pool of up to pSize nodes.
    // Pre: none
    // Post: (in the new tree)
    // root == null, pool = null, pSize == Sz
    public BST(int Size) {
        this.root = null;
        this.pSize = Size;
        this.currSize = 0;

    }


    // Return true iff BST contains no nodes.
    // Pre: none
    // Post: the binary tree is unchanged
    public boolean isEmpty() {
        return this.root == null;
    }


    // Return pointer to matching data element, or null if no matching
    // element exists in the BST. "Matching" should be tested using the
    // data object's compareTo() method.
    // Pre: x is null or points to a valid object of type T
    // Post: the binary tree is unchanged
    public T find(T x) {
        return find(this.root, x);
    }


    /**
     * Helper for find
     */
    private T find(BinaryNode root, T x) {
        if (root == null || x == null) {
            return null;
        }
        if (root.element.compareTo(x) > 0) {
            return find(root.left, x);
        }
        else if (root.element.compareTo(x) < 0) {
            return find(root.right, x);
        }
        else {
            return root.element;
        }
    }


    // Insert element x into BST, unless it is already stored. Return true
    // if insertion is performed and false otherwise.
    // Pre: x is null or points to a valid object of type T
    // Post: the binary tree contains x
    public boolean insert(T x) {
        if (x == null) {
            return false;
        }
        BinaryNode temp = this.root;
        this.root = insert(this.root, x);
        // Verify that new tree is different from previous (successful
        // insertion)
        return !this.root.equals(temp);
    }


    /**
     * Helper for insert
     */
    private BinaryNode insert(BinaryNode root, T x) {
        // Found the end
        if (root == null) {
            if (this.currSize > 0) {
                if (this.currSize == 1) {
                    BinaryNode temp = this.pool;
                    this.pool = null;
                    temp.element = x;
                    return temp;
                }
                else if (this.currSize == 2) {
                    BinaryNode temp = this.pool.right;
                    this.pool.right = null;
                    temp.element = x;
                    return temp;
                }
                BinaryNode temp = this.pool;
                while (temp.right.right != null) {
                    temp = temp.right;
                }
                this.currSize--;
                BinaryNode secondTemp = temp.right;
                temp.right = null;
                secondTemp.element = x;
                return secondTemp;

            }
            else
                return new BinaryNode(x);
        }
        // Current element is greater than x
        else if (root.element.compareTo(x) > 0) {
            root.left = insert(root.left, x);
        }
        // Current element is less than x
        else if (root.element.compareTo(x) < 0) {
            root.right = insert(root.right, x);
        }
        return root;
    }


    // Delete element matching x from the BST, if present. Return true if
    // matching element is removed from the tree and false otherwise.
    // Pre: x is null or points to a valid object of type T
    // Post: the binary tree does not contain x
    public boolean remove(T x) {
        if (x == null) {
            return false;
        }
        BinaryNode temp = this.root;
        this.root = remove(this.root, x);
        // Verify successful removal by comparing to previous tree
        return !temp.equals(this.root);
    }


    /**
     * Helper for remove
     */
    private BinaryNode remove(BinaryNode root, T x) {

        // Base case
        if (root == null) {
            return null;
        }
        // Element to remove is smaller than current
        else if (root.element.compareTo(x) > 0) {
            root.left = remove(root.left, x);
        }
        // element to remove is greater than current
        else if (root.element.compareTo(x) < 0) {
            root.right = remove(root.right, x);
        }
        // current is element to remove
        else if (root.element.compareTo(x) == 0) {
            // element is leaf
            if (root.left == null && root.right == null) {

                if (this.pSize > 0) {
                    BinaryNode temp = this.pool;
                    int poolCount = 1;
                    while (temp.right != null && poolCount < this.currSize) {
                        temp = temp.right;
                        poolCount++;
                    }
                    temp.right = root;
                }

                root = null;

            }
            // element only has right leaf
            else if (root.left == null) {
                root = root.right;
            }
            // element only has left leaf
            else if (root.right == null) {
                root = root.left;
            }
            // find min to replace with if element has both leaves
            else {
                BinaryNode temp = getReplacer(root.right);
                BinaryNode secondTemp = remove(root.right, temp.element);
                root.right = secondTemp;
                temp.right = secondTemp;
                temp.left = root.left;
                return temp;

            }
        }
        return root;
    }


    /**
     * Helper for remove when replacing a node
     */
    private BinaryNode getReplacer(BinaryNode root) {

        if (root.left == null) {
            return root;
        }
        return getReplacer(root.left);
    }


    // Remove from the tree all values y such that y > x, according to
    // compareTo().
    // Pre: x is null or points to a valid object of type T
    // Post: the tree contains no value y such that compareTo()
    // indicates y > x
    public void capWith(T x) {

        this.root = capWith(this.root, x);

    }


    /**
     * Helper for cap
     */
    private BinaryNode capWith(BinaryNode root, T x) {
        // base case
        if (root == null) {
            return null;
        }
        root.left = capWith(root.left, x);
        root.right = capWith(root.right, x);
        // element to remove
        if (root.element.compareTo(x) > 0) {
            BinaryNode temp = root.left;
            root = null;
            return temp;
        }
        return root;

    }


    // Return true iff other is a BST that has the same physical structure
    // and stores equal data values in corresponding nodes. "Equal" should
    // be tested using the data object's equals() method.
    // Pre: other is null or points to a valid BST<> object, instantiated
    // on the same data type as the tree on which equals() is invoked
    // Post: both binary trees are unchanged
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {

        // object is null
        if (other == null) {
            return false;
        }
        // object is not a BST
        else if (other.getClass() != this.getClass()) {
            return false;
        }
        else {
            // safely cast other and check for equality
            BST node = (BST)other;
            return equals(this.root, node.root);
        }
    }


    /**
     * Helper for equals
     */
    private boolean equals(BinaryNode root, BinaryNode other) {
        if (root == null && other == null) {
            return true;
        }
        if (root == null && other != null) {
            return false;
        }
        else if (other == null && root != null) {
            return false;
        }
        if (root.element.compareTo(other.element) != 0) {
            return false;
        }
        else {
            return equals(root.left, other.left) && equals(root.right,
                other.right);
        }
    }


    // Writes a formatted display of the tree to standard output.
    public void printTree() {
        if (isEmpty())
            System.out.println("Empty tree");
        else
            printTree(root, 0);
    }


    // Helper function for public printTree()
    private void printTree(BinaryNode t, int Level) {
        if (t != null) {
            printTree(t.right, Level + 1);
            for (int p = 0; p < Level; p++) {
                System.out.print("   ");
            }
            System.out.println(t.element);
            printTree(t.left, Level + 1);
        }
    }

    // Private helper methods can be added below... or above... as you like

}
