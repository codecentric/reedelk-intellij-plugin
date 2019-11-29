package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.editor.properties.PropertiesPanelToolWindowFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;

public class EmptySelectionPanel extends DisposablePanel {

    public EmptySelectionPanel(@NotNull Project project) {
        setLayout(new GridBagLayout());
        setBackground(Colors.PROPERTIES_EMPTY_SELECTION_BACKGROUND);

        ToolWindow toolWindow = ToolWindowManager
                .getInstance(project)
                .getToolWindow(PropertiesPanelToolWindowFactory.ID);

        toolWindow.setTitle(EMPTY);
        JLabel noSelectionLabel = new JLabel(message("properties.panel.nothing.selected"));
        noSelectionLabel.setForeground(Colors.PROPERTIES_EMPTY_SELECTION_TEXT);
        add(noSelectionLabel);
    }
}
