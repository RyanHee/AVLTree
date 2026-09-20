package main;

/**
 * A binary node with a cached height for AVL balancing.
 * @param <T> the naturally ordered value type
 */
public class AVLNode<T extends Comparable<? super T>> extends BinaryNode<T> {
    private int height;

    /** @param value the non-null value stored in this node */
    public AVLNode(T value) {
        super(value);
    }

    /** @return the cached height in edges */
    public int getHeight() {
        return height;
    }

    /** @return left height minus right height */
    public int getBalanceFactor() {
        return height(left()) - height(right());
    }

    /** Refreshes the height after children have been updated. */
    void updateHeight() {
        height = 1 + Math.max(height(left()), height(right()));
    }

    private int height(BinaryNode<T> node) {
        return node == null ? -1 : ((AVLNode<T>) node).getHeight();
    }

    @Override
    public String toString() {
        return value().toString();
    }
}
