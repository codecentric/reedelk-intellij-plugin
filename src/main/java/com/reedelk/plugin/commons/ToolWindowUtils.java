package com.reedelk.plugin.commons;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.reedelk.plugin.editor.properties.PropertiesPanelToolWindowFactory;

public class ToolWindowUtils {

    public static class ComponentProperties {

        private ComponentProperties() {
        }

        public static void show(Project project) {
            ToolWindow toolWindow = get(project);
            if (!toolWindow.isVisible()) {
                toolWindow.show(() -> {
                });
            }
        }

        public static ToolWindow get(Project project) {
            return ToolWindowManager
                    .getInstance(project)
                    .getToolWindow(PropertiesPanelToolWindowFactory.ID);
        }
    }
}
