package de.codecentric.reedelk.plugin.commons;

import java.awt.*;

import static java.awt.Font.PLAIN;

public class Fonts {

    private static String fontName = FontInfoProvider.getInstance().fontName();
    private static int fontSize = FontInfoProvider.getInstance().fontSize();

    private Fonts() {
    }

    public static class Component {

        private Component() {
        }

        public static final Font TITLE = new Font(fontName, PLAIN, 13);
        public static final Font DESCRIPTION = new Font(fontName, PLAIN, 13);
        public static final Font INBOUND = new Font(fontName, PLAIN, 20);
    }

    public static class Flow {

        private Flow() {
        }

        public static final Font TITLE = new Font(fontName, PLAIN, 20);
        public static final Font DESCRIPTION = new Font(fontName, PLAIN, 13);
    }

    public static class Router {

        private Router() {
        }

        public static final Font DEFAULT_ROUTE = new Font(fontName, PLAIN, 11);
    }

    public static class ScriptEditor {

        private ScriptEditor() {
        }

        public static final int SCRIPT_EDITOR_FONT_SIZE;
        public static final Font DYNAMIC_FIELD_FONT_SIZE;

        static {
            SCRIPT_EDITOR_FONT_SIZE = fontSize + 6;
            DYNAMIC_FIELD_FONT_SIZE = new Font(fontName, PLAIN, fontSize + 4);
        }
    }
}
