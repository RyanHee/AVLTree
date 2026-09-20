import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import java.util.TreeSet;

/** Dependency-free regression suite. Run with java -cp build TestBST. */
public class TestBST {
    public static void main(String[] args) {
        long seed = args.length == 0 ? new Random().nextLong() : Long.parseLong(args[0]);
        System.out.println("Random seed: " + seed + " (pass this seed as an argument to reproduce)");
        Random random = new Random(seed);
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        equal(false, tree.contains(random.nextInt()));
        equal(null, tree.remove(random.nextInt()));
        equal(-1, tree.getHeight());
        equal(0, tree.getNumLevels());
        equal(0, tree.getNumNodes());
        equal(0, tree.getNumLeaves());
        equal(0, tree.getWidth());
        equal(0, tree.getDiameter());
        equal(true, tree.isFull());
        equal(null, tree.getSmallest());
        equal(null, tree.getLargest());
        equal(new ArrayList<Integer>(), tree.preOrder());
        equal(new ArrayList<Integer>(), tree.inOrder());
        equal(new ArrayList<Integer>(), tree.postOrder());
        equal(new ArrayList<Integer>(), tree.reverseOrder());
        equal(new ArrayList<Integer>(), tree.levelOrder());

        // Random values, inserted by sorted rank to retain exact shape checks.
        ArrayList<Integer> values = randomValues(random, 7);
        ArrayList<Integer> insertionOrder = select(values, 3, 1, 5, 0, 2, 4, 6, 3);
        for (int value : insertionOrder) {
            tree.add(value);
        }
        equal(7, tree.getNumNodes());
        equal(4, tree.getNumLeaves());
        equal(2, tree.getHeight());
        equal(3, tree.getNumLevels());
        equal(5, tree.getDiameter());
        equal(5, tree.getDiameter());
        equal(4, tree.getWidth());
        equal(true, tree.isFull());
        equal(0, tree.getWidthAtLevel(-1));
        equal(1, tree.getWidthAtLevel(0));
        equal(4, tree.getWidthAtLevel(2));
        equal(0, tree.getWidthAtLevel(Integer.MAX_VALUE));
        equal(select(values, 3, 1, 0, 2, 5, 4, 6), tree.preOrder());
        equal(values, tree.inOrder());
        equal(select(values, 0, 2, 1, 4, 6, 5, 3), tree.postOrder());
        equal(select(values, 6, 5, 4, 3, 2, 1, 0), tree.reverseOrder());
        equal(select(values, 3, 1, 5, 0, 2, 4, 6), tree.levelOrder());
        for (int value : select(values, 3, 1, 5, 0, 2, 4, 6)) {
            BinaryNode<Integer> removed = tree.remove(value);
            equal(value, removed.value());
            equal(null, removed.left());
            equal(null, removed.right());
            equal(false, tree.contains(value));
        }
        equal(0, tree.getNumNodes());
        equal(0, tree.getDiameter());

        // Longest path is wholly within the root's left subtree.
        BinarySearchTree<Integer> branched = new BinarySearchTree<>();
        ArrayList<Integer> branchValues = randomValues(random, 8);
        for (int value : select(branchValues, 7, 3, 2, 1, 0, 4, 5, 6)) {
            branched.add(value);
        }
        equal(7, branched.getDiameter());
        equal(false, branched.isFull());

        BinarySearchTree<BigDecimal> decimals = new BinarySearchTree<>();
        BigDecimal decimal = BigDecimal.valueOf(random.nextInt()).setScale(1);
        decimals.add(decimal);
        decimals.add(decimal.setScale(2));
        equal(1, decimals.getNumNodes());
        equal(true, decimals.contains(decimal.setScale(3)));
        equal(decimal, decimals.remove(decimal.setScale(3)).value());
        rejectsNull(() -> decimals.add(null));
        rejectsNull(() -> decimals.contains(null));
        rejectsNull(() -> decimals.remove(null));

        for (int run = 0; run < 20; run++) {
            BinarySearchTree<Integer> actual = new BinarySearchTree<>();
            TreeSet<Integer> expected = new TreeSet<>();
            ArrayList<Integer> pool = randomValues(random, 100);
            for (int step = 0; step < 500; step++) {
                int value = pool.get(random.nextInt(pool.size()));
                if (random.nextBoolean()) {
                    actual.add(value);
                    expected.add(value);
                } else {
                    boolean existed = expected.remove(value);
                    BinaryNode<Integer> removed = actual.remove(value);
                    equal(existed, removed != null);
                    if (removed != null) {
                        equal(value, removed.value());
                        equal(null, removed.left());
                        equal(null, removed.right());
                    }
                }
                equal(expected.size(), actual.getNumNodes());
                equal(expected.contains(value), actual.contains(value));
                equal(expected.isEmpty() ? null : expected.first(), actual.getSmallest());
                equal(expected.isEmpty() ? null : expected.last(), actual.getLargest());
                equal(new ArrayList<>(expected), actual.inOrder());
                ArrayList<Integer> descending = new ArrayList<>(expected);
                Collections.reverse(descending);
                equal(descending, actual.reverseOrder());
            }
        }
        System.out.println("All BST regression tests passed (including 10,000 randomized operations).");
    }

    private static ArrayList<Integer> randomValues(Random random, int count) {
        TreeSet<Integer> values = new TreeSet<>();
        while (values.size() < count) {
            values.add(random.nextInt());
        }
        return new ArrayList<>(values);
    }

    private static ArrayList<Integer> select(ArrayList<Integer> values, int... ranks) {
        ArrayList<Integer> selected = new ArrayList<>();
        for (int rank : ranks) {
            selected.add(values.get(rank));
        }
        return selected;
    }

    private static void equal(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("Expected " + expected + ", got " + actual);
        }
    }

    private static void rejectsNull(Runnable action) {
        try {
            action.run();
            throw new AssertionError("Expected NullPointerException");
        } catch (NullPointerException expected) {
            // Null values cannot be ordered.
        }
    }
}
