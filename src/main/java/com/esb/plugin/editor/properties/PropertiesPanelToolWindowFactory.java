package com.esb.plugin.editor.properties;

import com.esb.plugin.commons.Icons;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class PropertiesPanelToolWindowFactory implements ToolWindowFactory {

    public static final String ID = "componentPropertiesToolWindow";

    @Override
    public void init(ToolWindow window) {
        window.setStripeTitle("Properties");
        window.setIcon(Icons.Component.DefaultComponentIcon);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JBPanel panel = new PropertiesPanel(project);

        final ContentFactory contentFactory = ServiceManager.getService(ContentFactory.class);
        final Content content = contentFactory.createContent(panel, "", false);

        toolWindow.getContentManager().addContent(content);
    }
}
