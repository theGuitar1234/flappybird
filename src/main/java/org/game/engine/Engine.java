package org.game.engine;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.game.engine.sound.Sound;
import org.game.gamescreen.RotatableLabel;
import org.game.util.GenerateRandom;
import org.game.util.constants.AppContext;

public class Engine {

    private JLabel belowPipe;
    private RotatableLabel abovePipe;

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

    private static Timer animationTimer;
    private static Timer rotateTimer;
    private static MouseAdapter mouseAdapter;

    private int score = 0;
    private final JLabel scoreFirstDigit = new JLabel();
    private final JLabel scoreSecondDigit = new JLabel();
    private final JLabel scoreThirdDigit = new JLabel();

    private final JLabel gameOverMessage = new JLabel();

    private static final int RAND_PIPE = GenerateRandom.generateRandom(0, 2);

    public void run(JPanel jpanel, JFrame jframe, Map<String, JLabel> assets) {

        isRunning = true;

        Sound.init();

        RotatableLabel bird = (RotatableLabel) assets.get(AppContext.BIRD_KEY);

        if (!isBound) {
            initMouseListener(jframe, jpanel, bird);
            initPipes(jframe, jpanel, bird);
            initScore(jpanel, score);
            isBound = true;
        }
    }

    private void displayScore(int score) {
        String strScore = String.format("%03d", score);
        scoreFirstDigit.setIcon(new ImageIcon(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(0)))]));
        scoreSecondDigit.setIcon(new ImageIcon(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(1)))]));
        scoreThirdDigit.setIcon(new ImageIcon(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(2)))]));
    }

    private void initScore(JPanel jpanel, int score) {
        String strScore = String.format("%03d", score);
        
        int y = 0;
        int x = jpanel.getWidth() / 2;

        scoreFirstDigit.setSize(scoreFirstDigit.getPreferredSize());
        scoreFirstDigit.setIcon(new ImageIcon(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(0)))]));
        Dimension sf = scoreFirstDigit.getPreferredSize();
        scoreFirstDigit.setBounds(new Rectangle(x - sf.width, y + sf.width, sf.width, sf.height));

        scoreSecondDigit.setSize(scoreSecondDigit.getPreferredSize());
        scoreSecondDigit.setIcon(new ImageIcon(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(1)))]));
        Dimension sd = scoreSecondDigit.getPreferredSize();
        scoreSecondDigit.setBounds(new Rectangle(x, y + sd.width, sd.width, sd.height));

        scoreThirdDigit.setSize(scoreThirdDigit.getPreferredSize());
        scoreThirdDigit.setIcon(new ImageIcon(AppContext.SCORES[Integer.parseInt(String.valueOf(strScore.charAt(2)))]));
        Dimension st = scoreThirdDigit.getPreferredSize();
        scoreThirdDigit.setBounds(new Rectangle(x + sd.width, y + st.width, st.width, st.height));

        jpanel.add(scoreFirstDigit);
        jpanel.add(scoreSecondDigit);
        jpanel.add(scoreThirdDigit);
    }

    private boolean detectCollision(JFrame jframe, RotatableLabel bird) {
        return (bird.getY() > jframe.getHeight() - AppContext.BASE_HEIGHT);
    }

    private boolean detectCollision(JLabel pipe, JLabel bird) {
        return (pipe.getBounds().intersects(bird.getBounds()));
    }

    private void checkScore(JLabel pipe, JLabel bird) {
        Boolean alreadyScored = (Boolean) pipe.getClientProperty("scored");

        if (Boolean.TRUE.equals(alreadyScored)) return;
        if (pipe.getX() + pipe.getWidth() < bird.getX()) {
            score++;
            pipe.putClientProperty("scored", true);
            displayScore(score);
            Sound.score();
        }
    }

    private void initMouseListener(JFrame jframe, JPanel jpanel, RotatableLabel bird) {
        mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isRunning) {
                    jframe.removeMouseListener(this);
                    return;
                }

                initAnimation(jframe, jpanel, bird);
                initRotate(bird);
                Sound.flap();
            }
        };
        jframe.addMouseListener(mouseAdapter);
    }

    private void initAnimation(JFrame jframe, JPanel jpanel, RotatableLabel bird) {

        animationinDecrement = 0.12;
        v = V;
        t = 0;

        if (isInitAnimationBound)
            return;
        isInitAnimationBound = true;

        animationTimer = new Timer(AppContext.REFRESH, e -> {
            if (!isRunning) {
                animationTimer.stop();
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

        animationTimer.start();
        // animationTimer.schedule(timerTask, 0, AppContext.REFRESH);
    }

    private void initRotate(RotatableLabel bird) {

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

    private void initPipes(JFrame jframe, JPanel jpanel, JLabel bird) {
        animationTimer = new Timer(AppContext.PIPE_REFRESH, e -> {
            if (!isRunning) {
                animationTimer.stop();
                return;
            }
            belowPipe = new JLabel();
            belowPipe.setSize(belowPipe.getPreferredSize());
            belowPipe.setIcon(new ImageIcon(AppContext.PIPE_PATHS[RAND_PIPE]));

            Dimension bp = belowPipe.getPreferredSize();

            int xBelowPipe = jpanel.getWidth() + bp.width;
            // drops as Y increases
            int belowPipeOffsetY = GenerateRandom.generateRandom(
                    AppContext.BASE_HEIGHT + AppContext.VERTICAL_PIPE_LIMIT,
                    jpanel.getHeight() - AppContext.VERTICAL_PIPE_LIMIT - AppContext.PIPE_GAP);
            int yBelowPipe = belowPipeOffsetY;

            belowPipe.setBounds(new Rectangle(xBelowPipe, yBelowPipe, bp.width, bp.height));

            abovePipe = new RotatableLabel();
            abovePipe.setRotationDegrees(180);
            abovePipe.setSize(belowPipe.getPreferredSize());
            abovePipe.setIcon(new ImageIcon(AppContext.PIPE_PATHS[RAND_PIPE]));

            Dimension ap = abovePipe.getPreferredSize();

            int xAbovePipe = jpanel.getWidth() + ap.width;
            int abovePipeOffsetY = yBelowPipe;
            int yAbovePipe = abovePipeOffsetY - ap.height - AppContext.PIPE_GAP;

            abovePipe.setBounds(new Rectangle(xAbovePipe, yAbovePipe, ap.width, ap.height));

            jpanel.add(belowPipe);
            jpanel.add(abovePipe);
            jpanel.revalidate();
            jpanel.repaint();
            movePipes(jframe, jpanel, belowPipe, bird, bp, true);
            movePipes(jframe, jpanel, abovePipe, bird, ap, false);
        });

        animationTimer.start();
    }

    private void movePipes(JFrame jframe, JPanel jpanel, JLabel pipe, JLabel bird, Dimension p, boolean isBelowPipe) {
        animationTimer = new Timer(AppContext.REFRESH, e -> {
            if (!isRunning) {
                animationTimer.stop();
                return;
            }

            if (detectCollision(pipe, bird)) {
                gameOver(jframe, jpanel);
            }

            if (isBelowPipe) {
                checkScore(pipe, bird);
            }

            if (pipe.getX() < -p.getWidth())
                jpanel.remove(pipe);

            int x = (int) (pipe.getX() - 5);
            int y = pipe.getY();
            pipe.setLocation(new Point(x, y));
        });

        animationTimer.start();
    }

    private void gameOver(JFrame jframe, JPanel jpanel) {
        isRunning = false;

        Sound.hit();
        // Sound.stopHit();
        Sound.close();

        gameOverMessage.setSize(gameOverMessage.getPreferredSize());
        gameOverMessage.setIcon(new ImageIcon(AppContext.GAMEOVER));
        Dimension gm = gameOverMessage.getPreferredSize();
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
}