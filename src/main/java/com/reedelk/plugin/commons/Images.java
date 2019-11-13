package com.reedelk.plugin.commons;

import com.intellij.openapi.diagnostic.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Images {

    private static final Logger LOG = Logger.getInstance(Images.class);

    private Images() {
    }

    public static class Component {

        private Component() {
        }

        public static final Image DefaultComponent;
        public static final Image RemoveComponent;
        public static final Image InboundPlaceholderIcon;
        public static final Image PlaceholderHintIcon;

        static {
            DefaultComponent = loadImage("/icons/default-component.png");
            RemoveComponent = loadImage("/icons/remove-component-icon.png");
            InboundPlaceholderIcon = loadImage("/icons/inbound-placeholder-icon.png");
            PlaceholderHintIcon = loadImage("/icons/placeholder-hint.png");
        }

        private static final Map<String, Image> KEY_IMAGE_MAP = new HashMap<>();

        public static void put(String key, Image image) {
            KEY_IMAGE_MAP.put(key, image);
        }

        public static Image get(String key) {
            return KEY_IMAGE_MAP.getOrDefault(key, DefaultComponent);
        }

    }

    public static class Flow {

        private Flow() {
        }

        public static final Image Error;
        public static final Image Loading;

        static {
            Error = loadImage("/icons/flow-error.png");
            Loading = loadImage("/icons/flow-loading.png");
        }
    }

    public static class Misc {

        private Misc() {
        }

        public static final Image LightningBolt;

        static {
            LightningBolt = loadImage("/icons/lightning-bolt.png");
        }
    }

    private static Image loadImage(String resourceName) {
        try {
            URL resource = Images.class.getResource(resourceName);
            return ImageIO.read(resource);
        } catch (IOException e) {
            LOG.error(String.format("Could not load image with resource name '%s'", resourceName), e);
            throw new ImageNotFound(e);
        }
    }

    static class ImageNotFound extends RuntimeException {
        ImageNotFound(IOException e) {
            super(e);
        }
    }
}
