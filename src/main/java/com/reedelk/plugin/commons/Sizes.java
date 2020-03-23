package com.reedelk.plugin.commons;

import java.awt.*;

public class Sizes {

    private Sizes() {
    }

    public static class Table {
        private Table() {
        }

        public static final int ROW_HEIGHT = 23;

        public static final Dimension ROUTER = new Dimension(0, 150);
        public static final Dimension TABBED = new Dimension(0, 170);
        public static final Dimension DEFAULT = new Dimension(0, 100);
    }

    public static class TabbedPane {
        private TabbedPane() {
        }
        public static final Dimension TAB_LABEL = new Dimension(130, 15);
        public static final Dimension HEIGHT_TOP_PLACEMENT = new Dimension(0, 200);
    }

}
