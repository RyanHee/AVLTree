package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.TreeSet;
import main.AVLNode;
import main.AVLTree;
import main.BinaryNode;
import main.BinarySearchTree;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Seeded randomized checks of AVL ordering, balance, and cached heights. */
public class TestAVL {
    private static class InspectableTree extends AVLTree<Integer> {
        BinaryNode<Integer> rootNode() {
            return root();
        }
    }

    @Test
    @DisplayName("All four insertion rotation cases rebalance and stay consistent through removal")
    void fourRotationCases() {
        Random random = new Random(1);
        ArrayList<Integer> values = randomValues(random, 3);
        for (int[] order : new int[][]{{2, 1, 0}, {0, 1, 2}, {2, 0, 1}, {0, 2, 1}}) {
            InspectableTree tree = new InspectableTree();
            TreeSet<Integer> expected = new TreeSet<>();
            for (int rank : order) {
                tree.add(values.get(rank));
                expected.add(values.get(rank));
                verify(tree, expected);
            }
            assertEquals(values.get(1), tree.rootNode().value());
            while (!expected.isEmpty()) {
                removeAndCheck(tree, expected, tree.rootNode().value());
            }
            removeAndCheck(tree, expected, random.nextInt());
        }
    }

    @Test
    @DisplayName("10,000 randomized mixed add/remove operations stay ordered and balanced")
    void randomizedMixedOperations() {
        Random random = new Random(2);
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
    }

    @Test
    @DisplayName("Sorted ascending and descending insertion/deletion exercise repeated rebalancing")
    void sortedInsertionAndDeletion() {
        Random random = new Random(3);
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
    }

    @Test
    @DisplayName("Comparison-equal values are duplicates, and null is rejected everywhere")
    void duplicateAndNullHandling() {
        Random random = new Random(4);
        AVLTree<BigDecimal> decimals = new AVLTree<>();
        BigDecimal value = BigDecimal.valueOf(random.nextInt()).setScale(1);
        decimals.add(value);
        decimals.add(value.setScale(2));
        assertEquals(1, decimals.getNumNodes());
        assertEquals(true, decimals.contains(value.setScale(3)));
        assertEquals(value, decimals.remove(value.setScale(3)).value());
        assertThrows(NullPointerException.class, () -> decimals.add(null));
        assertThrows(NullPointerException.class, () -> decimals.remove(null));
        assertThrows(NullPointerException.class, () -> decimals.contains(null));
    }

    private static void removeAndCheck(InspectableTree tree, TreeSet<Integer> expected, int value) {
        boolean existed = expected.remove(value);
        BinaryNode<Integer> removed = tree.remove(value);
        assertEquals(existed, removed != null);
        if (removed != null) {
            assertEquals(value, removed.value());
            assertEquals(null, removed.left());
            assertEquals(null, removed.right());
            assertEquals(0, ((AVLNode<Integer>) removed).getHeight());
        }
        verify(tree, expected);
    }

    private static void verify(InspectableTree tree, TreeSet<Integer> expected) {
        assertEquals(new ArrayList<>(expected), tree.inOrder());
        assertEquals(expected.size(), tree.getNumNodes());
        assertEquals(expected.isEmpty() ? null : expected.first(), tree.getSmallest());
        assertEquals(expected.isEmpty() ? null : expected.last(), tree.getLargest());
        assertEquals(checkNode(tree.rootNode(), null, null), tree.getHeight());
        assertEquals(expected.size(), tree.levelOrder().size());
        assertEquals(expected, new TreeSet<>(tree.levelOrder()));
        String[] labels = tree.forDraw();
        assertEquals(63, labels.length);
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
        assertEquals(height, ((AVLNode<Integer>) node).getHeight());
        assertEquals(left - right, ((AVLNode<Integer>) node).getBalanceFactor());
        return height;
    }

    private static void checkLabels(BinaryNode<Integer> node, String[] labels, int index) {
        if (index >= labels.length) {
            return;
        }
        assertEquals(node == null ? " " : node.value().toString(), labels[index]);
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
}
