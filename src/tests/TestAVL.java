import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;
import java.util.TreeSet;

/** Seeded randomized checks of AVL ordering, balance, and cached heights. */
public class TestAVL {
    private static class InspectableTree extends AVLTree<Integer> {
        BinaryNode<Integer> rootNode() {
            return root();
        }
    }

    public static void main(String[] args) {
        long seed = args.length == 0 ? new Random().nextLong() : Long.parseLong(args[0]);
        System.out.println("AVL random seed: " + seed);
        Random random = new Random(seed);
        // All four insertion rotation cases, using random ordered values.
        ArrayList<Integer> values = randomValues(random, 3);
        for (int[] order : new int[][]{{2, 1, 0}, {0, 1, 2}, {2, 0, 1}, {0, 2, 1}}) {
            InspectableTree tree = new InspectableTree();
            TreeSet<Integer> expected = new TreeSet<>();
            for (int rank : order) {
                tree.add(values.get(rank));
                expected.add(values.get(rank));
                verify(tree, expected);
            }
            equal(values.get(1), tree.rootNode().value());
            while (!expected.isEmpty()) {
                removeAndCheck(tree, expected, tree.rootNode().value());
            }
            removeAndCheck(tree, expected, random.nextInt());
        }

        for (int run = 0; run < 20; run++) {
            InspectableTree tree = new InspectableTree();
            TreeSet<Integer> expected = new TreeSet<>();
            ArrayList<Integer> pool = randomValues(random, 100);
            verify(tree, expected);
            for (int step = 0; step < 500; step++) {
                int value = pool.get(random.nextInt(pool.size()));
                if (random.nextBoolean()) {
                    // Ensure dispatch through the BST API still balances.
                    BinarySearchTree<Integer> base = tree;
                    base.add(value);
                    expected.add(value);
                    verify(tree, expected);
                } else {
                    removeAndCheck(tree, expected, value);
                }
            }
            ArrayList<Integer> remaining = new ArrayList<>(expected);
            Collections.shuffle(remaining, random);
            for (int value : remaining) {
                removeAndCheck(tree, expected, value);
            }
        }

        // Sorted insertion and deletion exercise repeated ancestor rebalancing.
        for (boolean reverse : new boolean[]{false, true}) {
            InspectableTree tree = new InspectableTree();
            TreeSet<Integer> expected = new TreeSet<>();
            ArrayList<Integer> ordered = randomValues(random, 1000);
            if (reverse) {
                Collections.reverse(ordered);
            }
            for (int value : ordered) {
                tree.add(value);
                expected.add(value);
                verify(tree, expected);
            }
            for (int value : ordered) {
                removeAndCheck(tree, expected, value);
            }
        }

        AVLTree<BigDecimal> decimals = new AVLTree<>();
        BigDecimal value = BigDecimal.valueOf(random.nextInt()).setScale(1);
        decimals.add(value);
        decimals.add(value.setScale(2));
        equal(1, decimals.getNumNodes());
        equal(true, decimals.contains(value.setScale(3)));
        equal(value, decimals.remove(value.setScale(3)).value());
        rejectsNull(() -> decimals.add(null));
        rejectsNull(() -> decimals.remove(null));
        rejectsNull(() -> decimals.contains(null));
        System.out.println("All AVL tests passed (10,000 mixed operations plus sorted insertion/deletion).");
    }

    private static void removeAndCheck(InspectableTree tree, TreeSet<Integer> expected, int value) {
        boolean existed = expected.remove(value);
        BinaryNode<Integer> removed = tree.remove(value);
        equal(existed, removed != null);
        if (removed != null) {
            equal(value, removed.value());
            equal(null, removed.left());
            equal(null, removed.right());
            equal(0, ((AVLNode<Integer>) removed).getHeight());
        }
        verify(tree, expected);
    }

    private static void verify(InspectableTree tree, TreeSet<Integer> expected) {
        equal(new ArrayList<>(expected), tree.inOrder());
        equal(expected.size(), tree.getNumNodes());
        equal(expected.isEmpty() ? null : expected.first(), tree.getSmallest());
        equal(expected.isEmpty() ? null : expected.last(), tree.getLargest());
        equal(checkNode(tree.rootNode(), null, null), tree.getHeight());
        equal(expected.size(), tree.levelOrder().size());
        equal(expected, new TreeSet<>(tree.levelOrder()));
        String[] labels = tree.forDraw();
        equal(63, labels.length);
        checkLabels(tree.rootNode(), labels, 0);
    }

    private static int checkNode(BinaryNode<Integer> node, Integer lower, Integer upper) {
        if (node == null) {
            return -1;
        }
        if ((lower != null && node.value() <= lower) || (upper != null && node.value() >= upper)) {
            throw new AssertionError("BST ordering violated at " + node.value());
        }
        int left = checkNode(node.left(), lower, node.value());
        int right = checkNode(node.right(), node.value(), upper);
        if (Math.abs(left - right) > 1) {
            throw new AssertionError("AVL balance violated at " + node.value());
        }
        int height = 1 + Math.max(left, right);
        equal(height, ((AVLNode<Integer>) node).getHeight());
        equal(left - right, ((AVLNode<Integer>) node).getBalanceFactor());
        return height;
    }

    private static void checkLabels(BinaryNode<Integer> node, String[] labels, int index) {
        if (index >= labels.length) {
            return;
        }
        equal(node == null ? " " : node.value().toString(), labels[index]);
        checkLabels(node == null ? null : node.left(), labels, 2 * index + 1);
        checkLabels(node == null ? null : node.right(), labels, 2 * index + 2);
    }

    private static ArrayList<Integer> randomValues(Random random, int count) {
        TreeSet<Integer> values = new TreeSet<>();
        while (values.size() < count) {
            values.add(random.nextInt());
        }
        return new ArrayList<>(values);
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
            // Null cannot be ordered.
        }
    }
}
