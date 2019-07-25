package com.esb.plugin.editor.properties;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.editor.designer.ComponentSelectedListener;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

public class PropertiesPanelToolWindowFactory implements ToolWindowFactory, ComponentSelectedListener {

    public static final String ID = "componentPropertiesToolWindow";
    private final Application application;

    public PropertiesPanelToolWindowFactory(@NotNull Application application) {
        this.application = application;
    }

    @Override
    public void init(ToolWindow window) {
        window.setStripeTitle("Component Properties");
        window.setIcon(Icons.Component.DefaultComponentIcon);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JBPanel panel = new PropertiesPanel(project);

        final ContentFactory contentFactory = ServiceManager.getService(ContentFactory.class);
        final Content content = contentFactory.createContent(panel, "", false);

        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public void onComponentUnSelected() {

    }

    @Override
    public void onComponentSelected(Module module, FlowSnapshot snapshot, GraphNode selected) {

    }
}
