package de.codecentric.reedelk.plugin.editor.properties.commons;

import de.codecentric.reedelk.plugin.editor.properties.PropertiesPanelToolWindowFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import de.codecentric.reedelk.plugin.commons.Colors;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
import static de.codecentric.reedelk.runtime.api.commons.StringUtils.EMPTY;

public class EmptySelectionPanel extends DisposablePanel {

    public EmptySelectionPanel(@NotNull Project project) {
        setLayout(new GridBagLayout());
        setOpaque(false);

        ToolWindow toolWindow = ToolWindowManager
                .getInstance(project)
                .getToolWindow(PropertiesPanelToolWindowFactory.ID);

        toolWindow.setTitle(EMPTY);
        JLabel noSelectionLabel = new JLabel(message("properties.panel.nothing.selected"));
        noSelectionLabel.setForeground(Colors.FOREGROUND_TEXT);
        add(noSelectionLabel);
    }
}
