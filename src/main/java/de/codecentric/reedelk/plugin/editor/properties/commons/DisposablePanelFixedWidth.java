package de.codecentric.reedelk.plugin.editor.properties.commons;

import javax.swing.*;
import java.awt.*;

public class DisposablePanelFixedWidth extends DisposablePanel {

    private final int width;

    public DisposablePanelFixedWidth(JComponent decorated, int width) {
        setLayout(new BorderLayout());
        this.width = width;
        add(decorated, BorderLayout.CENTER);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension preferredSize = super.getPreferredSize();
        if (preferredSize.width > width) {
            preferredSize.width = width;
        }
        return preferredSize;
    }
}
