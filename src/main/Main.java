package main;

/** Demonstrates AVLTree usage: insertion with rotations, traversals, and removal. */
public class Main {
    public static void main(String[] args) {
        AVLTree<Integer> tree = new AVLTree<>();

        System.out.println("Adding 30, 20, 10 (10 triggers a right rotation):");
        for (int value : new int[]{30, 20, 10}) {
            tree.add(value);
        }
        System.out.println("Level order: " + tree.levelOrder());
        System.out.println("Height:      " + tree.getHeight());

        System.out.println();
        System.out.println("Adding 40, 50, 60, 25, 5 (more rotations along the way):");
        for (int value : new int[]{40, 50, 60, 25, 5}) {
            tree.add(value);
        }
        System.out.println("Level order: " + tree.levelOrder());
        System.out.println("In order:    " + tree.inOrder());
        System.out.println("Nodes:       " + tree.getNumNodes());
        System.out.println("Leaves:      " + tree.getNumLeaves());
        System.out.println("Height:      " + tree.getHeight());

        System.out.println();
        System.out.println("Removing 20:");
        tree.remove(20);
        System.out.println("In order:    " + tree.inOrder());
        System.out.println("Height:      " + tree.getHeight());

        System.out.println();
        System.out.println("Contains 50? " + tree.contains(50));
        System.out.println("Contains 20? " + tree.contains(20));
        System.out.println("Smallest / largest: " + tree.getSmallest() + " / " + tree.getLargest());
    }
}
