package main;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Interactive AVL explorer with compact layout, animated updates, and camera controls. */
public class Panel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Color BACKGROUND = new Color(15, 23, 38);
    private static final Color TEXT = new Color(227, 235, 247);
    private final VisualAVLTree<Integer> tree = new VisualAVLTree<>();
    private final JTextField input = new JTextField(22);
    private final JLabel status = new JLabel("Add some integers to grow your tree.");
    private final JLabel stats = new JLabel();
    private final TreeCanvas canvas = new TreeCanvas();

    public Panel() {
        super(new BorderLayout());
        setBackground(BACKGROUND);
        JPanel header = new JPanel(new BorderLayout(0, 12));
        header.setBackground(BACKGROUND);
        header.setBorder(new EmptyBorder(22, 24, 18, 24));
        JLabel title = new JLabel("AVL / Tree explorer");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        title.setForeground(TEXT);
        header.add(title, BorderLayout.NORTH);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controls.setOpaque(false);
        input.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        input.setBackground(new Color(32, 48, 71));
        input.setForeground(TEXT);
        input.setCaretColor(TEXT);
        input.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new Color(75, 98, 130)),
                new EmptyBorder(9, 12, 9, 12)));
        input.setToolTipText("Integers separated by spaces, such as 30 20 10");
        input.getAccessibleContext().setAccessibleName("Tree values");
        controls.add(input);
        addButton(controls, "Add", () -> apply("Add"));
        addButton(controls, "Remove", () -> apply("Remove"));
        addButton(controls, "Find", () -> apply("Find"));
        addButton(controls, "Fit tree", canvas::fit);
        JLabel speedLabel = new JLabel("Speed");
        speedLabel.setForeground(new Color(147, 168, 194));
        speedLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        controls.add(speedLabel);
        JSlider speed = new JSlider(150, 2000, 700);
        speed.setOpaque(false);
        speed.setPreferredSize(new Dimension(120, speed.getPreferredSize().height));
        speed.setToolTipText("Animation duration, from fast to slow");
        speed.addChangeListener(event -> canvas.setAnimationDurationMillis(speed.getValue()));
        controls.add(speed);
        header.add(controls, BorderLayout.CENTER);
        stats.setForeground(new Color(147, 168, 194));
        header.add(stats, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BACKGROUND);
        footer.setBorder(new EmptyBorder(14, 24, 18, 24));
        status.setForeground(TEXT);
        JLabel help = new JLabel("Scroll to zoom · Drag to pan · Enter to add");
        help.setForeground(new Color(147, 168, 194));
        footer.add(status, BorderLayout.CENTER);
        footer.add(help, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
        input.addActionListener(event -> apply("Add"));
        refreshStats();
    }

    private void addButton(JPanel controls, String title, Runnable action) {
        JButton button = new JButton(title);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setBackground(title.equals("Add") ? new Color(94, 234, 195) : new Color(39, 56, 80));
        button.setForeground(title.equals("Add") ? BACKGROUND : TEXT);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setBorder(new EmptyBorder(11, 16, 11, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(event -> action.run());
        controls.add(button);
    }

    private void apply(String operation) {
        String text = input.getText().trim();
        ArrayList<Integer> values = new ArrayList<>();
        try {
            if (text.isEmpty()) {
                throw new NumberFormatException();
            }
            for (String token : text.split("\\s+")) {
                values.add(Integer.parseInt(token));
            }
        } catch (NumberFormatException exception) {
            status.setText("Enter integers separated by spaces. Nothing was changed.");
            return;
        }
        Set<Integer> highlights = new HashSet<>();
        Set<Integer> rotated = new HashSet<>();
        int affected = 0;
        for (int value : values) {
            if (operation.equals("Add")) {
                if (!tree.contains(value)) {
                    tree.add(value);
                    rotated.addAll(tree.lastRotatedValues());
                    affected++;
                }
                highlights.add(value);
            } else if (operation.equals("Remove")) {
                if (tree.remove(value) != null) {
                    rotated.addAll(tree.lastRotatedValues());
                    affected++;
                }
            } else if (tree.contains(value)) {
                highlights.add(value);
                affected++;
            }
        }
        canvas.update(TreeLayout.create(tree.root()), highlights, rotated);
        refreshStats();
        status.setText(operation.equals("Find") ? "Found " + affected + " of " + values.size() + " requested values."
                : operation.equals("Add") ? "Added " + affected + " new values."
                : "Removed " + affected + " values.");
        input.selectAll();
        input.requestFocusInWindow();
    }

    private void refreshStats() {
        stats.setText(tree.getNumNodes() + " nodes    /    Height " + tree.getHeight()
                + "    /    " + tree.getNumLeaves() + " leaves    /    Node labels show balance factors");
    }

    private static final class TreeCanvas extends JPanel {
        private static final long serialVersionUID = 1L;
        private TreeLayout layout = TreeLayout.create(null);
        private Map<Integer, Point2D.Double> from = new HashMap<>();
        private Set<Integer> highlights = new HashSet<>();
        private Set<Integer> rotating = new HashSet<>();
        private final Timer animation;
        private long started;
        private double progress = 1;
        private double zoom = 1;
        private double panX;
        private double panY;
        private boolean autoFit = true;
        private Point drag;
        private double animationDurationNanos = 700_000_000.0;

        TreeCanvas() {
            setBackground(new Color(19, 29, 46));
            animation = new Timer(16, event -> {
                progress = Math.min(1, (System.nanoTime() - started) / animationDurationNanos);
                if (progress == 1) {
                    ((Timer) event.getSource()).stop();
                }
                repaint();
            });
            MouseAdapter mouse = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent event) {
                    drag = event.getPoint();
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }

                @Override
                public void mouseDragged(MouseEvent event) {
                    autoFit = false;
                    panX += event.getX() - drag.x;
                    panY += event.getY() - drag.y;
                    drag = event.getPoint();
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent event) {
                    drag = null;
                    setCursor(Cursor.getDefaultCursor());
                }

                @Override
                public void mouseWheelMoved(MouseWheelEvent event) {
                    autoFit = false;
                    double next = Math.max(0.02, Math.min(3, zoom * Math.pow(1.12, -event.getPreciseWheelRotation())));
                    double ratio = next / zoom;
                    panX = event.getX() - (event.getX() - panX) * ratio;
                    panY = event.getY() - (event.getY() - panY) * ratio;
                    zoom = next;
                    repaint();
                }
            };
            addMouseListener(mouse);
            addMouseMotionListener(mouse);
            addMouseWheelListener(mouse);
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent event) {
                    if (autoFit) {
                        fit();
                    }
                }
            });
        }

        void update(TreeLayout next, Set<Integer> marked, Set<Integer> rotated) {
            Map<Integer, Point2D.Double> current = positions();
            from = new HashMap<>();
            Map<Integer, Integer> parents = new HashMap<>();
            for (int[] edge : next.edges) {
                parents.put(edge[1], edge[0]);
            }
            for (Map.Entry<Integer, Point2D.Double> entry : next.positions.entrySet()) {
                Point2D.Double origin = current.get(entry.getKey());
                if (origin == null) {
                    origin = current.getOrDefault(parents.get(entry.getKey()), entry.getValue());
                }
                from.put(entry.getKey(), origin);
            }
            layout = next;
            highlights = new HashSet<>(marked);
            rotating = new HashSet<>(rotated);
            progress = 0;
            started = System.nanoTime();
            if (autoFit) {
                fit();
            }
            animation.start();
            repaint();
        }

        void setAnimationDurationMillis(int millis) {
            animationDurationNanos = millis * 1_000_000.0;
        }

        void fit() {
            autoFit = true;
            double width = layout.maxX - layout.minX + TreeLayout.DIAMETER + 100;
            double height = layout.maxY + TreeLayout.DIAMETER + 100;
            zoom = Math.min(1, Math.min(Math.max(1, getWidth()) / width, Math.max(1, getHeight()) / height));
            panX = getWidth() / 2.0 - zoom * (layout.minX + layout.maxX) / 2;
            panY = (getHeight() - layout.maxY * zoom) / 2;
            repaint();
        }

        private Map<Integer, Point2D.Double> positions() {
            Map<Integer, Point2D.Double> result = new HashMap<>();
            double t = progress * progress * (3 - 2 * progress);
            for (Map.Entry<Integer, Point2D.Double> entry : layout.positions.entrySet()) {
                Point2D.Double end = entry.getValue();
                Point2D.Double start = from.getOrDefault(entry.getKey(), end);
                double x = start.x + (end.x - start.x) * t;
                double y = start.y + (end.y - start.y) * t;
                if (rotating.contains(entry.getKey())) {
                    double dx = end.x - start.x;
                    double dy = end.y - start.y;
                    double length = Math.hypot(dx, dy);
                    if (length > 1) {
                        double swing = Math.sin(t * Math.PI) * Math.min(70, 24 + length * 0.3);
                        double perpX = -dy / length;
                        double perpY = dx / length;
                        if (perpY > 0) {
                            perpX = -perpX;
                            perpY = -perpY;
                        }
                        x += perpX * swing;
                        y += perpY * swing;
                    }
                }
                result.put(entry.getKey(), new Point2D.Double(x, y));
            }
            return result;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (layout.positions.isEmpty()) {
                g.setColor(TEXT);
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
                centered(g, "A little input. A balanced tree.", getWidth() / 2.0, getHeight() / 2.0);
                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
                g.setColor(new Color(147, 168, 194));
                centered(g, "Try adding 30 20 10 to see your first rotation.", getWidth() / 2.0, getHeight() / 2.0 + 34);
            } else {
                g.translate(panX, panY);
                g.scale(zoom, zoom);
                Map<Integer, Point2D.Double> points = positions();
                g.setStroke(new BasicStroke(2));
                g.setColor(new Color(93, 116, 146));
                for (int[] edge : layout.edges) {
                    g.draw(new Line2D.Double(points.get(edge[0]), points.get(edge[1])));
                }
                for (Map.Entry<Integer, Point2D.Double> entry : points.entrySet()) {
                    Point2D.Double point = entry.getValue();
                    boolean marked = highlights.contains(entry.getKey());
                    boolean rotatingNow = progress < 1 && rotating.contains(entry.getKey());
                    double radius = TreeLayout.DIAMETER / 2;
                    Ellipse2D circle = new Ellipse2D.Double(point.x - radius, point.y - radius, radius * 2, radius * 2);
                    g.setColor(rotatingNow ? new Color(84, 58, 21) : marked ? new Color(26, 80, 77) : new Color(32, 48, 71));
                    g.fill(circle);
                    g.setStroke(new BasicStroke(rotatingNow ? 3 : 2));
                    g.setColor(rotatingNow ? new Color(247, 181, 89) : marked ? new Color(94, 234, 195) : new Color(98, 133, 179));
                    g.draw(circle);
                    String value = entry.getKey().toString();
                    int fontSize = 18;
                    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
                    while (g.getFontMetrics().stringWidth(value) > TreeLayout.DIAMETER - 12 && fontSize > 9) {
                        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, --fontSize));
                    }
                    g.setColor(TEXT);
                    centered(g, value, point.x, point.y - 1);
                    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
                    g.setColor(new Color(153, 177, 203));
                    centered(g, "bf " + layout.balances.get(entry.getKey()), point.x, point.y + 21);
                }
            }
            g.dispose();
        }

        private void centered(Graphics2D g, String text, double x, double y) {
            g.drawString(text, (float) (x - g.getFontMetrics().stringWidth(text) / 2.0), (float) y);
        }

        @Override
        public void removeNotify() {
            animation.stop();
            progress = 1;
            super.removeNotify();
        }
    }
}
