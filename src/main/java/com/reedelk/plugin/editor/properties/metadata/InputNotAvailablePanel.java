package com.reedelk.plugin.editor.properties.metadata;

import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;

import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static com.reedelk.plugin.editor.properties.metadata.RendererUtils.htmlText;

public class InputNotAvailablePanel extends DisposablePanel {

    InputNotAvailablePanel(String text) {
        super(new BorderLayout());
        JBLabel label = new JBLabel(htmlText(text));
        add(label, BorderLayout.CENTER);
        setBorder(empty(2, 5));
    }
}
