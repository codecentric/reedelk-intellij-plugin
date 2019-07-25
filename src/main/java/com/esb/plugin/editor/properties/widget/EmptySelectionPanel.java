package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.commons.Colors;
import com.esb.plugin.editor.properties.PropertiesPanelToolWindowFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class EmptySelectionPanel extends DisposablePanel {

    public EmptySelectionPanel(@NotNull Project project) {
        setLayout(new GridBagLayout());
        setBackground(Colors.PROPERTIES_EMPTY_SELECTION_BACKGROUND);

        ToolWindow toolWindow = ToolWindowManager
                .getInstance(project)
                .getToolWindow(PropertiesPanelToolWindowFactory.ID);

        toolWindow.setTitle("");
        JLabel noSelectionLabel = new JLabel("No selection");
        noSelectionLabel.setForeground(Colors.PROPERTIES_EMPTY_SELECTION_TEXT);
        add(noSelectionLabel);
    }
}
