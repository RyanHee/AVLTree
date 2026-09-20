package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.TreeSet;
import main.BinaryNode;
import main.BinarySearchTree;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Regression suite for the standalone (non-balancing) BinarySearchTree. */
public class TestBST {
    @Test
    @DisplayName("An empty tree reports empty defaults for every query")
    void emptyTreeDefaults() {
        Random random = new Random(1);
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        assertEquals(false, tree.contains(random.nextInt()));
        assertEquals(null, tree.remove(random.nextInt()));
        assertEquals(-1, tree.getHeight());
        assertEquals(0, tree.getNumLevels());
        assertEquals(0, tree.getNumNodes());
        assertEquals(0, tree.getNumLeaves());
        assertEquals(0, tree.getWidth());
        assertEquals(0, tree.getDiameter());
        assertEquals(true, tree.isFull());
        assertEquals(null, tree.getSmallest());
        assertEquals(null, tree.getLargest());
        assertEquals(new ArrayList<Integer>(), tree.preOrder());
        assertEquals(new ArrayList<Integer>(), tree.inOrder());
        assertEquals(new ArrayList<Integer>(), tree.postOrder());
        assertEquals(new ArrayList<Integer>(), tree.reverseOrder());
        assertEquals(new ArrayList<Integer>(), tree.levelOrder());
    }

    @Test
    @DisplayName("A known 7-node shape produces the expected traversals, stats, and removals")
    void structuralStatsTraversalsAndRemoval() {
        Random random = new Random(2);
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        // Random values, inserted by sorted rank to retain exact shape checks.
        ArrayList<Integer> values = randomValues(random, 7);
        ArrayList<Integer> insertionOrder = select(values, 3, 1, 5, 0, 2, 4, 6, 3);
        for (int value : insertionOrder) {
            tree.add(value);
        }
        assertEquals(7, tree.getNumNodes());
        assertEquals(4, tree.getNumLeaves());
        assertEquals(2, tree.getHeight());
        assertEquals(3, tree.getNumLevels());
        assertEquals(5, tree.getDiameter());
        assertEquals(4, tree.getWidth());
        assertEquals(true, tree.isFull());
        assertEquals(0, tree.getWidthAtLevel(-1));
        assertEquals(1, tree.getWidthAtLevel(0));
        assertEquals(4, tree.getWidthAtLevel(2));
        assertEquals(0, tree.getWidthAtLevel(Integer.MAX_VALUE));
        assertEquals(select(values, 3, 1, 0, 2, 5, 4, 6), tree.preOrder());
        assertEquals(values, tree.inOrder());
        assertEquals(select(values, 0, 2, 1, 4, 6, 5, 3), tree.postOrder());
        assertEquals(select(values, 6, 5, 4, 3, 2, 1, 0), tree.reverseOrder());
        assertEquals(select(values, 3, 1, 5, 0, 2, 4, 6), tree.levelOrder());
        for (int value : select(values, 3, 1, 5, 0, 2, 4, 6)) {
            BinaryNode<Integer> removed = tree.remove(value);
            assertEquals(value, removed.value());
            assertEquals(null, removed.left());
            assertEquals(null, removed.right());
            assertEquals(false, tree.contains(value));
        }
        assertEquals(0, tree.getNumNodes());
        assertEquals(0, tree.getDiameter());
    }

    @Test
    @DisplayName("Diameter is measured correctly when the longest path avoids the root")
    void diameterAcrossSkewedBranch() {
        Random random = new Random(3);
        BinarySearchTree<Integer> branched = new BinarySearchTree<>();
        ArrayList<Integer> branchValues = randomValues(random, 8);
        for (int value : select(branchValues, 7, 3, 2, 1, 0, 4, 5, 6)) {
            branched.add(value);
        }
        assertEquals(7, branched.getDiameter());
        assertEquals(false, branched.isFull());
    }

    @Test
    @DisplayName("Comparison-equal values are duplicates, and null is rejected everywhere")
    void duplicateAndNullHandling() {
        Random random = new Random(4);
        BinarySearchTree<BigDecimal> decimals = new BinarySearchTree<>();
        BigDecimal decimal = BigDecimal.valueOf(random.nextInt()).setScale(1);
        decimals.add(decimal);
        decimals.add(decimal.setScale(2));
        assertEquals(1, decimals.getNumNodes());
        assertEquals(true, decimals.contains(decimal.setScale(3)));
        assertEquals(decimal, decimals.remove(decimal.setScale(3)).value());
        assertThrows(NullPointerException.class, () -> decimals.add(null));
        assertThrows(NullPointerException.class, () -> decimals.contains(null));
        assertThrows(NullPointerException.class, () -> decimals.remove(null));
    }

    @Test
    @DisplayName("10,000 randomized mixed add/remove operations match TreeSet")
    void randomizedMixedOperations() {
        Random random = new Random(5);
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
                    assertEquals(existed, removed != null);
                    if (removed != null) {
                        assertEquals(value, removed.value());
                        assertEquals(null, removed.left());
                        assertEquals(null, removed.right());
                    }
                }
                assertEquals(expected.size(), actual.getNumNodes());
                assertEquals(expected.contains(value), actual.contains(value));
                assertEquals(expected.isEmpty() ? null : expected.first(), actual.getSmallest());
                assertEquals(expected.isEmpty() ? null : expected.last(), actual.getLargest());
                assertEquals(new ArrayList<>(expected), actual.inOrder());
                ArrayList<Integer> descending = new ArrayList<>(expected);
                Collections.reverse(descending);
                assertEquals(descending, actual.reverseOrder());
            }
        }
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
}
