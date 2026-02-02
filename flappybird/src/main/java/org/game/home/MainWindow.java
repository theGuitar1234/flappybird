package org.game.home;

import java.awt.Dimension;
import java.nio.file.Paths;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class MainWindow {
    
    private JFrame jframe;
    private ImageIcon imageIcon;
    
    public MainWindow() {
        jframe = setWindow(jframe);
        jframe = setIcon(jframe, imageIcon);
        displayWindow(jframe);
    }

    private static JFrame setWindow(JFrame jframe) {
        jframe = new JFrame("Flappy Bird");
        jframe.setSize(450, 800);
        jframe.setPreferredSize(new Dimension());
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setResizable(true);
        return jframe;
    }

    private static JFrame setIcon(JFrame jframe, ImageIcon imageIcon) {
        String iconPath = Paths.get(System.getProperty("user.dir"), "/resources/favicon.ico").toString();
        imageIcon = new ImageIcon();
        System.out.println(iconPath);
        jframe.setIconImage(imageIcon.getImage());
        return jframe;
    }

    private static void displayWindow(JFrame jframe) {
        jframe.setVisible(true);
    }
    
}
