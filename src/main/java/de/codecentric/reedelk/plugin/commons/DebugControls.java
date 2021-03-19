package de.codecentric.reedelk.plugin.commons;

public class DebugControls {

    public static class Designer {

        private Designer() {
        }

        public static final boolean LOG_FLOW_INFO = false;
        public static final boolean SHOW_BOX = false;
        public static final boolean SHOW_CENTER = false;
        public static final boolean SHOW_COORDS = false;
        public static final boolean SHOW_HEIGHTS = false;
    }

    public static class Properties {

        private Properties() {
        }

        public static final boolean CONTAINER_CONTEXT_INFO = false;
    }

    public static class Script {

        private Script() {
        }

        public static final boolean SCRIPT_EDITOR_LIFECYCLE = false;
    }

    private DebugControls() {
    }
}
