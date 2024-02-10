import javax.swing.*;
import java.awt.*;

public class Frame extends JFrame {
    public Frame(String s){
        super(s);
        Panel panel = new Panel();
        add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int w= screenSize.width;
        int h= screenSize.height;
        if (w!=1920||h!=1080){
            setSize(w, w*9/16-20);
        }
        else{
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        setVisible(true);
    }
}
