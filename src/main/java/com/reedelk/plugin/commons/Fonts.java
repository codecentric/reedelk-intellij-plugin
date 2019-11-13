package com.reedelk.plugin.commons;

import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

import static java.awt.Font.PLAIN;

public class Fonts {

    private static final String FONT_NAME = JBUI.Fonts.toolbarFont().getFontName();

    private Fonts() {
    }

    public static class Component {

        private Component() {
        }

        public static final Font TITLE = new Font(FONT_NAME, PLAIN, 13);
        public static final Font DESCRIPTION = new Font(FONT_NAME, PLAIN, 13);
        public static final Font INBOUND = new Font(FONT_NAME, PLAIN, 20);
    }

    public static class Flow {

        private Flow() {
        }

        public static final Font TITLE = new Font(FONT_NAME, PLAIN, 20);
        public static final Font DESCRIPTION = new Font(FONT_NAME, PLAIN, 13);
    }

    public static class Router {

        private Router() {
        }

        public static final Font DEFAULT_ROUTE = new Font(FONT_NAME, PLAIN, 11);
    }

    public static class ScriptEditor {
        private ScriptEditor() {
        }

        public static final int SCRIPT_EDITOR_FONT_SIZE;
        public static final Font DYNAMIC_FIELD_FONT_SIZE;

        static {
            Font targetFont = new JLabel().getFont();
            SCRIPT_EDITOR_FONT_SIZE = targetFont.getSize() + 4;
            DYNAMIC_FIELD_FONT_SIZE = new Font(targetFont.getName(), PLAIN, targetFont.getSize());
        }
    }
}
