package com.esb.plugin.commons;

import java.awt.*;

public class Fonts {

    private static final String FONT_NAME = ".SF NS Text";

    public static class Component {
        public static final Font TITLE = new Font(FONT_NAME, Font.PLAIN, 13);
        public static final Font DESCRIPTION = new Font(FONT_NAME, Font.PLAIN, 13);
        public static final Font INBOUND = new Font(FONT_NAME, Font.PLAIN, 20);
    }

    public static class Flow {
        public static final Font TITLE = new Font(FONT_NAME, Font.PLAIN, 20);
        public static final Font DESCRIPTION = new Font(FONT_NAME, Font.PLAIN, 13);
    }
}
