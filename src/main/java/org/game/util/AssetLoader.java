package org.game.util;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

public final class AssetLoader {

    private static final Map<String, BufferedImage> IMAGE_CACHE = new ConcurrentHashMap<>();

    private AssetLoader() {}

    public static BufferedImage load(String path) {
        return IMAGE_CACHE.computeIfAbsent(path, AssetLoader::image);
    }

    private static BufferedImage image(String path) {
        try (InputStream inputStream = AssetLoader.class.getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new RuntimeException("Missing image resource: " + path);
            }

            BufferedImage image = ImageIO.read(inputStream);

            if (image == null) {
                throw new RuntimeException("Invalid image resource: " + path);
            }

            return image;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load image: " + path, e);
        }
    }
}