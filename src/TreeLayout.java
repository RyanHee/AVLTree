import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Compact subtree layout: parents are centered and same-depth contours cannot overlap. */
final class TreeLayout {
    static final double DIAMETER = 96;
    static final double GAP = 132;
    static final double LEVEL = 136;
    final Map<Integer, Point2D.Double> positions = new LinkedHashMap<>();
    final List<int[]> edges = new ArrayList<>();
    final Map<Integer, Integer> balances = new LinkedHashMap<>();
    double minX;
    double maxX;
    double maxY;

    static TreeLayout create(BinaryNode<Integer> root) {
        TreeLayout layout = new TreeLayout();
        Branch branch = build(root);
        if (branch != null) {
            layout.place(branch, 0, 0);
        }
        return layout;
    }

    private static Branch build(BinaryNode<Integer> node) {
        if (node == null) {
            return null;
        }
        Branch branch = new Branch(node);
        branch.left = build(node.left());
        branch.right = build(node.right());
        if (branch.left != null && branch.right != null) {
            double separation = GAP;
            int shared = Math.min(branch.left.rightContour.size(), branch.right.leftContour.size());
            for (int i = 0; i < shared; i++) {
                separation = Math.max(separation,
                        branch.left.rightContour.get(i) - branch.right.leftContour.get(i) + GAP);
            }
            branch.leftOffset = -separation / 2;
            branch.rightOffset = separation / 2;
        } else {
            branch.leftOffset = -GAP / 2;
            branch.rightOffset = GAP / 2;
        }
        branch.leftContour.add(0.0);
        branch.rightContour.add(0.0);
        int depths = Math.max(branch.left == null ? 0 : branch.left.leftContour.size(),
                branch.right == null ? 0 : branch.right.leftContour.size());
        for (int i = 0; i < depths; i++) {
            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;
            if (branch.left != null && i < branch.left.leftContour.size()) {
                min = branch.left.leftContour.get(i) + branch.leftOffset;
                max = branch.left.rightContour.get(i) + branch.leftOffset;
            }
            if (branch.right != null && i < branch.right.leftContour.size()) {
                min = Math.min(min, branch.right.leftContour.get(i) + branch.rightOffset);
                max = Math.max(max, branch.right.rightContour.get(i) + branch.rightOffset);
            }
            branch.leftContour.add(min);
            branch.rightContour.add(max);
        }
        return branch;
    }

    private void place(Branch branch, double x, double y) {
        int value = branch.node.value();
        positions.put(value, new Point2D.Double(x, y));
        balances.put(value, ((AVLNode<Integer>) branch.node).getBalanceFactor());
        minX = Math.min(minX, x);
        maxX = Math.max(maxX, x);
        maxY = Math.max(maxY, y);
        if (branch.left != null) {
            edges.add(new int[]{value, branch.left.node.value()});
            place(branch.left, x + branch.leftOffset, y + LEVEL);
        }
        if (branch.right != null) {
            edges.add(new int[]{value, branch.right.node.value()});
            place(branch.right, x + branch.rightOffset, y + LEVEL);
        }
    }

    private static final class Branch {
        final BinaryNode<Integer> node;
        final List<Double> leftContour = new ArrayList<>();
        final List<Double> rightContour = new ArrayList<>();
        Branch left;
        Branch right;
        double leftOffset;
        double rightOffset;

        Branch(BinaryNode<Integer> node) {
            this.node = node;
        }
    }
}
