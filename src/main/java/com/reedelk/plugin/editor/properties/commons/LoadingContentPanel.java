package com.reedelk.plugin.editor.properties.commons;

import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;

import javax.swing.border.Border;
import java.awt.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class LoadingContentPanel extends DisposablePanel {

    static final Border BORDER_LOADING_CONTENT = JBUI.Borders.empty(5);

    public LoadingContentPanel() {
        super(new GridBagLayout());
        JBLabel loadingContentLabel = new JBLabel(message("message.loading.content"));
        loadingContentLabel.setFontColor(UIUtil.FontColor.BRIGHTER);
        loadingContentLabel.setBorder(BORDER_LOADING_CONTENT);
        add(loadingContentLabel);
    }
}
