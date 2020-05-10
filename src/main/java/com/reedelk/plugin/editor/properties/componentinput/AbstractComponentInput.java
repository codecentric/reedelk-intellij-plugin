package com.reedelk.plugin.editor.properties.componentinput;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.DisposableScrollPane;
import com.reedelk.plugin.service.module.impl.component.componentio.IOComponent;
import com.reedelk.plugin.service.module.impl.component.componentio.OnComponentIO;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;
import java.awt.*;

abstract class AbstractComponentInput extends DisposableScrollPane implements OnComponentIO {

    protected static final int LEFT_OFFSET = 24;

    public AbstractComponentInput() {
        setBorder(JBUI.Borders.empty(0, 1, 0, 8));
        setBackground(JBColor.WHITE);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    @Override
    public void onComponentIO(String inputFQCN, String outputFQCN, IOComponent IOComponent) {
        DisposablePanel theContent = new DisposablePanel(new GridBagLayout());
        theContent.setBackground(JBColor.WHITE);
        DisposablePanel panel = ContainerFactory.pushTop(theContent);
        panel.setBackground(JBColor.WHITE);
        panel.setBorder(JBUI.Borders.empty(5, 2));

        render(IOComponent, theContent);
        setViewportView(panel);
    }

    abstract void render(IOComponent IOComponent, DisposablePanel parent);

    private static final String HTML_WITH_VALUE = "<html>%s : <i>%s</i></html>";
    private static final String HTML_WITHOUT_VALUE = "<html>%s</html>";

    protected static String htmlLabel(String key, String value) {
        return htmlLabel(key, value, true);
    }

    protected static String htmlLabel(String key, String value, boolean escape) {
        if (StringUtils.isBlank(value)) {
            if (escape) {
                key = key.replaceAll("<", "&lt;");
                key = key.replaceAll(">", "&gt;");
            }
            return String.format(HTML_WITHOUT_VALUE, key);
        } else {
            if (escape) {
                value = value.replaceAll("<", "&lt;");
                value = value.replaceAll(">", "&gt;");
            }
            return String.format(HTML_WITH_VALUE, key, value);
        }
    }
}
