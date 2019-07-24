package com.esb.plugin.editor.properties.widget.input.script;

import com.esb.plugin.commons.Colors;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public enum JavascriptEditorMode {

    POPUP {
        @Override
        public Dimension preferredSize() {
            return new Dimension(800, 400);
        }

        @Override
        public Border border() {
            return JBUI.Borders.empty();
        }
    },

    DEFAULT {
        @Override
        public Dimension preferredSize() {
            return new Dimension(800, 150);
        }

        @Override
        public Border border() {
            return BorderFactory.createMatteBorder(1, 1, 1, 1, Colors.SCRIPT_EDITOR_BORDER);
        }
    };

    public abstract Dimension preferredSize();

    public abstract Border border();
}
