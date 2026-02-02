package org.game.home;

import java.awt.Dimension;

import javax.swing.JFrame;

public class MainWindow {
    
    private JFrame jframe;
    
    public MainWindow() {
        jframe = new JFrame("Flappy Bird");
        jframe.setVisible(true);
        jframe.setSize(450, 800);
        jframe.setPreferredSize(new Dimension());
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setResizable(true);
    }
}
