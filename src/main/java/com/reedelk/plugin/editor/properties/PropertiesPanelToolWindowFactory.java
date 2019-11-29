package com.reedelk.plugin.editor.properties;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.reedelk.plugin.commons.Icons;
import org.jetbrains.annotations.NotNull;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class PropertiesPanelToolWindowFactory implements ToolWindowFactory {

    public static final String ID = "componentPropertiesToolWindow";

    @Override
    public void init(ToolWindow window) {
        window.setStripeTitle(message("toolwindow.flow.properties.title"));
        window.setIcon(Icons.ToolWindow.Properties);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        final JBPanel panel = new PropertiesPanel(project);
        final ContentFactory contentFactory = ServiceManager.getService(ContentFactory.class);
        final Content content = contentFactory.createContent(panel, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
