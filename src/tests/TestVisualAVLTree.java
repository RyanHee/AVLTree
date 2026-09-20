package tests;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import main.AVLNode;
import main.BinaryNode;
import main.VisualAVLTree;

/**
 * Checks that VisualAVLTree keeps AVL invariants like AVLTree, and that its
 * rotation tracking (used to animate the graphics explorer) reports exactly
 * the nodes that rotated.
 */
public class TestVisualAVLTree {
    private static final class InspectableTree extends VisualAVLTree<Integer> {
        BinaryNode<Integer> rootNode() {
            return root();
        }
    }

    public static void main(String[] args) {
        long seed = args.length == 0 ? new Random().nextLong() : Long.parseLong(args[0]);
        System.out.println("VisualAVLTree random seed: " + seed);
        Random random = new Random(seed);

        checkRotationCases();

        InspectableTree tree = new InspectableTree();
        TreeSet<Integer> expected = new TreeSet<>();
        verify(tree, expected);
        ArrayList<Integer> pool = randomValues(random, 200);
        for (int step = 0; step < 5000; step++) {
            int value = pool.get(random.nextInt(pool.size()));
            if (random.nextBoolean()) {
                tree.add(value);
                expected.add(value);
            } else {
                expected.remove(value);
                tree.remove(value);
            }
            for (int rotated : tree.lastRotatedValues()) {
                require(tree.contains(rotated), "Rotated value missing from tree: " + rotated);
            }
            verify(tree, expected);
        }
        System.out.println("All VisualAVLTree tests passed (rotation tracking plus 5,000 mixed operations).");
    }

    /** Exercises the four classic rotation shapes plus a rotation triggered by removal. */
    private static void checkRotationCases() {
        int low = 10;
        int mid = 20;
        int high = 30;

        InspectableTree rightRight = new InspectableTree();
        rightRight.add(low);
        rightRight.add(mid);
        rightRight.add(high);
        equal(Set.of(low, mid), rightRight.lastRotatedValues());
        equal(mid, rightRight.rootNode().value());

        InspectableTree leftLeft = new InspectableTree();
        leftLeft.add(high);
        leftLeft.add(mid);
        leftLeft.add(low);
        equal(Set.of(high, mid), leftLeft.lastRotatedValues());
        equal(mid, leftLeft.rootNode().value());

        InspectableTree leftRight = new InspectableTree();
        leftRight.add(high);
        leftRight.add(low);
        leftRight.add(mid);
        equal(Set.of(low, mid, high), leftRight.lastRotatedValues());
        equal(mid, leftRight.rootNode().value());

        InspectableTree rightLeft = new InspectableTree();
        rightLeft.add(low);
        rightLeft.add(high);
        rightLeft.add(mid);
        equal(Set.of(low, mid, high), rightLeft.lastRotatedValues());
        equal(mid, rightLeft.rootNode().value());

        InspectableTree balanced = new InspectableTree();
        balanced.add(low);
        balanced.add(mid);
        require(balanced.lastRotatedValues().isEmpty(), "Balanced insert should not rotate");

        InspectableTree removal = new InspectableTree();
        for (int value : new int[]{mid, low, high, 5}) {
            removal.add(value);
        }
        removal.remove(high);
        equal(Set.of(low, mid), removal.lastRotatedValues());
        equal(low, removal.rootNode().value());
    }

    private static void verify(InspectableTree tree, TreeSet<Integer> expected) {
        equal(new ArrayList<>(expected), tree.inOrder());
        equal(expected.size(), tree.getNumNodes());
        checkNode(tree.rootNode(), null, null);
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

    private static ArrayList<Integer> randomValues(Random random, int count) {
        TreeSet<Integer> values = new TreeSet<>();
        while (values.size() < count) {
            values.add(random.nextInt(100_000));
        }
        return new ArrayList<>(values);
    }

    private static void equal(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("Expected " + expected + ", got " + actual);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
