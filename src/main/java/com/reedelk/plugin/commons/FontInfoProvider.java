package com.reedelk.plugin.commons;

import com.intellij.util.ui.JBUI;

public class FontInfoProvider {

    /**
     * This flag is used to AVOID initializing Java Fonts when tests
     * are running. Initializing the Font system while running the tests
     * slows down test execution and therefore, this flag can be used to
     * use a default and predefined font for test purposes only.
     */
    private static boolean TESTING = false;

    private static final int DEFAULT_TEST_FONT_SIZE = 13;
    private static final String DEFAULT_TEST_FONT_NAME = "Sans Serif";

    private static FontInfoProvider INSTANCE;

    private final String fontName;
    private final int fontSize;

    private FontInfoProvider() {
        if (TESTING) {
            fontName = DEFAULT_TEST_FONT_NAME;
            fontSize = DEFAULT_TEST_FONT_SIZE;
        } else {
            fontName = JBUI.Fonts.toolbarFont().getFontName();
            fontSize = JBUI.Fonts.toolbarFont().getSize();
        }
    }

    String fontName() {
        return fontName;
    }

    int fontSize() {
        return fontSize;
    }

    public static void testing() {
        TESTING = true;
    }

    static synchronized FontInfoProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FontInfoProvider();
        }
        return INSTANCE;
    }
}
