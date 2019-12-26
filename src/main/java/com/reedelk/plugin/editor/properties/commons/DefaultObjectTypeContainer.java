package com.reedelk.plugin.editor.properties.commons;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static java.awt.BorderLayout.CENTER;

public class DefaultObjectTypeContainer extends DisposablePanel {

    public static final Border BORDER_OBJECT_TYPE_CONTENT = empty(3, 10);

    DefaultObjectTypeContainer(JComponent renderedComponent, String displayName) {
        setBorder(BORDER_OBJECT_TYPE_CONTENT);
        setLayout(new BorderLayout());
        add(renderedComponent, CENTER);
    }
}
