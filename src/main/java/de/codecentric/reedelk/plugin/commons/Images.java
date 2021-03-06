package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.plugin.exception.PluginException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

public class Images {

    private Images() {
    }

    public static class Component {

        private Component() {
        }

        public static final Image UknownComponent;
        public static final Image DefaultComponent;
        public static final Image RemoveComponent;
        public static final Image InboundPlaceholderIcon;
        public static final Image OutboundPlaceholderIcon;
        public static final Image PlaceholderHintIcon;

        static {
            UknownComponent = loadImage("/images/ComponentUnknown.png");
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

    public static class Misc {

        private Misc() {
        }

        public static final Image ReedelkLogo;

        static {
            ReedelkLogo = loadImage("/images/ReedelkLogo.png");
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
