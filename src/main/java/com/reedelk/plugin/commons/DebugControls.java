package com.reedelk.plugin.commons;

public class DebugControls {

    private static final boolean ALL_ENABLED = false;

    public static class Designer {

        private Designer() {
        }

        public static final boolean LOG_FLOW_INFO = ALL_ENABLED || false;
        public static final boolean SHOW_BOX = ALL_ENABLED || false;
        public static final boolean SHOW_CENTER = ALL_ENABLED || false;
        public static final boolean SHOW_COORDS = ALL_ENABLED || false;
        public static final boolean SHOW_HEIGHTS = ALL_ENABLED || false;
    }

    private DebugControls() {
    }
}
