package com.esb.plugin.commons;

import com.intellij.openapi.diagnostic.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Images {

    private static final Logger LOG = Logger.getInstance(Images.class);

    public static class Component {

        public static final Image DefaultComponent;
        public static final Image RemoveComponent;
        static {
            DefaultComponent = loadImage("/icons/default-component.png");
            RemoveComponent = loadImage("/icons/remove-component-icon.png");
        }

        private static final Map<String, Image> KEY_IMAGE_MAP = new HashMap<>();

        public static void put(String key, Image image) {
            KEY_IMAGE_MAP.put(key, image);
        }

        public static Image get(String key) {
            return KEY_IMAGE_MAP.getOrDefault(key, DefaultComponent);
        }
    }

    private static Image loadImage(String resourceName) {
        try {
            URL resource = Images.class.getResource(resourceName);
            return ImageIO.read(resource);
        } catch (IOException e) {
            LOG.error(String.format("Could not load image with resource name '%s'", resourceName), e);
            throw new RuntimeException(e);
        }
    }
}
