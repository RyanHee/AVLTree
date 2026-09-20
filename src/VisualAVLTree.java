import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An AVL tree used only by the graphics explorer. It duplicates
 * {@link AVLTree}'s insertion, removal, and rotation logic, but also records
 * which values took part in a rotation so the display can animate them
 * distinctly from an ordinary reflow. This tracking lives here, outside
 * {@link AVLTree}, so the core data structure stays free of display concerns.
 * @param <T> the naturally ordered value type
 */
public class VisualAVLTree<T extends Comparable<? super T>> extends BinarySearchTree<T> {
    private final Set<T> rotated = new LinkedHashSet<>();

    /**
     * Reports which values took part in a rotation during the most recent
     * {@link #add(Comparable)} or {@link #remove(Comparable)} call.
     * @return the rotated values, empty if that call rebalanced without rotating
     */
    public Set<T> lastRotatedValues() {
        return rotated;
    }

    @Override
    public void add(T value) {
        Objects.requireNonNull(value, "value");
        rotated.clear();
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
        rotated.clear();
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
        rotated.add(node.value());
        rotated.add(pivot.value());
        node.setLeft(pivot.right());
        pivot.setRight(node);
        node.updateHeight();
        pivot.updateHeight();
        return pivot;
    }

    private AVLNode<T> rotateLeft(AVLNode<T> node) {
        AVLNode<T> pivot = (AVLNode<T>) node.right();
        rotated.add(node.value());
        rotated.add(pivot.value());
        node.setRight(pivot.left());
        pivot.setLeft(node);
        node.updateHeight();
        pivot.updateHeight();
        return pivot;
    }
}
