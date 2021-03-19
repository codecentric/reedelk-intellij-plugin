package de.codecentric.reedelk.plugin.editor.properties.metadata;

import de.codecentric.reedelk.plugin.editor.properties.commons.DisposablePanel;
import de.codecentric.reedelk.plugin.editor.properties.commons.TypeObjectContainerHeader;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;

public class MetadataPanelRootEntry extends DisposablePanel {

    public MetadataPanelRootEntry(String title, JComponent content) {
        super(new BorderLayout());
        TypeObjectContainerHeader.HorizontalSeparator horizontalSeparator = new TypeObjectContainerHeader.HorizontalSeparator();
        horizontalSeparator.setOpaque(false);

        setBorder(JBUI.Borders.empty(2, 5));
        setBackground(JBColor.WHITE);
        add(new JBLabel(title), BorderLayout.WEST);
        add(horizontalSeparator, CENTER);
        add(content, SOUTH);
    }
}
