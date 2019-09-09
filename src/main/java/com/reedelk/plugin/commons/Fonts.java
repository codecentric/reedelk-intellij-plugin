package com.reedelk.plugin.commons;

import java.awt.*;

public class Fonts {

    private static final String FONT_NAME = ".SF NS Text";

    private Fonts() {
    }

    public static class Component {

        private Component() {
        }

        public static final Font TITLE = new Font(FONT_NAME, Font.PLAIN, 13);
        public static final Font DESCRIPTION = new Font(FONT_NAME, Font.PLAIN, 13);
        public static final Font INBOUND = new Font(FONT_NAME, Font.PLAIN, 20);
    }

    public static class Flow {

        private Flow() {
        }

        public static final Font TITLE = new Font(FONT_NAME, Font.PLAIN, 20);
        public static final Font DESCRIPTION = new Font(FONT_NAME, Font.PLAIN, 13);
    }

    public static class ScriptEditor {
        private ScriptEditor() {
        }

        public static final Font SCRIPT_EDITOR = new Font(FONT_NAME, Font.PLAIN, 16);
    }
}
