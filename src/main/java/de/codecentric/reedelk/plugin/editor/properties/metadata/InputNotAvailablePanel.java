package de.codecentric.reedelk.plugin.editor.properties.metadata;

import de.codecentric.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.intellij.ui.components.JBLabel;

import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders.empty;

public class InputNotAvailablePanel extends DisposablePanel {

    InputNotAvailablePanel(String text) {
        super(new BorderLayout());
        JBLabel label = new JBLabel(RendererUtils.htmlText(text));
        add(label, BorderLayout.CENTER);
        setBorder(empty(2, 5));
    }
}
