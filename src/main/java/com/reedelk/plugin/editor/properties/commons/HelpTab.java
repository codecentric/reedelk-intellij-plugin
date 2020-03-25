package com.reedelk.plugin.editor.properties.commons;

import com.reedelk.plugin.component.ComponentData;

import javax.swing.*;
import java.awt.event.ComponentEvent;
import java.util.Optional;

import static com.intellij.util.ui.JBUI.Borders;
import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static java.lang.String.format;

public class HelpTab extends DisposableScrollPane {

    private static final String CONTENT_TYPE = "text/html";
    private static final String HTML_TEMPLATE =
            "<div style=\"color: #333333; padding-left:5px;padding-right:10px;font-family:verdana\">" +
                    "<h2>%s</h2>" +
                    "<p>%s</p>" +
                    "</div>";

    private boolean loaded = false;

    public HelpTab(ComponentData componentData) {
        createVerticalScrollBar();
        setBorder(Borders.empty());
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);

        final JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType(CONTENT_TYPE);
        setViewportView(editorPane);

        addComponentListener(new ComponentListenerAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // If the content was already loaded, there is no need to loading it again.
                if (loaded) return;

                // Lazy loading of Table content. We do it so that we can optimize the rendering
                // of the component's properties.
                SwingUtilities.invokeLater(() -> {

                    String componentDescription = Optional.ofNullable(componentData.getDescription()).orElse(EMPTY);

                    editorPane.setEditable(false);

                    editorPane.setText(format(HTML_TEMPLATE, componentData.getDisplayName(), componentDescription));

                    loaded = true;
                });
            }
        });
    }
}
