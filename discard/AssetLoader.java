package org.game.util;

import java.net.URL;

import javax.swing.ImageIcon;

public class AssetLoader {
    public static ImageIcon load(String path) {
        URL resource = AssetLoader.class.getResource(path);
        if (resource == null) {
            throw new RuntimeException("Missing Resource: " + path);
        }
        return new ImageIcon(resource);
    }
}
