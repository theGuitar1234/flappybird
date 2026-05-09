package org.game.engine;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.game.gamescreen.RotatableSprite;
import org.game.gamescreen.SpriteComponent;
import org.game.util.AssetLoader;
import org.game.util.AudioLoader;
import org.game.util.GenerateRandom;
import org.game.util.constants.AppContext;

public class Engine {

    private Runnable onGameOver;

    private SpriteComponent belowPipe;
    private RotatableSprite abovePipe;

    private static final double V = 10;
    private static final double g = 9.8;

    private static double v;
    private static double t;
    private static double animationinDecrement;
    private static double rotationInDecrement;
    private static double rotation;

    private static boolean isInitAnimationBound = false;
    private static boolean isInitRotateBound = false;
    private static volatile boolean isRunning = false;
    private static boolean isBound = false;

    // private Timer animationTimer;
    private Timer rotateTimer;
    private Timer gravityTimer;
    private Timer pipeSpawnTimer;
    private final List<Timer> pipeTimerBuffer= new ArrayList<>();

    private static MouseAdapter mouseAdapter;

    private int score = 0;
    private final SpriteComponent scoreFirstDigit = new SpriteComponent();
    private final SpriteComponent scoreSecondDigit = new SpriteComponent();
    private final SpriteComponent scoreThirdDigit = new SpriteComponent();

    private final SpriteComponent gameOverMessage = new SpriteComponent();

    private static final int RAND_PIPE = GenerateRandom.generateRandom(0, 2);

    public Engine(Runnable onGameOver) {
        this.onGameOver = onGameOver;
    }

    public void run(JPanel jpanel, JFrame jframe, Map<String, SpriteComponent> assets) {

        isRunning = true;

        // Sound.init();

        RotatableSprite bird = (RotatableSprite) assets.get(AppContext.BIRD_KEY);

        if (!isBound) {
            initMouseListener(jframe, jpanel, bird);
            initPipes(jframe, jpanel, bird);
            initScore(jpanel, score);
            isBound = true;
        }
    }

    private void displayScore(int score) {
        String strScore = String.format("%03d", score);
        scoreFirstDigit.setImage(AssetLoader.load(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(0)))]));
        scoreSecondDigit.setImage(AssetLoader.load(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(1)))]));
        scoreThirdDigit.setImage(AssetLoader.load(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(2)))]));
    }

    private void initScore(JPanel jpanel, int score) {
        String strScore = String.format("%03d", score);
        
        int y = 0;
        int x = jpanel.getWidth() / 2;

        // scoreFirstDigit.setSize(scoreFirstDigit.getPreferredSize());
        BufferedImage sfIcon = AssetLoader.load(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(0)))]);
        scoreFirstDigit.setImage(sfIcon);
        // Dimension sf = scoreFirstDigit.getPreferredSize();
        int sfHeight = sfIcon.getHeight();
        int sfWidth = sfIcon.getWidth();
        scoreFirstDigit.setBounds(new Rectangle(x - sfWidth, y + sfWidth, sfWidth, sfHeight));

        // scoreSecondDigit.setSize(scoreSecondDigit.getPreferredSize());
        BufferedImage sdIcon = AssetLoader.load(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(1)))]);
        scoreSecondDigit.setImage(sdIcon);
        // Dimension sd = scoreSecondDigit.getPreferredSize();
        int sdHeight = sdIcon.getHeight();
        int sdWidth = sdIcon.getWidth();
        scoreSecondDigit.setBounds(new Rectangle(x, y + sdWidth, sdWidth, sdHeight));

        BufferedImage stIcon = AssetLoader.load(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(2)))]);
        // scoreThirdDigit.setSize(scoreThirdDigit.getPreferredSize());
        scoreThirdDigit.setImage(stIcon);
        // Dimension st = scoreThirdDigit.getPreferredSize();
        int stHeight = stIcon.getHeight();
        int stWidth = stIcon.getWidth();
        scoreThirdDigit.setBounds(new Rectangle(x + sdWidth, y + stWidth, stWidth, stHeight));

        jpanel.add(scoreFirstDigit);
        jpanel.add(scoreSecondDigit);
        jpanel.add(scoreThirdDigit);
    }

    private boolean detectCollision(JFrame jframe, RotatableSprite bird) {
        return (bird.getY() > jframe.getHeight() - AppContext.BASE_HEIGHT);
    }

    private boolean detectCollision(SpriteComponent pipe, SpriteComponent bird) {
        return (pipe.getBounds().intersects(bird.getBounds()));
    }

    private void checkScore(SpriteComponent pipe, SpriteComponent bird) {
        Boolean alreadyScored = (Boolean) pipe.getClientProperty("scored");

        if (Boolean.TRUE.equals(alreadyScored)) return;
        if (pipe.getX() + pipe.getWidth() < bird.getX()) {
            score++;
            pipe.putClientProperty("scored", true);
            displayScore(score);
            // Sound.score();
            AudioLoader.play(AppContext.SCORE_AUD);
        }
    }

    private void initMouseListener(JFrame jframe, JPanel jpanel, RotatableSprite bird) {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isRunning) {
                    jframe.removeMouseListener(this);
                    return;
                }

                initAnimation(jframe, jpanel, bird);
                initRotate(bird);
                // Sound.flap();
                AudioLoader.play(AppContext.FLAP_AUD);
            }
        };
        jframe.addMouseListener(mouseAdapter);
    }

    private void initAnimation(JFrame jframe, JPanel jpanel, RotatableSprite bird) {

        animationinDecrement = 0.12;
        v = V;
        t = 0;

        if (isInitAnimationBound)
            return;
        isInitAnimationBound = true;

        gravityTimer = new Timer(AppContext.REFRESH, e -> {
            if (!isRunning) {
                gravityTimer.stop();
                return;
            }

            double s = v - g * (Math.pow(t, 2));
            t += animationinDecrement;

            int x = bird.getX();
            int y = (int) (bird.getY() - s);

            bird.setLocation(new Point(x, y));

            if (detectCollision(jframe, bird)) {
                gameOver(jframe, jpanel);
            }
        });

        gravityTimer.start();
        // animationTimer.schedule(timerTask, 0, AppContext.REFRESH);
    }

    private void initRotate(RotatableSprite bird) {

        rotation = 0;
        rotationInDecrement = 1.5;

        if (isInitRotateBound)
            return;
        isInitRotateBound = true;

        rotateTimer = new Timer(AppContext.REFRESH, e -> {
            if (!isRunning) {
                rotateTimer.stop();
                return;
            }
            if (rotation <= -90.0)
                rotationInDecrement *= -1;

            rotation -= rotationInDecrement;
            bird.setRotationDegrees(rotation);
        });

        rotateTimer.start();
        // rotateTimer.schedule(timerTask, 0, AppContext.REFRESH);
    }

    private void initPipes(JFrame jframe, JPanel jpanel, SpriteComponent bird) {
        pipeSpawnTimer = new Timer(AppContext.PIPE_REFRESH, e -> {
            if (!isRunning) {
                pipeSpawnTimer.stop();
                return;
            }
            belowPipe = new SpriteComponent();
            // belowPipe.setSize(belowPipe.getPreferredSize());
            BufferedImage bpIcon = AssetLoader.load(AppContext.PIPE_PATHS[RAND_PIPE]);
            belowPipe.setImage(bpIcon);

            // Dimension bp = belowPipe.getPreferredSize();
            int bpWidth = bpIcon.getWidth();
            int bpHeight = bpIcon.getHeight();
            int xBelowPipe = jpanel.getWidth() + bpWidth;
            // drops as Y increases
            int belowPipeOffsetY = GenerateRandom.generateRandom(
                    AppContext.BASE_HEIGHT + AppContext.VERTICAL_PIPE_LIMIT,
                    jpanel.getHeight() - AppContext.VERTICAL_PIPE_LIMIT - AppContext.PIPE_GAP);
            int yBelowPipe = belowPipeOffsetY;

            belowPipe.setBounds(new Rectangle(xBelowPipe, yBelowPipe, bpWidth, bpHeight));

            abovePipe = new RotatableSprite();
            abovePipe.setRotationDegrees(180);
            // abovePipe.setSize(belowPipe.getPreferredSize());
            BufferedImage apIcon = AssetLoader.load(AppContext.PIPE_PATHS[RAND_PIPE]);
            abovePipe.setImage(apIcon);

            // Dimension ap = abovePipe.getPreferredSize();
            int apWidth = apIcon.getWidth();
            int apHeight = apIcon.getHeight();

            int xAbovePipe = jpanel.getWidth() + apWidth;
            int abovePipeOffsetY = yBelowPipe;
            int yAbovePipe = abovePipeOffsetY - apHeight - AppContext.PIPE_GAP;

            abovePipe.setBounds(new Rectangle(xAbovePipe, yAbovePipe, apWidth, apHeight));

            jpanel.add(belowPipe);
            jpanel.add(abovePipe);
            jpanel.revalidate();
            jpanel.repaint();
            movePipes(jframe, jpanel, belowPipe, bird, new Dimension(bpWidth, bpHeight), true);
            movePipes(jframe, jpanel, abovePipe, bird, new Dimension(apWidth, apHeight), false);
        });

        pipeSpawnTimer.start();
    }

    private void movePipes(JFrame jframe, JPanel jpanel, SpriteComponent pipe, SpriteComponent bird, Dimension p, boolean isBelowPipe) {
        Timer pipeMoveTimer = new Timer(AppContext.REFRESH, e -> {
            if (!isRunning) {
                ((Timer) e.getSource()).stop();
                return;
            }

            if (detectCollision(pipe, bird)) {
                gameOver(jframe, jpanel);
            }

            if (isBelowPipe) {
                checkScore(pipe, bird);
            }

            if (pipe.getX() < -p.getWidth()) {
                jpanel.remove(pipe);
                jpanel.repaint();

                Timer source = (Timer) e.getSource();
                source.stop();

                return;
            }

            int x = (int) (pipe.getX() - 5);
            int y = pipe.getY();
            pipe.setLocation(new Point(x, y));
        });

        pipeTimerBuffer.add(pipeMoveTimer);
        pipeMoveTimer.start();
    }

    private void gameOver(JFrame jframe, JPanel jpanel) {
        if (!isRunning) return;
        isRunning = false;
        stopTimers();

        // if (animationTimer != null) {
        //     animationTimer.stop();
        // }

        if (onGameOver != null) {
            onGameOver.run();
        }

        // Sound.hit();
        // Sound.stopHit();
        // Sound.close();

        AudioLoader.play(AppContext.HIT_AUD);

        // gameOverMessage.setSize(gameOverMessage.getPreferredSize());
        // ImageIcon gameOverIcon = AssetLoader.load(AppContext.GAMEOVER);
        BufferedImage gameOverIcon = AssetLoader.load(AppContext.GAMEOVER);
        // gameOverMessage.setImage(gameOverIcon);
        gameOverMessage.setImage(gameOverIcon);
        // Dimension gm = gameOverMessage.getPreferredSize();
        Dimension gm = new Dimension(gameOverIcon.getWidth(), gameOverIcon.getHeight());
        gameOverMessage.setBounds(new Rectangle(jpanel.getWidth()/2 - gm.width/2, jpanel.getHeight()/2 - gm.height, gm.width, gm.height));
        
        jpanel.add(gameOverMessage);
        jpanel.revalidate();
        jpanel.repaint();

        // if (animationTimer != null) {
        //     animationTimer.stop();
        //     animationTimer = null;
        // }
        // if (rotateTimer != null) {
        //     rotateTimer.stop();
        //     rotateTimer = null;
        // }

        if (mouseAdapter != null) {
            jframe.removeMouseListener(mouseAdapter);
            mouseAdapter = null;
        }

        jframe.removeMouseListener(mouseAdapter);

        isInitAnimationBound = false;
        isInitRotateBound = false;
    }

    private void stopTimers() {
        if (gravityTimer != null) {
            gravityTimer.stop();
            gravityTimer = null;
        }

        if (rotateTimer != null) {
            rotateTimer.stop();
            rotateTimer = null;
        }

        if (pipeSpawnTimer != null) {
            pipeSpawnTimer.stop();
            pipeSpawnTimer = null;
        }

        for (Timer timer : pipeTimerBuffer) {
            if (timer != null) {
                timer.stop();
            }
        }

        pipeTimerBuffer.clear();
    }
}