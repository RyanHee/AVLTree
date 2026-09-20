import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * A naturally ordered tree that restores AVL balance after insertion and removal.
 * Duplicate and null handling, traversals, and statistics follow BinarySearchTree.
 * Mutation takes O(log n) time using cached node heights. Not thread-safe.
 * @param <T> the naturally ordered value type
 */
public class AVLTree<T extends Comparable<? super T>> extends BinarySearchTree<T> {
    @Override
    public void add(T value) {
        Objects.requireNonNull(value, "value");
        setRoot(insert(root(), value));
    }

    private AVLNode<T> insert(BinaryNode<T> node, T value) {
        if (node == null) {
            return new AVLNode<>(value);
        }
        int comparison = value.compareTo(node.value());
        if (comparison < 0) {
            node.setLeft(insert(node.left(), value));
        } else if (comparison > 0) {
            node.setRight(insert(node.right(), value));
        } else {
            return (AVLNode<T>) node;
        }
        return rebalance((AVLNode<T>) node);
    }

    @Override
    public BinaryNode<T> remove(T value) {
        Objects.requireNonNull(value, "value");
        ArrayList<BinaryNode<T>> removed = new ArrayList<>(1);
        setRoot(remove(root(), value, removed));
        return removed.isEmpty() ? null : removed.get(0);
    }

    private BinaryNode<T> remove(BinaryNode<T> node, T value,
                                 ArrayList<BinaryNode<T>> removed) {
        if (node == null) {
            return null;
        }
        int comparison = value.compareTo(node.value());
        if (comparison < 0) {
            node.setLeft(remove(node.left(), value, removed));
        } else if (comparison > 0) {
            node.setRight(remove(node.right(), value, removed));
        } else if (node.left() != null && node.right() != null) {
            BinaryNode<T> successor = node.right();
            while (successor.left() != null) {
                successor = successor.left();
            }
            T removedValue = node.value();
            node.setValue(successor.value());
            node.setRight(remove(node.right(), successor.value(), removed));
            removed.get(0).setValue(removedValue);
        } else {
            BinaryNode<T> child = node.left() != null ? node.left() : node.right();
            node.setLeft(null);
            node.setRight(null);
            ((AVLNode<T>) node).updateHeight();
            removed.add(node);
            return child;
        }
        return rebalance((AVLNode<T>) node);
    }

    private AVLNode<T> rebalance(AVLNode<T> node) {
        node.updateHeight();
        if (node.getBalanceFactor() > 1) {
            AVLNode<T> left = (AVLNode<T>) node.left();
            if (left.getBalanceFactor() < 0) {
                node.setLeft(rotateLeft(left));
            }
            return rotateRight(node);
        }
        if (node.getBalanceFactor() < -1) {
            AVLNode<T> right = (AVLNode<T>) node.right();
            if (right.getBalanceFactor() > 0) {
                node.setRight(rotateRight(right));
            }
            return rotateLeft(node);
        }
        return node;
    }

    private AVLNode<T> rotateRight(AVLNode<T> node) {
        AVLNode<T> pivot = (AVLNode<T>) node.left();
        node.setLeft(pivot.right());
        pivot.setRight(node);
        node.updateHeight();
        pivot.updateHeight();
        return pivot;
    }

    private AVLNode<T> rotateLeft(AVLNode<T> node) {
        AVLNode<T> pivot = (AVLNode<T>) node.right();
        node.setRight(pivot.left());
        pivot.setLeft(node);
        node.updateHeight();
        pivot.updateHeight();
        return pivot;
    }

    /**
     * Supplies six levels for the existing Swing display, including empty slots.
     * @return 63 labels in level order, with spaces for missing nodes
     */
    public String[] forDraw() {
        String[] labels = new String[63];
        Queue<BinaryNode<T>> queue = new LinkedList<>();
        queue.add(root());
        for (int i = 0; i < labels.length; i++) {
            BinaryNode<T> node = queue.remove();
            labels[i] = node == null ? " " : node.value().toString();
            if (i < 31) {
                queue.add(node == null ? null : node.left());
                queue.add(node == null ? null : node.right());
            }
        }
        return labels;
    }
}
