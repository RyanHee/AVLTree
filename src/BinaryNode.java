public class BinaryNode<T extends Comparable<? super T>> {
    private BinaryNode<T> left;
    private BinaryNode<T> right;
    private T value;

    public BinaryNode(T val) {
        value = java.util.Objects.requireNonNull(val, "value");
        left = null;
        right = null;
    }

    public void setRight(BinaryNode<T> r) {
        right = r;
    }
    public void setLeft(BinaryNode<T> l) {
        left = l;
    }

    public BinaryNode<T> left() {
        return left;
    }

    public BinaryNode<T> right() {
        return right;
    }
    public T value() {
        return value;
    }

    public void setValue(T x) {
        value = java.util.Objects.requireNonNull(x, "value");
    }

}
