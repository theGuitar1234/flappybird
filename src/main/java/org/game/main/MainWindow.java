package org.game.main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.game.engine.Engine;
import org.game.gamescreen.Game;
import org.game.gamescreen.SpriteComponent;
import org.game.home.WelcomeScreen;
import org.game.util.AssetLoader;
import org.game.util.GenerateRandom;
import org.game.util.constants.AppContext;

public class MainWindow {

    private static volatile boolean isRunning = false;

    private WelcomeScreen welcomeScreen;

    private JFrame jframe;
    private JPanel jpanel;
    private Game game;

    // private static final ImageIcon imageIcon = AssetLoader.load(AppContext.FAVICON);
    
    public MainWindow() {

        jframe = new JFrame(AppContext.TITLE);
        setWindow(jframe);
        setIcon(jframe);
        initPanel(jframe);
        displayWindow(jframe);
        welcomeScreen = new WelcomeScreen(jframe);
        
        if (!isRunning) {
            jframe.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    isRunning = true;

                    if (welcomeScreen != null) {
                        welcomeScreen.stop();
                        welcomeScreen = null;
                    }

                    game = new Game();
                    game.initGame(jframe);

                    jpanel = game.getJPanel();
                    Map<String, SpriteComponent> assets = game.getAssets();
                    new Engine(() -> {
                        if (game != null) {
                            game.stop();
                        }
                    }).run(jpanel, jframe, assets);

                    jframe.removeMouseListener(this);
                }
            });
        }
    }

    private static void setWindow(JFrame jframe) {
        jframe.setSize(AppContext.WIDTH, AppContext.HEIGHT);
        jframe.setPreferredSize(new Dimension(AppContext.WIDTH, AppContext.HEIGHT));
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setResizable(false);
    }

    private static void setIcon(JFrame jframe) {
        // jframe.setIconImage(imageIcon.getImage());
        jframe.setIconImage(AssetLoader.load(AppContext.FAVICON));
    }

    private static void displayWindow(JFrame jframe) {
        jframe.setVisible(true);
    }

    private JPanel drawBackground() {
        jpanel = new JPanel() {
            int rand = GenerateRandom.generateRandom(0, 2);
            Image bg = AssetLoader.load(AppContext.BACKGROUNDS[rand]);

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };

        return jpanel;
    }

    private void initPanel(JFrame jframe) {
        jpanel = drawBackground();
        jframe.setContentPane(jpanel);
    }
}
