package com.reedelk.plugin.commons;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.reedelk.plugin.editor.properties.PropertiesPanelToolWindowFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Optional;

import static java.util.Arrays.stream;

public class ToolWindowUtils {

    private ToolWindowUtils() {
    }

    public static void switchToolWindowAndNotifyWithMessage(Project project, String message, String runConfigName) {
        SwingUtilities.invokeLater(() ->
                findToolWindowByRunConfig(project, runConfigName).ifPresent(toolWindow -> {
                    getContentBy(toolWindow.toolWindow, runConfigName).ifPresent(content ->
                            toolWindow.toolWindow.getContentManager().setSelectedContent(content));
                    toolWindow.toolWindow.show(() ->
                            NotificationUtils.notifyInfo(toolWindow.toolWindowId, message, project));
                }));
    }

    private static Optional<ToolWindowAndId> findToolWindowByRunConfig(@NotNull Project project, @NotNull String runConfigName) {

        Optional<Content> contentInRunToolWindow = get(project, ToolWindowId.RUN)
                .flatMap(toolWindow -> getContentBy(toolWindow, runConfigName));
        if (contentInRunToolWindow.isPresent()) return get(project, ToolWindowId.RUN)
                .map(toolWindow -> new ToolWindowAndId(toolWindow, ToolWindowId.RUN));

        Optional<Content> contentInDebugToolWindow = get(project, ToolWindowId.DEBUG)
                .flatMap(toolWindow -> getContentBy(toolWindow, runConfigName));
        if (contentInDebugToolWindow.isPresent()) return get(project, ToolWindowId.DEBUG)
                .map(toolWindow -> new ToolWindowAndId(toolWindow, ToolWindowId.DEBUG));

        return Optional.empty();
    }

    static class ToolWindowAndId {
        ToolWindow toolWindow;
        String toolWindowId;

        ToolWindowAndId(ToolWindow toolWindow, String toolWindowId) {
            this.toolWindow = toolWindow;
            this.toolWindowId = toolWindowId;
        }
    }

    private static Optional<Content> getContentBy(ToolWindow toolWindow, @NotNull String contentDisplayName) {
        return stream(toolWindow.getContentManager().getContents())
                .filter(content -> contentDisplayName.equals(content.getDisplayName())).findFirst();
    }

    public static void show(Project project) {
        ToolWindow toolWindow = get(project);
        if (!toolWindow.isVisible()) {
            toolWindow.show(() -> {
            });
        }
    }

    public static Optional<ToolWindow> get(Project project, String id) {
        return Optional.ofNullable(ToolWindowManager.getInstance(project).getToolWindow(id));
    }

    public static ToolWindow get(Project project) {
        return ToolWindowManager
                .getInstance(project)
                .getToolWindow(PropertiesPanelToolWindowFactory.ID);
    }
}
