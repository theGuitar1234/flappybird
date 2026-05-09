package org.game.gamescreen;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.game.util.AssetLoader;
import org.game.util.GenerateRandom;
import org.game.util.constants.AppContext;

public class Game {

    private Timer flapTimer;

    private JPanel jpanel;
    // private Timer animationTimer;

    public JPanel getJPanel() {
        return this.jpanel;
    }

    private RotatableSprite bird;
    private SpriteComponent base;

    private Map<String, SpriteComponent> assets;

    public Map<String, SpriteComponent> getAssets() { return this.assets; }
    public void setAssets(Map<String, SpriteComponent> assets) { this.assets = assets; }

    private static final int RAND_BIRD = GenerateRandom.generateRandom(0, 3);

    public void initGame(JFrame jframe) {

        jpanel = new JPanel();
        bird = new RotatableSprite();
        base = new SpriteComponent();

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

    private void drawGame(JPanel jpanel, RotatableSprite bird, SpriteComponent base) {
        // bird.setSize(bird.getPreferredSize());
        // ImageIcon birdIcon = AssetLoader.load(AppContext.BIRD_PATHS[RAND_BIRD][0]);
        BufferedImage birdIcon = AssetLoader.load(AppContext.BIRD_PATHS[RAND_BIRD][0]);
        // bird.setIcon(birdIcon);
        bird.setImage(birdIcon);

        // Dimension b = bird.getPreferredSize();
        Dimension b = new Dimension(birdIcon.getWidth(), birdIcon.getHeight());

        int xBird = b.width;
        int yBird = (jpanel.getHeight() - AppContext.BIRD_BOX_SIZE) / 2;

        bird.setBounds(new Rectangle(xBird, yBird, AppContext.BIRD_BOX_SIZE, AppContext.BIRD_BOX_SIZE));

        // base.setSize(base.getPreferredSize());
        // ImageIcon baseIcon = AssetLoader.load(AppContext.BASE);
        BufferedImage baseIcon = AssetLoader.load(AppContext.BASE);
        // base.setIcon(baseIcon);
        base.setImage(baseIcon);

        // Dimension ba = base.getPreferredSize();
        Dimension ba = new Dimension(baseIcon.getWidth(), baseIcon.getHeight());

        int xBase = 0;
        int yBase = jpanel.getHeight() - AppContext.BASE_HEIGHT;

        base.setBounds(new Rectangle(xBase, yBase, ba.width, ba.height));

        jpanel.add(base);
        jpanel.add(bird);
    }

    // private void flapAnimation(SpriteComponent bird) {
    //     Timer timer = new Timer();
    //     TimerTask timerTask = new TimerTask() {
    //         int n = 0;
    //         @Override
    //         public void run() {
    //             if (n == AppContext.BIRD_PATHS[RAND_BIRD].length) n = 0;
    //             // bird.setIcon(AssetLoader.load(AppContext.BIRD_PATHS[RAND_BIRD][n]));
    //             bird.setImage(AssetLoader.load(AppContext.BIRD_PATHS[RAND_BIRD][n]));
    //             n++;
    //         }
    //     };

    //     timer.schedule(timerTask, 0, AppContext.REFRESH);
    // }

    // private void flapAnimation(SpriteComponent bird) {
    //     if (flapTimer != null && flapTimer.isRunning()) {
    //         flapTimer.stop();
    //     }

    //     final int[] n = {0};

    //     flapTimer = new Timer(AppContext.REFRESH, e -> {
    //         if (n[0] == AppContext.BIRD_PATHS[RAND_BIRD].length) {
    //             n[0] = 0;
    //         }

    //         bird.setImage(AssetLoader.load(AppContext.BIRD_PATHS[RAND_BIRD][n[0]]));
    //         n[0]++;
    //     });

    //     flapTimer.start();
    // }

    private void flapAnimation(SpriteComponent bird) {
        if (flapTimer != null && flapTimer.isRunning()) {
            flapTimer.stop();
        }

        final int[] frame = {0};

        flapTimer = new Timer(AppContext.BIRD_ANIMATION_REFRESH, e -> {
            if (frame[0] == AppContext.BIRD_PATHS[RAND_BIRD].length) {
                frame[0] = 0;
            }

            bird.setImage(AssetLoader.load(AppContext.BIRD_PATHS[RAND_BIRD][frame[0]]));
            frame[0]++;
        });

        flapTimer.start();
    }

    public void stop() {
        if (flapTimer != null) {
            flapTimer.stop();
            flapTimer = null;
        }
    }
}
