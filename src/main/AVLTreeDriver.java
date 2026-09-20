package main;

import javax.swing.SwingUtilities;

/** Launches the interactive AVL tree display. */
public class AVLTreeDriver {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Frame("AVL Tree"));
    }
}
