package com.reedelk.plugin.editor.properties.renderer.typelist;

import com.intellij.ui.components.JBList;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.DisposableScrollPane;

import javax.swing.border.EmptyBorder;
import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders.empty;

public class ListScrollPane extends DisposablePanel {

    private static final Dimension DIMENSION = new Dimension(0, 65);
    private static final EmptyBorder BORDER = empty(0, 3);

    public ListScrollPane(JBList<Object> list) {
        DisposableScrollPane scrollPane = new DisposableScrollPane();
        scrollPane.setViewportView(list);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        setPreferredSize(DIMENSION);
        setBorder(BORDER);
    }
}
