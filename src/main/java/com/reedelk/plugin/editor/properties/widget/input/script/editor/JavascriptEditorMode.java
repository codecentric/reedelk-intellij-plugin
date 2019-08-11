package com.reedelk.plugin.editor.properties.widget.input.script.editor;

import com.intellij.openapi.editor.ex.EditorEx;

import java.awt.*;

public enum JavascriptEditorMode {

    POPUP {
        @Override
        public Dimension preferredSize(EditorEx editor) {
            return new Dimension(1000, 600);
        }
    },

    DEFAULT {
        @Override
        public Dimension preferredSize(EditorEx editor) {
            return new Dimension(800, 150);
        }
    },

    INLINE {
        @Override
        public Dimension preferredSize(EditorEx editor) {
            return new Dimension(800, editor.getLineHeight() + 2);
        }
    };

    public abstract Dimension preferredSize(EditorEx editor);

}
