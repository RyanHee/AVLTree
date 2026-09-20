import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/** Run headlessly; optionally supply a PNG path for a rendered preview. */
public class TestTreeView {
    public static void main(String[] args) throws Exception {
        Random random = new Random(87321);
        AVLTree<Integer> tree = new AVLTree<>();
        verify(tree);
        for (int i = 0; i < 800; i++) {
            tree.add(random.nextInt());
            verify(tree);
        }
        for (int value : tree.inOrder()) {
            tree.remove(value);
            verify(tree);
        }
        SwingUtilities.invokeAndWait(() -> {
            Panel panel = new Panel();
            try {
                panel.setSize(1200, 800);
                layout(panel);
                JTextField input = find(panel, JTextField.class, null);
                input.setText("30  20\t10 40 50 25 5 15 35 45 60");
                find(panel, JButton.class, "Add").doClick();
                input.setText("70 invalid");
                find(panel, JButton.class, "Add").doClick();
                require(labelContains(panel, "11 nodes"), "Invalid input changed the tree");
                input.setText("25");
                find(panel, JButton.class, "Find").doClick();
                require(labelContains(panel, "Found 1 of 1"), "Find failed");
                input.setText("25");
                find(panel, JButton.class, "Remove").doClick();
                require(labelContains(panel, "10 nodes"), "Remove failed");
                input.setText("25");
                find(panel, JButton.class, "Add").doClick();
                find(panel, JButton.class, "Fit tree").doClick();
                JComponent canvas = (JComponent) panel.getComponent(1);
                canvas.dispatchEvent(new MouseWheelEvent(canvas, MouseEvent.MOUSE_WHEEL,
                        0, 0, 400, 300, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, -2));
                canvas.dispatchEvent(new MouseEvent(canvas, MouseEvent.MOUSE_PRESSED, 0,
                        0, 400, 300, 1, false));
                canvas.dispatchEvent(new MouseEvent(canvas, MouseEvent.MOUSE_DRAGGED, 0,
                        0, 440, 330, 0, false));
                canvas.dispatchEvent(new MouseEvent(canvas, MouseEvent.MOUSE_RELEASED, 0,
                        0, 440, 330, 1, false));
                find(panel, JButton.class, "Fit tree").doClick();
                // Finish animation before capturing a deterministic final layout.
                canvas.removeNotify();
                BufferedImage image = new BufferedImage(1200, 800, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = image.createGraphics();
                panel.paint(graphics);
                graphics.dispose();
                if (args.length > 0) {
                    ImageIO.write(image, "png", new File(args[0]));
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            } finally {
                panel.removeNotify();
            }
        });
        System.out.println("Tree layout, controls, zoom/pan, and headless rendering checks passed.");
    }

    private static void verify(AVLTree<Integer> tree) {
        TreeLayout layout = TreeLayout.create(tree.root());
        require(layout.positions.size() == tree.getNumNodes(), "Missing nodes");
        require(layout.edges.size() == Math.max(0, tree.getNumNodes() - 1), "Incorrect edges");
        Map<Double, List<Double>> levels = new HashMap<>();
        for (Point2D.Double point : layout.positions.values()) {
            levels.computeIfAbsent(point.y, key -> new ArrayList<>()).add(point.x);
        }
        for (List<Double> xs : levels.values()) {
            xs.sort(Comparator.naturalOrder());
            for (int i = 1; i < xs.size(); i++) {
                require(xs.get(i) - xs.get(i - 1) >= TreeLayout.GAP - 0.001, "Overlapping nodes");
            }
        }
        checkParents(tree.root(), layout);
    }

    private static void checkParents(BinaryNode<Integer> node, TreeLayout layout) {
        if (node == null) {
            return;
        }
        Point2D.Double parent = layout.positions.get(node.value());
        if (node.left() != null) {
            Point2D.Double left = layout.positions.get(node.left().value());
            require(left.x < parent.x && left.y == parent.y + TreeLayout.LEVEL, "Left placement");
        }
        if (node.right() != null) {
            Point2D.Double right = layout.positions.get(node.right().value());
            require(right.x > parent.x && right.y == parent.y + TreeLayout.LEVEL, "Right placement");
        }
        if (node.left() != null && node.right() != null) {
            double midpoint = (layout.positions.get(node.left().value()).x
                    + layout.positions.get(node.right().value()).x) / 2;
            require(Math.abs(midpoint - parent.x) < 0.001, "Parent not centered");
        }
        checkParents(node.left(), layout);
        checkParents(node.right(), layout);
    }

    private static void layout(Container container) {
        container.doLayout();
        for (Component component : container.getComponents()) {
            if (component instanceof Container) {
                layout((Container) component);
            }
        }
    }

    private static <T extends Component> T find(Container container, Class<T> type, String text) {
        for (Component component : container.getComponents()) {
            if (type.isInstance(component) && (text == null
                    || component instanceof JButton && text.equals(((JButton) component).getText()))) {
                return type.cast(component);
            }
            if (component instanceof Container) {
                T found = find((Container) component, type, text);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static boolean labelContains(Container container, String text) {
        for (Component component : container.getComponents()) {
            if (component instanceof JLabel && ((JLabel) component).getText().contains(text)) {
                return true;
            }
            if (component instanceof Container && labelContains((Container) component, text)) {
                return true;
            }
        }
        return false;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
