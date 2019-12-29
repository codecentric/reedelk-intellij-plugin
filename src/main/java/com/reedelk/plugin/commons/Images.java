package com.reedelk.plugin.commons;

import com.reedelk.plugin.exception.PluginException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class Images {

    private Images() {
    }

    public static class Component {

        private Component() {
        }

        public static final Image DefaultComponent;
        public static final Image RemoveComponent;
        public static final Image InboundPlaceholderIcon;
        public static final Image OutboundPlaceholderIcon;
        public static final Image PlaceholderHintIcon;

        static {
            DefaultComponent = loadImage("/images/ComponentDefault.png");
            RemoveComponent = loadImage("/images/ComponentRemove.png");
            InboundPlaceholderIcon = loadImage("/images/ComponentInboundPlaceholder.png");
            OutboundPlaceholderIcon = loadImage("/images/ComponentOutboundPlaceholder.png");
            PlaceholderHintIcon = loadImage("/images/ComponentPlaceholderHint.png");
        }
    }

    public static class Flow {

        private Flow() {
        }

        public static final Image Error;
        public static final Image Loading;

        static {
            Error = loadImage("/images/FlowError.png");
            Loading = loadImage("/images/FlowLoading.png");
        }
    }

    private static Image loadImage(String resourceName) {
        try {
            URL resource = Images.class.getResource(resourceName);
            return ImageIO.read(resource);
        } catch (IOException exception) {
            String message = message("images.load.error", resourceName, exception.getMessage());
            throw new PluginException(message, exception);
        }
    }
}
