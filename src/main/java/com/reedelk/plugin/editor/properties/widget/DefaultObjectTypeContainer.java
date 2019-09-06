package com.reedelk.plugin.editor.properties.widget;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders;
import static java.awt.BorderLayout.CENTER;

class DefaultObjectTypeContainer extends DisposablePanel {
    DefaultObjectTypeContainer(JComponent renderedComponent, String displayName) {
        Border outsideMargin = Borders.emptyTop(3);
        Border titledBorder = BorderFactory.createTitledBorder(displayName);
        Border outside = new CompoundBorder(outsideMargin, titledBorder);
        Border internalPadding = Borders.empty(3);
        setBorder(new CompoundBorder(outside, internalPadding));
        setLayout(new BorderLayout());
        add(renderedComponent, CENTER);
    }
}
