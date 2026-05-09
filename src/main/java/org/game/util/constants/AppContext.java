package org.game.util.constants;

import org.game.util.GenerateRandom;

public final class AppContext {

    public static final String TITLE = "Flappy Bird";

    public static final int BASE_HEIGHT = 112;
    public static final int PIPE_GAP = 100;
    public static final int VERTICAL_PIPE_LIMIT = 112;

    public static final int RAND_BIRD = GenerateRandom.generateRandom(0, 3);

    public static final String BIRD_KEY = "bird";

    public static final int REFRESH = 56;
    public static final int PIPE_REFRESH = 2250;
    public static final int BIRD_ANIMATION_REFRESH = 56;
    public static final int WELCOME_ANIMATION_REFRESH = 16;
    public static final double GRAVITY = 900.0;
    public static final double FLAP_VELOCITY = -330.0;
    public static final double PIPE_SPEED = 120.0;
    public static final int BIRD_BOX_SIZE = 32;
    public static final int BIRD_FRAME_INTERVAL_TICKS = 2;
    
    public static final int WIDTH = 320;
    public static final int HEIGHT = 582;
    
    // public static final String SYSTEM = Paths.get(System.getProperty("user.dir")).toString();

    public static final String BASE = "/sprites/base.png";

    public static final String GAMEOVER = "/sprites/gameover.png";

    public static final String[] BACKGROUNDS = {
        "/sprites/background-day.png",
        "/sprites/background-night.png",
    };

    public static final String[][] BIRD_PATHS = {
        {
            "/sprites/bluebird-downflap.png",
            "/sprites/bluebird-midflap.png",
            "/sprites/bluebird-upflap.png"
        },
        {
            "/sprites/redbird-downflap.png",
            "/sprites/redbird-midflap.png",
            "/sprites/redbird-upflap.png"
        },
        {
            "/sprites/yellowbird-downflap.png",
            "/sprites/yellowbird-midflap.png",
            "/sprites/yellowbird-upflap.png"
        },
    };

    public static final String[] PIPE_PATHS = {
        "/sprites/pipe-green.png",
        "/sprites/pipe-red.png"
    };

    public static final String[] SCORES = {
        "/sprites/0.png",
        "/sprites/1.png",
        "/sprites/2.png",
        "/sprites/3.png",
        "/sprites/4.png",
        "/sprites/5.png",
        "/sprites/6.png",
        "/sprites/7.png",
        "/sprites/8.png",
        "/sprites/9.png",
    };

    public static final String FAVICON = "/favicon.png";

    public static final String START_GAME = "/sprites/message.png";

    public static final String FLAP_AUD = "/audio/wing.wav";
    public static final String HIT_AUD = "/audio/hit.wav";
    public static final String SCORE_AUD = "/audio/point.wav";
}

