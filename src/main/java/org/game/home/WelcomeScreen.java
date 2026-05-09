package org.game.home;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.game.gamescreen.SpriteComponent;
import org.game.util.AssetLoader;
import org.game.util.constants.AppContext;

public class WelcomeScreen {

    private JPanel jpanel;

    private Timer welcomeTimer;

    // private JLabel bird;
    // private JLabel base;
    // private JLabel startGame;

    private SpriteComponent bird;
    private SpriteComponent base;
    private SpriteComponent startGame;

    private double startGamePhase = 0.0;
    private int startGameBaseY;
    private int welcomeBirdFrameTick = 0;

    private int welcomeBirdFrame = 0;
    private int welcomeDirection = 1;

    public WelcomeScreen(JFrame jframe) {

        jpanel = new JPanel();
        bird = new SpriteComponent();
        base = new SpriteComponent();
        startGame = new SpriteComponent();

        initPanel(jpanel);

        drawWelcomeScreen(jpanel, bird, base, startGame);
        welcomeAnimation(bird, startGame);

        JPanel root = (JPanel) jframe.getContentPane();
        root.add(jpanel);
        root.revalidate();
        root.repaint();
    }

    // private void welcomeAnimation(SpriteComponent bird, SpriteComponent startGame) {
    //     Timer timer = new Timer();
    //     TimerTask timerTask = new TimerTask() {

    //         int n = 0;
    //         int indecrement = 1;
    //         int min = AppContext.HEIGHT / 8;
    //         int max = startGame.getY();
            
    //         @Override
    //         public void run() {
    //             if (n == AppContext.BIRD_PATHS[AppContext.RAND_BIRD].length) n = 0;
    //             // bird.setIcon(AssetLoader.load(AppContext.BIRD_PATHS[AppContext.RAND_BIRD][n]));
                
    //             n++;

    //             if (startGame.getY() < max) {
    //                 indecrement *= -1;
    //             } else if (startGame.getY() > min) {
    //                 indecrement *= -1;
    //             }

    //             startGame.setLocation(startGame.getX(), startGame.getY() + indecrement);
    //         }
    //     };

    //     timer.schedule(timerTask, 0, AppContext.REFRESH);
    // }

    private void welcomeAnimation(SpriteComponent bird, SpriteComponent startGame) {
        int min = AppContext.HEIGHT / 8;
        int max = startGame.getY();

        welcomeTimer = new Timer(AppContext.WELCOME_ANIMATION_REFRESH, e -> {
            welcomeBirdFrameTick++;

            if (welcomeBirdFrameTick < AppContext.BIRD_FRAME_INTERVAL_TICKS) {
                return;
            }

            welcomeBirdFrameTick = 0;

            if (welcomeBirdFrame == AppContext.BIRD_PATHS[AppContext.RAND_BIRD].length) {
                welcomeBirdFrame = 0;
            }

            bird.setImage(AssetLoader.load(AppContext.BIRD_PATHS[AppContext.RAND_BIRD][welcomeBirdFrame]));
            welcomeBirdFrame++;
            // if (welcomeBirdFrame == AppContext.BIRD_PATHS[AppContext.RAND_BIRD].length) {
            //     welcomeBirdFrame = 0;
            // }

            // bird.setImage(AssetLoader.load(AppContext.BIRD_PATHS[AppContext.RAND_BIRD][welcomeBirdFrame]));
            // welcomeBirdFrame++;

            startGamePhase += 0.06;

            int amplitude = 12;
            int y = startGameBaseY + (int) Math.round(Math.sin(startGamePhase) * amplitude);

            startGame.setLocation(startGame.getX(), y);

            // if (startGame.getY() <= max) {
            //     welcomeDirection = 1;
            // } else if (startGame.getY() >= min) {
            //     welcomeDirection = -1;
            // }

            // startGame.setLocation(startGame.getX(), startGame.getY() + welcomeDirection);
        });

        welcomeTimer.start();
    }

    public void stop() {
        if (welcomeTimer != null) {
            welcomeTimer.stop();
            welcomeTimer = null;
        }

        if (jpanel != null) {
            Container parent = jpanel.getParent();

            if (parent != null) {
                parent.remove(jpanel);
                parent.revalidate();
                parent.repaint();
            }

            jpanel.removeAll();
            jpanel = null;
        }
    }

    private void drawWelcomeScreen(JPanel jpanel, SpriteComponent bird, SpriteComponent base, SpriteComponent startGame) {
        // bird.setSize(bird.getPreferredSize());
        // base.setSize(base.getPreferredSize());
        // startGame.setSize(startGame.getPreferredSize());

        // ImageIcon birdIcon = AssetLoader.load(AppContext.BIRD_PATHS[AppContext.RAND_BIRD][0]);
        // ImageIcon baseIcon = AssetLoader.load(AppContext.BASE);
        // ImageIcon startGameIcon = AssetLoader.load(AppContext.START_GAME);

        BufferedImage birdIcon = AssetLoader.load(AppContext.BIRD_PATHS[AppContext.RAND_BIRD][0]);
        BufferedImage baseIcon = AssetLoader.load(AppContext.BASE);
        BufferedImage startGameIcon = AssetLoader.load(AppContext.START_GAME);

        // bird.setIcon(birdIcon);
        // base.setIcon(baseIcon);
        // startGame.setIcon(startGameIcon);

        bird.setImage(birdIcon);
        base.setImage(baseIcon);
        startGame.setImage(startGameIcon);
 
        // Dimension b = bird.getPreferredSize();
        // Dimension ba = base.getPreferredSize();

        // Dimension b = new Dimension(birdIcon.getWidth(), birdIcon.getHeight());
        // Dimension ba = new Dimension(baseIcon.getWidth(), baseIcon.getHeight());

        Dimension b = new Dimension(birdIcon.getWidth(), birdIcon.getHeight());
        Dimension ba = new Dimension(baseIcon.getWidth(), baseIcon.getHeight()); 

        int xBird = b.width;
        int yBird = (jpanel.getHeight() - b.height) / 2;

        int xBase = 0;
        int yBase = jpanel.getHeight() - AppContext.BASE_HEIGHT;

        bird.setBounds(new Rectangle(xBird, yBird, b.width, b.height));
        base.setBounds(new Rectangle(xBase, yBase, ba.width, ba.height));

        // Dimension s = startGame.getPreferredSize();

        Dimension s = new Dimension(startGameIcon.getWidth(), startGameIcon.getHeight());

        int xStartGame = (jpanel.getWidth() - s.width) / 2;
        int yStartGame = (jpanel.getHeight()/2 - s.height);

        startGameBaseY = yStartGame;

        startGame.setBounds(new Rectangle(xStartGame, yStartGame, s.width, s.height));

        jpanel.add(bird);
        jpanel.add(base);
        jpanel.add(startGame);
    }

    private void initPanel(JPanel jpanel) {
        jpanel.setSize(AppContext.WIDTH, AppContext.HEIGHT);
        jpanel.setPreferredSize(new Dimension(AppContext.WIDTH, AppContext.HEIGHT));
        jpanel.setVisible(true);
        jpanel.setOpaque(false);
        jpanel.setLayout(null);
    }
}
