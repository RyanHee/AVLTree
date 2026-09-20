import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * An unbalanced binary search tree containing distinct, non-null values.
 * Values are ordered by {@link Comparable#compareTo(Object)}; a comparison of
 * zero identifies duplicates even when {@code equals} differs. Values must
 * retain their ordering while stored in the tree. This class is not thread-safe.
 *
 * @param <T> the naturally ordered value type
 */
public class BinarySearchTree<T extends Comparable<? super T>> {
    /** Root node, or null when empty. */
    private BinaryNode<T> root;
    /** Largest path length seen during the current diameter calculation. */
    private int diameter;
    /**
     * Provides the root to subclasses implementing balanced trees.
     * @return the root, or null when empty
     */
    protected BinaryNode<T> root() {
        return root;
    }

    /**
     * Replaces the root after a subclass updates or rotates the tree.
     * @param node the new root, possibly null
     */
    protected void setRoot(BinaryNode<T> node) {
        root = node;
    }

    /**
     * Creates an empty tree.
     */
    public BinarySearchTree() {
        root = null;
    }

    /**
     * Inserts a value unless a comparison-equivalent value already exists.
     *
     * @param val the value to insert
     * @throws NullPointerException if {@code val} is null
     */
    public void add(T val) {
        java.util.Objects.requireNonNull(val, "value");
        BinaryNode<T> b = new BinaryNode<>(val);
        if (root == null) {
            root = b;
        }
        else {
            add(b, root);
        }
    }

    /**
     * Inserts a node into a nonempty subtree, ignoring duplicate values.
     *
     * @param addNode the node to insert
     * @param b the current subtree root
     */
    private void add(BinaryNode<T> addNode, BinaryNode<T> b) {
        if (addNode.value().compareTo(b.value())<0) {
            if (b.left() == null) {
                b.setLeft(addNode);
            }
            else {
                add(addNode, b.left());
            }
        }
        else if (addNode.value().compareTo(b.value())>0) {
            if (b.right() == null) {
                b.setRight(addNode);
            }
            else {
                add(addNode, b.right());
            }
        }
        else {
            return;
        }
    }

    /**
     * Checks whether every node has either zero or two children.
     *
     * @return true if the tree is full, including an empty tree
     */
    public boolean isFull() {
        return isFull(root);
    }

    /**
     * Checks fullness recursively.
     *
     * @param node the subtree root, possibly null
     * @return whether the subtree is full
     */
    private boolean isFull(BinaryNode<T> node) {
        if (node == null) {
            return true;
        }
        if (node.right() != null && node.left() != null) {
            return isFull(node.right()) && isFull(node.left());
        }
        else if (node.right() == null && node.left() == null) {
            return true;
        }
        return false;
    }
    /**
     * Searches for a value using natural ordering.
     *
     * @param value the value to find
     * @return true if a comparison-equivalent value exists
     * @throws NullPointerException if {@code value} is null
     */
    public boolean contains(T value) {
        java.util.Objects.requireNonNull(value, "value");
        BinaryNode<T> node = root;
        while (node != null) {
            int comparison = value.compareTo(node.value());
            if (comparison == 0) {
                return true;
            }
            node = comparison < 0 ? node.left() : node.right();
        }
        return false;
    }

    /**
     * Counts the nodes at a zero-based depth, with the root at level zero.
     *
     * @param level the depth to inspect
     * @return the node count, or zero for a negative or nonexistent level
     */
    public int getWidthAtLevel(int level) {
        if (level < 0) {
            return 0;
        }
        if (root == null) {
            return 0;
        }

        ArrayList<BinaryNode<T>> list = new ArrayList<>();
        list.add(root);
        for (int i = 0; i<level && !list.isEmpty(); i++) {
            ArrayList<BinaryNode<T>> nList = new ArrayList<>();
            for (BinaryNode<T> node:list) {
                if (node.left() != null) {
                    nList.add(node.left());
                }
                if (node.right() != null) {
                    nList.add(node.right());
                }
            }
            list = nList;
        }

        int width = list.size();
        return width;
    }

    /**
     * Returns the number of occupied levels.
     *
     * @return the height plus one, or zero for an empty tree
     */
    public int getNumLevels() {
        return getHeight()+1;
    }

    /**
     * Measures the longest downward path from the root in edges.
     *
     * @return the height; zero for a leaf and -1 for an empty tree
     */
    public int getHeight() {
        return getHeight(root);
    }

    /**
     * Calculates subtree height in edges.
     *
     * @param node the subtree root, possibly null
     * @return the height, or -1 for a null subtree
     */
    private int getHeight(BinaryNode<T> node) {
        return node == null ? -1 : 1 + Math.max(getHeight(node.left()), getHeight(node.right()));
    }

    /**
     * Counts the nodes on the longest path between any two nodes.
     * The path need not pass through the root. Each call recalculates the diameter
     * in linear time and resets the internal accumulator.
     *
     * @return the diameter in nodes, or zero for an empty tree
     */
    public int getDiameter() {
        diameter = 0;
        diameterHeight(root);
        return diameter;
    }

    /**
     * Updates the diameter accumulator while computing subtree height in nodes.
     *
     * @param node the subtree root, possibly null
     * @return the height in nodes, or zero for a null subtree
     */
    private int diameterHeight(BinaryNode<T> node) {
        if (node == null) {
            return 0;
        }
        int left = diameterHeight(node.left());
        int right = diameterHeight(node.right());
        diameter = Math.max(diameter, left + right + 1);
        return 1 + Math.max(left, right);
    }

    /**
     * Counts all nodes in the tree.
     *
     * @return the node count, or zero for an empty tree
     */
    public int getNumNodes() {
        if (root == null) {
            return 0;
        }
        return getNumNodes(root);
    }

    /**
     * Counts nodes recursively.
     *
     * @param node the subtree root, possibly null
     * @return the subtree node count
     */
    private int getNumNodes(BinaryNode<T> node) {
        return node == null ? 0 : 1 + getNumNodes(node.left()) + getNumNodes(node.right());
    }

    /**
     * Counts nodes with no children.
     *
     * @return the leaf count, or zero for an empty tree
     */
    public int getNumLeaves() {
        if (root == null) {
            return 0;
        }
        return getNumLeaves(root);
    }

    /**
     * Counts leaves recursively.
     *
     * @param node a non-null subtree root
     * @return the subtree leaf count
     */
    private int getNumLeaves(BinaryNode<T> node) {
        if (node.right() == null && node.left() == null) {
            return 1;
        }
        int ans = 0;
        if (node.left() != null) {
            ans += getNumLeaves(node.left());
        }
        if (node.right() != null) {
            ans += getNumLeaves(node.right());
        }
        return ans;
    }

    /**
     * Finds the greatest number of nodes on any single level.
     *
     * @return the maximum width, or zero for an empty tree
     */
    public int getWidth() {
        if (root == null) {
            return 0;
        }
        Queue<BinaryNode<T>> queue = new LinkedList<>();
        queue.add(root);
        int maxWidth = 0;
        while (!queue.isEmpty()) {
            int length = queue.size();
            maxWidth = Math.max(length, maxWidth);
            for (int i = 0; i<length; i++) {
                BinaryNode<T> node = queue.remove();
                if (node.right() != null) {
                    queue.add(node.right());
                }
                if (node.left() != null) {
                    queue.add(node.left());
                }
            }
        }
        return maxWidth;
    }

    /**
     * Finds the smallest value by following left children.
     *
     * @return the minimum value, or null for an empty tree
     */
    public T getSmallest() {
        if (root == null) {
            return null;
        }
        else {
            BinaryNode<T> node = root;
            while (node.left() != null) {
                node = node.left();
            }
            return node.value();
        }
    }

    /**
     * Finds the largest value by following right children.
     *
     * @return the maximum value, or null for an empty tree
     */
    public T getLargest() {
        if (root == null) {
            return null;
        }
        if (root.right() == null) {
            return root.value();
        }
        else {
            BinaryNode<T> node = root;
            while (node.right() != null) {
                node = node.right();
            }
            return node.value();
        }

    }

    /**
     * Visits values in this order: root, left subtree, right subtree.
     *
     * @return a new mutable list of the stored value references, empty if the tree
     *         is empty; modifying the list does not change the tree
     */
    public ArrayList<T> preOrder() {
        return preOrder(root, new ArrayList<>());
    }

    /**
     * Appends a subtree in preorder.
     *
     * @param node the subtree root, possibly null
     * @param list the destination list
     * @return the supplied list
     */
    private ArrayList<T> preOrder(BinaryNode<T> node, ArrayList<T> list) {
        if (node == null) {
            return list;
        }
        list.add(node.value());
        if (node.left() != null) {
            preOrder(node.left(), list);
        }
        if (node.right() != null) {
            preOrder(node.right(), list);
        }
        return list;
    }

    /**
     * Visits values in this order: left subtree, right subtree, root.
     *
     * @return a new mutable list of the stored value references, empty if the tree
     *         is empty; modifying the list does not change the tree
     */
    public ArrayList<T> postOrder() {
        ArrayList<T> result = new ArrayList<>();
        postOrder(root, result);
        return result;
    }

    /**
     * Appends a subtree in postorder.
     *
     * @param node the subtree root, possibly null
     * @param result the destination list
     */
    private void postOrder(BinaryNode<T> node, ArrayList<T> result) {
        if (node == null) {
            return;
        }
        postOrder(node.left(), result);
        postOrder(node.right(), result);
        result.add(node.value());
    }

    /**
     * Visits values in this order: left subtree, root, right subtree (ascending order).
     *
     * @return a new mutable list of the stored value references, empty if the tree
     *         is empty; modifying the list does not change the tree
     */
    public ArrayList<T> inOrder() {
        ArrayList<T> result = new ArrayList<>();
        inOrder(root, result);
        return result;
    }

    /**
     * Appends a subtree in ascending order.
     *
     * @param node the subtree root, possibly null
     * @param result the destination list
     */
    private void inOrder(BinaryNode<T> node, ArrayList<T> result) {
        if (node == null) {
            return;
        }
        inOrder(node.left(), result);
        result.add(node.value());
        inOrder(node.right(), result);
    }

    /**
     * Visits values in this order: right subtree, root, left subtree (descending order).
     *
     * @return a new mutable list of the stored value references, empty if the tree
     *         is empty; modifying the list does not change the tree
     */
    public ArrayList<T> reverseOrder() {
        ArrayList<T> result = new ArrayList<>();
        reverseOrder(root, result);
        return result;
    }

    /**
     * Appends a subtree in descending order.
     *
     * @param node the subtree root, possibly null
     * @param result the destination list
     */
    private void reverseOrder(BinaryNode<T> node, ArrayList<T> result) {
        if (node == null) {
            return;
        }
        reverseOrder(node.right(), result);
        result.add(node.value());
        reverseOrder(node.left(), result);
    }

    /**
     * Visits values in this order: level by level, from left to right.
     *
     * @return a new mutable list of the stored value references, empty if the tree
     *         is empty; modifying the list does not change the tree
     */
    public ArrayList<T> levelOrder() {
        ArrayList<T> ans = new ArrayList<>();
        if (root == null) {
            return ans;
        }
        ArrayList<BinaryNode<T>> list = new ArrayList<>();
        list.add(root);
        while (!list.isEmpty()) {
            ArrayList<BinaryNode<T>> nList = new ArrayList<>();
            for (BinaryNode<T> node : list) {
                ans.add(node.value());
                if (node.left() != null) {
                    nList.add(node.left());
                }
                if (node.right() != null) {
                    nList.add(node.right());
                }
            }
            list = nList;
        }

        return ans;
    }

    /**
     * Removes a comparison-equivalent value, preserving the search-tree ordering.
     * For a node with two children, its inorder successor supplies the replacement.
     *
     * @param value the value to remove
     * @return a detached node containing the stored value that was removed, with
     *         both children null; null if no matching value exists
     * @throws NullPointerException if {@code value} is null
     */
    public BinaryNode<T> remove(T value) {
        java.util.Objects.requireNonNull(value, "value");
        BinaryNode<T> parent = null;
        BinaryNode<T> node = root;
        while (node != null) {
            int comparison = value.compareTo(node.value());
            if (comparison == 0) {
                break;
            }
            parent = node;
            node = comparison < 0 ? node.left() : node.right();
        }
        if (node == null) {
            return null;
        }

        if (node.left() != null && node.right() != null) {
            BinaryNode<T> successorParent = node;
            BinaryNode<T> successor = node.right();
            while (successor.left() != null) {
                successorParent = successor;
                successor = successor.left();
            }
            T removedValue = node.value();
            node.setValue(successor.value());
            successor.setValue(removedValue);
            parent = successorParent;
            node = successor;
        }
        BinaryNode<T> child = node.left() != null ? node.left() : node.right();
        if (parent == null) {
            root = child;
        } else if (parent.left() == node) {
            parent.setLeft(child);
        } else {
            parent.setRight(child);
        }
        node.setLeft(null);
        node.setRight(null);
        return node;
    }

    /**
     * Formats the inorder traversal as a bracketed, comma-separated list.
     *
     * @return the values in ascending order, or {@code []} for an empty tree
     */
    @Override
    public String toString() {
        return inOrder().toString();
    }

}
