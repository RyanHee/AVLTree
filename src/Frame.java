import java.awt.Dimension;
import javax.swing.JFrame;

/** Window for the interactive tree explorer. */
public class Frame extends JFrame {
    private static final long serialVersionUID = 1L;

    public Frame(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new Panel());
        setMinimumSize(new Dimension(900, 600));
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
