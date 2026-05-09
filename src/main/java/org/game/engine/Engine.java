package org.game.engine;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
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

    private static final double V = 10;
    private static final double g = 12;

    private static double v;
    private static double t;
    private static double animationinDecrement;
    private static double rotationInDecrement;
    private static double rotation;

    private static volatile boolean isRunning = false;
    private static boolean isBound = false;

    // private Timer animationTimer;
    // private Timer rotateTimer;
    // private Timer gravityTimer;
    // private Timer pipeSpawnTimer;
    // private final List<Timer> pipeTimerBuffer= new ArrayList<>();

    private Timer engineTimer;

    private long lastFrameNanos;
    private double pipeSpawnAccumulatorMs = 0.0;

    private double birdY;

    private boolean gravityActive = false;
    private boolean rotationActive = false;

    private final List<PipeState> pipes = new ArrayList<>();

    private static final int ENGINE_FRAME_MS = 16;
    private static final int MAX_FRAME_MS = 50;

    private static final double PIPE_PIXELS_PER_STEP = 5.0;

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

        RotatableSprite bird = (RotatableSprite) assets.get(AppContext.BIRD_KEY);

        if (!isBound) {
            initMouseListener(jframe, jpanel, bird);
            initScore(jpanel, score);
            isBound = true;
        }

        initEngineLoop(jframe, jpanel, bird);
    }

    private void displayScore(int score) {
        String strScore = String.format("%03d", score);
        scoreFirstDigit
                .setImage(AssetLoader.load(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(0)))]));
        scoreSecondDigit
                .setImage(AssetLoader.load(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(1)))]));
        scoreThirdDigit
                .setImage(AssetLoader.load(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(2)))]));
    }

    private void initScore(JPanel jpanel, int score) {
        String strScore = String.format("%03d", score);

        int y = 0;
        int x = jpanel.getWidth() / 2;

        // scoreFirstDigit.setSize(scoreFirstDigit.getPreferredSize());
        BufferedImage sfIcon = AssetLoader
                .load(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(0)))]);
        scoreFirstDigit.setImage(sfIcon);
        // Dimension sf = scoreFirstDigit.getPreferredSize();
        int sfHeight = sfIcon.getHeight();
        int sfWidth = sfIcon.getWidth();
        scoreFirstDigit.setBounds(new Rectangle(x - sfWidth, y + sfWidth, sfWidth, sfHeight));

        // scoreSecondDigit.setSize(scoreSecondDigit.getPreferredSize());
        BufferedImage sdIcon = AssetLoader
                .load(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(1)))]);
        scoreSecondDigit.setImage(sdIcon);
        // Dimension sd = scoreSecondDigit.getPreferredSize();
        int sdHeight = sdIcon.getHeight();
        int sdWidth = sdIcon.getWidth();
        scoreSecondDigit.setBounds(new Rectangle(x, y + sdWidth, sdWidth, sdHeight));

        BufferedImage stIcon = AssetLoader
                .load(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(2)))]);
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

    private void initMouseListener(JFrame jframe, JPanel jpanel, RotatableSprite bird) {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isRunning) {
                    jframe.removeMouseListener(this);
                    return;
                }

                animationinDecrement = 0.12;
                v = V;
                t = 0;

                rotation = 0;
                rotationInDecrement = 1.5;

                birdY = bird.getY();

                gravityActive = true;
                rotationActive = true;

                AudioLoader.play(AppContext.FLAP_AUD);
            }
        };

        jframe.addMouseListener(mouseAdapter);
    }

    private void initEngineLoop(JFrame jframe, JPanel jpanel, RotatableSprite bird) {
        if (engineTimer != null && engineTimer.isRunning()) {
            return;
        }

        lastFrameNanos = System.nanoTime();

        engineTimer = new Timer(ENGINE_FRAME_MS, e -> {
            if (!isRunning) {
                stopEngineLoop();
                return;
            }

            long now = System.nanoTime();
            double frameMs = (now - lastFrameNanos) / 1_000_000.0;
            lastFrameNanos = now;

            if (frameMs > MAX_FRAME_MS) {
                frameMs = MAX_FRAME_MS;
            }

            double scale = frameMs / AppContext.REFRESH;

            updateGravity(jframe, jpanel, bird, scale);
            updateRotation(bird, scale);
            updatePipeSpawning(jframe, jpanel, bird, frameMs);
            updatePipes(jframe, jpanel, bird, scale);

            jpanel.repaint();
        });

        engineTimer.start();
    }

    private void updateGravity(JFrame jframe, JPanel jpanel, RotatableSprite bird, double scale) {
        if (!gravityActive) {
            return;
        }

        double s = v - g * Math.pow(t, 2);
        t += animationinDecrement * scale;

        birdY -= s * scale;

        int x = bird.getX();
        int y = (int) Math.round(birdY);

        bird.setLocation(x, y);

        if (detectCollision(jframe, bird)) {
            gameOver(jframe, jpanel);
        }
    }

    private void updateRotation(RotatableSprite bird, double scale) {
        if (!rotationActive) {
            return;
        }

        if (rotation <= -90.0 && rotationInDecrement > 0) {
            rotationInDecrement *= -1;
        }

        rotation -= rotationInDecrement * scale;

        bird.setRotationDegrees(rotation);
    }

    private void updatePipeSpawning(JFrame jframe, JPanel jpanel, SpriteComponent bird, double frameMs) {
        pipeSpawnAccumulatorMs += frameMs;

        if (pipeSpawnAccumulatorMs < AppContext.PIPE_REFRESH) {
            return;
        }

        pipeSpawnAccumulatorMs -= AppContext.PIPE_REFRESH;

        spawnPipePair(jpanel);
    }

    private void spawnPipePair(JPanel jpanel) {
        BufferedImage pipeIcon = AssetLoader.load(AppContext.PIPE_PATHS[RAND_PIPE]);

        int pipeWidth = pipeIcon.getWidth();
        int pipeHeight = pipeIcon.getHeight();

        int xPipe = jpanel.getWidth() + pipeWidth;

        int yBelowPipe = GenerateRandom.generateRandom(
                AppContext.BASE_HEIGHT + AppContext.VERTICAL_PIPE_LIMIT,
                jpanel.getHeight() - AppContext.VERTICAL_PIPE_LIMIT - AppContext.PIPE_GAP);

        int yAbovePipe = yBelowPipe - pipeHeight - AppContext.PIPE_GAP;

        SpriteComponent belowPipe = new SpriteComponent();
        belowPipe.setImage(pipeIcon);
        belowPipe.setBounds(new Rectangle(xPipe, yBelowPipe, pipeWidth, pipeHeight));

        RotatableSprite abovePipe = new RotatableSprite();
        abovePipe.setImage(pipeIcon);
        abovePipe.setRotationDegrees(180);
        abovePipe.setBounds(new Rectangle(xPipe, yAbovePipe, pipeWidth, pipeHeight));

        jpanel.add(belowPipe);
        jpanel.add(abovePipe);

        pipes.add(new PipeState(belowPipe, xPipe, pipeWidth, true));
        pipes.add(new PipeState(abovePipe, xPipe, pipeWidth, false));

        jpanel.revalidate();
    }

    private void updatePipes(JFrame jframe, JPanel jpanel, SpriteComponent bird, double scale) {
        Iterator<PipeState> iterator = pipes.iterator();

        while (iterator.hasNext()) {
            PipeState pipeState = iterator.next();
            SpriteComponent pipe = pipeState.getSprite();

            pipeState.setX(pipeState.getX() - PIPE_PIXELS_PER_STEP * scale);

            int x = (int) Math.round((float) pipeState.getX());
            int y = pipe.getY();

            pipe.setLocation(x, y);

            if (detectCollision(pipe, bird)) {
                gameOver(jframe, jpanel);
                return;
            }

            if (pipeState.isBelowPipe()) {
                checkScore(pipeState, bird);
            }

            if (pipe.getX() < -pipeState.getWidth()) {
                jpanel.remove(pipe);
                iterator.remove();
            }
        }
    }

    private void checkScore(PipeState pipeState, SpriteComponent bird) {
        if (pipeState.isScored()) {
            return;
        }

        SpriteComponent pipe = pipeState.getSprite();

        if (pipe.getX() + pipe.getWidth() < bird.getX()) {
            score++;
            pipeState.setScored(true);

            displayScore(score);
            AudioLoader.play(AppContext.SCORE_AUD);
        }
    }

    private void gameOver(JFrame jframe, JPanel jpanel) {
        if (!isRunning)
            return;
        isRunning = false;
        stopEngineLoop();
        clearRuntimeState();

        // if (animationTimer != null) {
        // animationTimer.stop();
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
        gameOverMessage.setBounds(new Rectangle(jpanel.getWidth() / 2 - gm.width / 2,
                jpanel.getHeight() / 2 - gm.height, gm.width, gm.height));

        jpanel.add(gameOverMessage);
        jpanel.revalidate();
        jpanel.repaint();

        // if (animationTimer != null) {
        // animationTimer.stop();
        // animationTimer = null;
        // }
        // if (rotateTimer != null) {
        // rotateTimer.stop();
        // rotateTimer = null;
        // }

        if (mouseAdapter != null) {
            jframe.removeMouseListener(mouseAdapter);
            mouseAdapter = null;
        }
    }

    private void stopEngineLoop() {
        if (engineTimer != null) {
            engineTimer.stop();
            engineTimer = null;
        }
    }

    private void clearRuntimeState() {
        gravityActive = false;
        rotationActive = false;

        pipeSpawnAccumulatorMs = 0.0;
        pipes.clear();

        isBound = false;
    }
}