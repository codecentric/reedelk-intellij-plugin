package com.reedelk.plugin.commons;

public class DebugControls {

    private static final boolean ALL_ENABLED = false;

    public static class Designer {
        public static boolean SHOW_BOX = ALL_ENABLED || false;
        public static boolean SHOW_CENTER = ALL_ENABLED || false;
        public static boolean SHOW_COORDS = ALL_ENABLED || false;
        public static boolean SHOW_HEIGHTS = ALL_ENABLED || false;
    }

    private DebugControls() {
    }
}
