package org.game.engine;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import org.game.engine.sound.Sound;

public class Engine {
    
    public void run(JFrame jframe) {

        Sound.init();

        jframe.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Sound.flap();
                
                
                //Sound.close();
            }
        });
    }
}
