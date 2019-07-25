package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.editor.properties.PropertiesPanelToolWindowFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class EmptySelectionPanel extends DisposablePanel {

    public EmptySelectionPanel(@NotNull Project project) {
        setBackground(new JBColor(new Color(237, 237, 237), new Color(237, 237, 237)));
        setLayout(new GridBagLayout());

        ToolWindow toolWindow = ToolWindowManager
                .getInstance(project)
                .getToolWindow(PropertiesPanelToolWindowFactory.ID);

        toolWindow.setTitle("");
        JLabel noSelectionLabel = new JLabel("No selection");
        noSelectionLabel.setForeground(new Color(153, 153, 153));
        add(noSelectionLabel);
    }
}
