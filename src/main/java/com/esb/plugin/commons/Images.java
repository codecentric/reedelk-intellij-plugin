package com.esb.plugin.commons;

import com.esb.plugin.component.scanner.ComponentIconAndImageProvider;
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

        private static final Image DefaultComponentImage;

        static {
            try {
                URL resource = ComponentIconAndImageProvider.class.getResource("/icons/default-component.png");
                DefaultComponentImage = ImageIO.read(resource);
            } catch (IOException e) {
                LOG.error("Could not load default component image", e);
                throw new RuntimeException(e);
            }
        }

        private static final Map<String, Image> KEY_IMAGE_MAP = new HashMap<>();


        public static void put(String key, Image image) {
            KEY_IMAGE_MAP.put(key, image);
        }

        public static Image get(String key) {
            return KEY_IMAGE_MAP.getOrDefault(key, DefaultComponentImage);
        }


    }
}
