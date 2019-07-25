package com.esb.plugin.editor.properties.widget.input.script.editor;

import java.awt.*;

public enum JavascriptEditorMode {

    POPUP {
        @Override
        public Dimension preferredSize() {
            return new Dimension(800, 400);
        }
    },

    DEFAULT {
        @Override
        public Dimension preferredSize() {
            return new Dimension(800, 150);
        }
    };

    public abstract Dimension preferredSize();

}
