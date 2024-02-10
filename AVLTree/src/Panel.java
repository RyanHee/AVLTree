import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class Panel extends JPanel implements ActionListener {

    private AVLTree tree;
    private JTextField input;
    private String s, display;
    private BufferedImage img, top, bottom, node;
    private JButton addB, delB;
    public Panel(){
        tree = new AVLTree();
        input = new JTextField(4);
        addB = new JButton("add");
        delB = new JButton("delete");
        addB.addActionListener(this);
        delB.addActionListener(this);
        s="";
        display="AVL TREE";

        try{
            img = ImageIO.read(Panel.class.getResource("Image/TreeLines.png"));
            top = ImageIO.read(Panel.class.getResource("Image/TreeLineTop4.png"));
            bottom = ImageIO.read(Panel.class.getResource("Image/TreeLineBottom.png"));
            node = ImageIO.read(Panel.class.getResource("Image/node.png"));
        }
        catch (Exception e){
            System.out.println("error");
        }




        /*
        tree.add(10);
        tree.add(130);
        tree.add(170);
        tree.add(-1);
        tree.add(122);
        tree.add(-10);
        tree.add(-20);
        tree.add(4);
        tree.add(-15);
        tree.add(-30);
        tree.add(2);
        tree.add(1);
        tree.add(3);
        tree.add(-7);
        tree.add(-9);
        tree.add(-6);
        tree.add(6);
        tree.add(5);
        tree.add(7);
        tree.add(100);
        tree.add(110);
        tree.add(99);
        tree.add(128);
        tree.add(126);
        tree.add(129);
        tree.add(160);
        tree.add(150);
        tree.add(165);
        tree.add(180);
        tree.add(190);
        tree.add(178);
        */
    }


    public void paint(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        super.paint(g);
        setBackground(Color.BLACK);
        //g.drawImage(img, );
        g.drawImage(top, 255*w/3200, 510*h/1800, 2700*w/3200, 410*h/1800, null);
        g.drawImage(bottom, 130*w/3200, 960*h/1800, 2960*w/3200, 490*h/1800, null);
        g.setColor(new Color(0, 100, 40));
        int[]xlst=new int[63];
        int[]ylst=new int[63];
        int i1 = 3000;
        int i2 = 1600;
        xlst[0]=1570*w/3000;
        ylst[0]=20*h/1600;

        xlst[1]=840*w/ i1;

        ylst[1]=140*h/ i2;
        xlst[2]=2300*w/ i1;
        ylst[2]=140*h/ i2;
        xlst[3]=490*w/ i1;
        ylst[3]=270*h/ i2;
        xlst[4]=1190*w/ i1;
        ylst[4]=270*h/ i2;
        xlst[5]=1950*w/ i1;
        ylst[5]=270*h/ i2;
        xlst[6]=2650*w/ i1;
        ylst[6]=270*h/ i2;
        xlst[7]=310*w/ i1;
        ylst[7]=430*h/ i2;
        xlst[8]=670*w/ i1;
        ylst[8]=430*h/ i2;
        xlst[9]=1010*w/ i1;
        ylst[9]=430*h/ i2;
        xlst[10]=1370*w/ i1;
        ylst[10]=430*h/ i2;
        xlst[11]=1770*w/ i1;
        ylst[11]=430*h/ i2;
        xlst[12]=2130*w/ i1;
        ylst[12]=430*h/ i2;
        xlst[13]=2470*w/ i1;
        ylst[13]=430*h/ i2;
        xlst[14]=2830*w/ i1;
        ylst[14]=430*h/ i2;
        xlst[15]=230*w/ i1;
        ylst[15]=690*h/ i2;
        xlst[16]=390*w/ i1;
        ylst[16]=690*h/ i2;
        xlst[17]=590*w/ i1;
        ylst[17]=690*h/ i2;
        xlst[18]=750*w/ i1;
        ylst[18]=690*h/ i2;
        xlst[19]=930*w/ i1;
        ylst[19]=690*h/ i2;
        xlst[20]=1090*w/ i1;
        ylst[20]=690*h/ i2;
        xlst[21]=1290*w/ i1;
        ylst[21]=690*h/ i2;
        xlst[22]=1450*w/ i1;
        ylst[22]=690*h/ i2;
        xlst[23]=1690*w/ i1;
        ylst[23]=690*h/ i2;
        xlst[24]=1850*w/ i1;
        ylst[24]=690*h/ i2;
        xlst[25]=2050*w/ i1;
        ylst[25]=690*h/ i2;
        xlst[26]=2210*w/ i1;
        ylst[26]=690*h/ i2;
        xlst[27]=2390*w/ i1;
        ylst[27]=690*h/ i2;
        xlst[28]=2550*w/ i1;
        ylst[28]=690*h/ i2;
        xlst[29]=2750*w/ i1;
        ylst[29]=690*h/ i2;
        xlst[30]=2910*w/ i1;
        ylst[30]=690*h/ i2;
        xlst[31]=190*w/ i1;
        ylst[31]=900*h/ i2;
        xlst[32]=270*w/ i1;
        ylst[32]=900*h/ i2;
        xlst[33]=350*w/ i1;
        ylst[33]=900*h/ i2;
        xlst[34]=430*w/ i1;
        ylst[34]=900*h/ i2;
        xlst[35]=550*w/ i1;
        ylst[35]=900*h/ i2;
        xlst[36]=630*w/ i1;
        ylst[36]=900*h/ i2;
        xlst[37]=710*w/ i1;
        ylst[37]=900*h/ i2;
        xlst[38]=790*w/ i1;
        ylst[38]=900*h/ i2;
        xlst[39]=890*w/ i1;
        ylst[39]=900*h/ i2;
        xlst[40]=970*w/ i1;
        ylst[40]=900*h/ i2;
        xlst[41]=1050*w/ i1;
        ylst[41]=900*h/ i2;
        xlst[42]=1130*w/ i1;
        ylst[42]=900*h/ i2;
        xlst[43]=1250*w/ i1;
        ylst[43]=900*h/ i2;
        xlst[44]=1330*w/ i1;
        ylst[44]=900*h/ i2;
        xlst[45]=1410*w/ i1;
        ylst[45]=900*h/ i2;
        xlst[46]=1490*w/ i1;
        ylst[46]=900*h/ i2;
        xlst[47]=1650*w/ i1;
        ylst[47]=900*h/ i2;
        xlst[48]=1730*w/ i1;
        ylst[48]=900*h/ i2;
        xlst[49]=1810*w/ i1;
        ylst[49]=900*h/ i2;
        xlst[50]=1890*w/ i1;
        ylst[50]=900*h/ i2;
        xlst[51]=2010*w/ i1;
        ylst[51]=900*h/ i2;
        xlst[52]=2090*w/ i1;
        ylst[52]=900*h/ i2;
        xlst[53]=2170*w/ i1;
        ylst[53]=900*h/ i2;
        xlst[54]=2250*w/ i1;
        ylst[54]=900*h/ i2;
        xlst[55]=2350*w/ i1;
        ylst[55]=900*h/ i2;
        xlst[56]=2430*w/ i1;
        ylst[56]=900*h/ i2;
        xlst[57]=2510*w/ i1;
        ylst[57]=900*h/ i2;
        xlst[58]=2590*w/ i1;
        ylst[58]=900*h/ i2;
        xlst[59]=2710*w/ i1;
        ylst[59]=900*h/ i2;
        xlst[60]=2790*w/ i1;
        ylst[60]=900*h/ i2;
        xlst[61]=2870*w/ i1;
        ylst[61]=900*h/ i2;
        xlst[62]=2950*w/ i1;
        ylst[62]=900*h/ i2;

        g.setFont(new Font("Sans",Font.BOLD,40));
        FontMetrics fontMetrics=g.getFontMetrics();
        int width = fontMetrics.stringWidth(display);
        g.drawString(display,w/2-width*w/4800,50);
        g.setFont(new Font("Sans",Font.BOLD,10));

        String[] lst= tree.forDraw();
        for (int i=0;i<63;i++){
            g.drawOval(xlst[i]-50*w/1600, 200*w/1600+ylst[i], 30*w/1600, 27*h/900);
            fontMetrics=g.getFontMetrics();
            width = fontMetrics.stringWidth(lst[i]);
            g.drawString(lst[i],xlst[i]-135*w/6400-width*w/3200,215*w/1600+ylst[i]);
        }




        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));



        add(addB);
        add(delB);
        add(input);
        input.setBounds(0*w/3200,1700*h/1800,200*w/3200,100*h/1800);
        addB.setBounds(200*w/3200,1700*h/1800,200*w/3200,100*h/1800);
        delB.setBounds(400*w/3200,1700*h/1800,200*w/3200,100*h/1800);



        /*

        //g.drawString(s, 800, 300);


        g.drawLine(824,305,600,370);
        g.drawLine(880,305,1115,370);
        g.drawLine(570,395,470,461);
        g.drawLine(622,395,732,461);
        g.drawLine(1090,395,1000,461);
        g.drawLine(1145,395,1257,461);
        g.drawLine(445,490,410,550);
        g.drawLine(493,490,535,550);
        g.drawLine(707,490,667,550);
        g.drawLine(760,490,795,550);
        g.drawLine(1022,490,1060,550);
        g.drawLine(968,490,935,550);
        g.drawLine(1235,490,1200,550);
        g.drawLine(1285,490,1325,550);
        g.drawLine(390,580,370,640);
        g.drawLine(430,580,445,640);
        g.drawLine(555,580,575,640);
        g.drawLine(520,580,500,640);
        g.drawLine(648,580,628,640);
        g.drawLine(688,580,708,640);
        g.drawLine(778,580,758,640);
        g.drawLine(820,580,840,640);
        g.drawLine(915,580,895,640);
        g.drawLine(955,580,975,640);
        g.drawLine(1080,580,1100,640);
        g.drawLine(1045,580,1025,640);
        g.drawLine(1175,580,1160,640);
        g.drawLine(1213,580,1233,640);
        g.drawLine(1313,580,1293,640);
        g.drawLine(1345,580,1365,640);
        g.setColor(new Color(0,100,0));

        g.setFont(new Font("Sans",Font.BOLD,20));

        int[]xlst=new int[5];
        xlst[0]=90;
        xlst[1]=42;
        xlst[2]=18;
        xlst[3]=7;
        xlst[4]=0;
        int[]spaces=new int[5];
        spaces[0]=0;
        spaces[1]=89;
        spaces[2]=41;
        spaces[3]=16;
        spaces[4]=4;
        int y=300;
        int i=0;
        for (String s:lst){
            String[]arr=s.split(" ");
            int x=xlst[i];
            String put ="";
            for (int j=0;j<x;j++){
                put+=" ";
            }
            int t=0;
            for (String s1:arr){
                put+=s1;
                for (int j=0;j<spaces[i];j++){
                    put+=" ";
                }
                if (t==6&&i==3){
                    put+=" ";
                    //put+=" ";
                    //put+=" ";
                }
                if (t==3&&i==3){
                    put+=" ";
                    //put+=" ";
                }
                if (t==4&&i==3){
                    //put+=" ";
                }
                if (t==1&&i==3){
                    //put=put.substring(0,put.length()-1);
                }
                if (t==0&&i==3){
                    put=put.substring(0,put.length()-1);
                }
                if (t==3&&i==4){
                    put=put.substring(0,put.length()-1);
                }

                if (t==9&&i==4){
                    put=put.substring(0,put.length()-1);
                }
                t++;
            }
            if (i==4){

            }

            i++;
            y+=90;
        }
        */
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==addB){
            s=input.getText();

            if (tree.contains(Integer.parseInt(s))){
                display=s+" is already in there!";
            }
            else{
                tree.add(new AVLNode(Integer.parseInt(s)));
                display=s+" added!";
                if (tree.getLevel(Integer.parseInt(s))>4){
                    display=s+" exceeds level limits!";
                    tree.remove(Integer.parseInt(s));
                }
                if (s.length()>5){
                    display=s+" is too long!";
                    tree.remove(Integer.parseInt(s));
                }
            }

            input.setText("");
            repaint();
        }
        if (e.getSource()==delB){
            s=input.getText();
            BinaryNode b = tree.remove(Integer.parseInt(s));
            input.setText("");
            display=s+" removed!";
            if (b==null){
                display=s+" don't exist!";
            }
            repaint();
        }
    }
}
