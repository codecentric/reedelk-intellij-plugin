package com.reedelk.plugin.editor.properties.commons;

import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.commons.Colors;

import javax.swing.border.Border;
import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public abstract class PanelWithText extends DisposablePanel {

    static final Border BORDER = Borders.empty(5);

    protected PanelWithText(String text) {
        super(new GridBagLayout());
        JBLabel loadingContentLabel = new JBLabel(text);
        loadingContentLabel.setForeground(Colors.FOREGROUND_TEXT);
        loadingContentLabel.setBorder(BORDER);
        add(loadingContentLabel);
    }

    public static class LoadingContentPanel extends PanelWithText {

        public LoadingContentPanel() {
            super(message("message.loading.content"));
        }
    }

    public static class NoPropertiesPanel extends PanelWithText {

        public NoPropertiesPanel() {
            super(message("message.no.properties"));
        }
    }
}
