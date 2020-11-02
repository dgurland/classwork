package cs3114.J3.DS;

import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

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

public class prQuadTree<T extends Compare2D<? super T>> {

    abstract class prQuadNode {

    }


    class prQuadLeaf extends prQuadNode {

        public ArrayList<T> Elements;


        /**
         * Creates a new empty quad leaf
         */
        public prQuadLeaf() {
            this.Elements = new ArrayList<>();
        }


        /**
         * Creates a new quad leaf
         * @param elem the value stored in the leaf
         */
        public prQuadLeaf(T elem) {
            this.Elements = new ArrayList<>();
            this.Elements.add(elem);
        }
    }


    class prQuadInternal extends prQuadNode {
        public prQuadNode NW, SW, SE, NE;


        /**
         * creates a new internal node with each of its leaves initialized
         * to null
         */
        public prQuadInternal() {
            this.NW = null;
            this.SW = null;
            this.SE = null;
            this.NE = null;
        }
    }

    prQuadNode root;
    long xMin, xMax, yMin, yMax;


    // Initialize quadtree to empty state.
    public prQuadTree(long xMin, long xMax, long yMin, long yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.root = null;
    }


    // Pre: elem != null
    // Post: If elem lies within the tree's region, and elem is not already
    // present in the tree, elem has been inserted into the tree.
    // Return true iff elem is inserted into the tree.
    public boolean insert(T elem) {
        if (!elem.inBox(this.xMin, this.xMax, this.yMin, this.yMax)) {
            return false;
        }
        prQuadNode current = this.root;
        long xMid = (xMin + xMax) / 2;
        long yMid = (yMin + yMax) / 2;
        long dist = xMax - xMin;
        root = insert(root, elem, xMid, yMid, dist);
        return !root.equals(current);
    }


    /**
     * recursive insert method 
     * @param root the node currently being examined
     * @param elem the element to insert
     * @param xMid the midpoint of the current region being examined
     * @param yMid ^^
     * @param dist the width of the region currently being examined
     * @return the new tree with the element inserted if possible
     */
    private prQuadNode insert(
        prQuadNode root,
        T elem,
        long xMid,
        long yMid,
        long dist) {

        if (root == null) {
            return new prQuadLeaf(elem);
        }
        prQuadInternal rootInternal;
        if (root instanceof prQuadTree.prQuadLeaf) {
            prQuadLeaf leaf = (prQuadLeaf)root;
            T currentElem = leaf.Elements.get(0);
            if (currentElem.equals(elem)) {
                return root;
            }
            rootInternal = new prQuadInternal();
            Direction currDir = currentElem.directionFrom(xMid, yMid);
            switch (currDir) {
                case NE:
                    rootInternal.NE = new prQuadLeaf(currentElem);
                    break;
                case NW:
                    rootInternal.NW = new prQuadLeaf(currentElem);
                    break;
                case SW:
                    rootInternal.SW = new prQuadLeaf(currentElem);
                    break;
                case SE:
                    rootInternal.SE = new prQuadLeaf(currentElem);
                    break;
                default:
                    break;
            }
        }
        else {
            rootInternal = (prQuadInternal)root;
        }
        switch (elem.directionFrom(xMid, yMid)) {
            case NE:
                long xNE = xMid + (dist / 4);
                long yNE = yMid + (dist / 4);
                rootInternal.NE = insert(rootInternal.NE, elem, xNE, yNE, dist
                    / 2);
                break;
            case NW:
                long xNW = xMid - (dist / 4);
                long yNW = yMid + (dist / 4);
                rootInternal.NW = insert(rootInternal.NW, elem, xNW, yNW, dist
                    / 2);
                break;
            case SW:
                long xSW = xMid - (dist / 4);
                long ySW = yMid - (dist / 4);
                rootInternal.SW = insert(rootInternal.SW, elem, xSW, ySW, dist
                    / 2);
                break;
            case SE:
                long xSE = xMid + (dist / 4);
                long ySE = yMid - (dist / 4);
                rootInternal.SE = insert(rootInternal.SE, elem, xSE, ySE, dist
                    / 2);
                break;
            default:
                break;
        }
        return rootInternal;

    }


    // Pre: elem != null
    // Returns reference to an element x within the tree such that
    // elem.equals(x)
    // is true, provided such a matching element occurs within the tree; returns
    // null otherwise.
    public T find(T Elem) {

        long xMid = (xMin + xMax) / 2;
        long yMid = (yMin + yMax) / 2;
        long dist = xMax - xMin;
        return find(root, Elem, xMid, yMid, dist);
    }


    /**
     * helper recursive method for find function
     * @param root the node currently being examined
     * @param elem the element to insert
     * @param xMid the midpoint of the current region being examined
     * @param yMid ^^
     * @param dist the width of the region currently being examined
     * @return the element if it was found, otherwise null
     */
    private T find(prQuadNode root, T elem, long xMid, long yMid, long dist) {
        if (root == null) {
            return null;
        }
        if (root instanceof prQuadTree.prQuadLeaf) {
            prQuadLeaf rootLeaf = (prQuadLeaf)root;
            if (rootLeaf.Elements.get(0).equals(elem)) {
                return rootLeaf.Elements.get(0);
            }
            return null;
        }
        prQuadInternal rootInternal = (prQuadInternal)root;
        switch (elem.directionFrom(xMid, yMid)) {
            case NE:
                long xNE = xMid + (dist / 4);
                long yNE = yMid + (dist / 4);
                return find(rootInternal.NE, elem, xNE, yNE, dist / 2);
            case NW:
                long xNW = xMid - (dist / 4);
                long yNW = yMid + (dist / 4);
                return find(rootInternal.NW, elem, xNW, yNW, dist / 2);
            case SW:
                long xSW = xMid - (dist / 4);
                long ySW = yMid - (dist / 4);
                return find(rootInternal.SW, elem, xSW, ySW, dist / 2);
            case SE:
                long xSE = xMid + (dist / 4);
                long ySE = yMid - (dist / 4);
                return find(rootInternal.SE, elem, xSE, ySE, dist / 2);
            default:
                return null;
        }
    }


    // Pre: xLo, xHi, yLo and yHi define a rectangular region
    // Returns a collection of (references to) all elements x such that x is in
    // the tree and x lies at coordinates within the defined rectangular region,
    // including the boundary of the region.
    public ArrayList<T> find(long xLo, long xHi, long yLo, long yHi) {
        ArrayList<T> results = new ArrayList<>();
        long xMid = (xMax + xMin) / 2;
        long yMid = (yMin + yMax) / 2;
        long dist = xMax - xMin;
        return find(results, root, xLo, xHi, yLo, yHi, xMid, yMid, dist);

    }


    /**
     * the helper recursive method for finding elements in a region
     * @param results the list of elements so far
     * @param root the node currently being examined
     * @param xLo x-low of region being searched
     * @param xHi x-high of region
     * @param yLo y-low of region
     * @param yHi y-high of region
     * @param xMid midpoint of node currently being examined
     * @param yMid ^^
     * @param dist width of node currently being examined
     * @return the list of elements found
     */
    private ArrayList<T> find(
        ArrayList<T> results,
        prQuadNode root,
        long xLo,
        long xHi,
        long yLo,
        long yHi,
        long xMid,
        long yMid,
        long dist) {
        if (root == null) {
            return results;
        }
        if (root instanceof prQuadTree.prQuadLeaf) {
            prQuadLeaf rootLeaf = (prQuadLeaf)root;
            T leafValue = rootLeaf.Elements.get(0);
            if (leafValue.inBox(xLo, xHi, yLo, yHi)) {
                results.add(leafValue);
            }
            return results;
        }
        prQuadInternal rootInternal = (prQuadInternal)root;

        if (xHi >= xMid && yHi >= yMid) {
            results = find(results, rootInternal.NE, xLo, xHi, yLo, yHi, (xMid
                + dist / 4), (yMid + dist / 4), dist / 2);
        }
        if (xLo <= xMid && yHi >= yMid) {
            results = find(results, rootInternal.NW, xLo, xHi, yLo, yHi, (xMid
                - dist / 4), (yMid + dist / 4), dist / 2);
        }
        if (xLo <= xMid && yLo <= yMid) {
            results = find(results, rootInternal.SW, xLo, xHi, yLo, yHi, (xMid
                - dist / 4), (yMid - dist / 4), dist / 2);
        }
        if (xHi >= xMid && yLo <= yMid) {
            results = find(results, rootInternal.SE, xLo, xHi, yLo, yHi, (xMid
                + dist / 4), (yMid - dist / 4), dist / 2);
        }
        return results;
    }
}
