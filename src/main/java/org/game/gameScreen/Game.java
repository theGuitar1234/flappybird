package org.game.gamescreen;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.game.util.GenerateRandom;
import org.game.util.constants.AppContext;

public class Game {

    private JPanel jpanel;
    private Timer animationTimer;

    public JPanel getJPanel() {
        return this.jpanel;
    }

    private RotatableLabel bird;
    private JLabel base;

    private Map<String, JLabel> assets;

    public Map<String, JLabel> getAssets() { return this.assets; }
    public void setAssets(Map<String, JLabel> assets) { this.assets = assets; }

    private static final int RAND_BIRD = GenerateRandom.generateRandom(0, 3);

    public void initGame(JFrame jframe) {

        jpanel = new JPanel();
        bird = new RotatableLabel();
        base = new JLabel();

        initPanel(jpanel);

        drawGame(jpanel, bird, base);
        flapAnimation(bird);

        JPanel root = (JPanel) jframe.getContentPane();
        root.removeAll();
        root.add(jpanel);
        root.revalidate();
        root.repaint();

        this.setAssets(Map.of(AppContext.BIRD_KEY, bird));
    }

    private void initPanel(JPanel jpanel) {
        jpanel.setSize(AppContext.WIDTH, AppContext.HEIGHT);
        jpanel.setPreferredSize(new Dimension(AppContext.WIDTH, AppContext.HEIGHT));
        jpanel.setVisible(true);
        jpanel.setOpaque(false);
        jpanel.setLayout(null);
    }

    private void drawGame(JPanel jpanel, RotatableLabel bird, JLabel base) {
        bird.setSize(bird.getPreferredSize());
        bird.setIcon(new ImageIcon(AppContext.BIRD_PATHS[RAND_BIRD][0]));

        Dimension b = bird.getPreferredSize();

        int xBird = b.width;
        int yBird = (jpanel.getHeight() - b.height) / 2;

        bird.setBounds(new Rectangle(xBird, yBird, b.width, b.height));

        base.setSize(base.getPreferredSize());
        base.setIcon(new ImageIcon(AppContext.BASE));

        Dimension ba = base.getPreferredSize();

        int xBase = 0;
        int yBase = jpanel.getHeight() - AppContext.BASE_HEIGHT;

        base.setBounds(new Rectangle(xBase, yBase, ba.width, ba.height));

        jpanel.add(base);
        jpanel.add(bird);
    }

    private void flapAnimation(JLabel bird) {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            int n = 0;
            @Override
            public void run() {
                if (n == AppContext.BIRD_PATHS[RAND_BIRD].length) n = 0;
                bird.setIcon(new ImageIcon(AppContext.BIRD_PATHS[RAND_BIRD][n]));
                n++;
            }
        };

        timer.schedule(timerTask, 0, AppContext.REFRESH);
    }
}
