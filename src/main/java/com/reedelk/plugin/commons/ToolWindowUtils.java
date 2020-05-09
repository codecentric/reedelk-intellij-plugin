package com.reedelk.plugin.commons;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.reedelk.plugin.editor.palette.PaletteToolWindowFactory;
import com.reedelk.plugin.editor.properties.PropertiesPanelToolWindowFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static java.util.Arrays.stream;

public class ToolWindowUtils {

    private static final Runnable EMPTY_POST_SHOW_ACTION = () -> {
    };

    private ToolWindowUtils() {
    }

    public static void notifyInfoWithoutShowing(Project project, String message, String runConfigName) {
        ApplicationManager.getApplication().invokeLater(() ->
                findToolWindowByRunConfig(project, runConfigName).ifPresent(toolWindow ->
                        notifyInfoBalloon(toolWindow.toolWindowId, message, project)));
    }

    public static void notifyInfo(Project project, String message, String runConfigName) {
        ApplicationManager.getApplication().invokeLater(() ->
                findToolWindowByRunConfig(project, runConfigName).ifPresent(toolWindow -> {
                    getContentBy(toolWindow.toolWindow, runConfigName).ifPresent(content ->
                            toolWindow.toolWindow.getContentManager().setSelectedContent(content));
                    toolWindow.toolWindow.show(() -> notifyInfoBalloon(toolWindow.toolWindowId, message, project));
                }));
    }

    public static void notifyError(Project project, String message, String runConfigName) {
        ApplicationManager.getApplication().invokeLater(() ->
                findToolWindowByRunConfig(project, runConfigName).ifPresent(toolWindow -> {
                    getContentBy(toolWindow.toolWindow, runConfigName).ifPresent(content ->
                            toolWindow.toolWindow.getContentManager().setSelectedContent(content));
                    toolWindow.toolWindow.show(() -> notifyErrorBalloon(toolWindow.toolWindowId, message, project));
                }));
    }

    public static void setPropertiesPanelToolWindowTitle(Project project, String newToolWindowTitle) {
        get(project, PropertiesPanelToolWindowFactory.ID).ifPresent(toolWindow -> toolWindow.setTitle(newToolWindowTitle));
    }

    public static void showPropertiesPanelToolWindow(Project project, Runnable postAction) {
        get(project, PropertiesPanelToolWindowFactory.ID).ifPresent(toolWindow ->
                toolWindow.show(postAction));
    }

    public static void showComponentsPaletteToolWindow(Project project) {
        get(project, PaletteToolWindowFactory.ID).ifPresent(toolWindow ->
                toolWindow.show(EMPTY_POST_SHOW_ACTION));
    }

    public static Optional<ToolWindow> get(Project project, String id) {
        return Optional.ofNullable(ToolWindowManager.getInstance(project).getToolWindow(id));
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

        return ToolWindowUtils.get(project, ToolWindowId.RUN).flatMap(toolWindow ->
                Optional.of(new ToolWindowAndId(toolWindow, ToolWindowId.RUN))); // Default tool window is Run.
    }

    private static Optional<Content> getContentBy(ToolWindow toolWindow, @NotNull String contentDisplayName) {
        return stream(toolWindow.getContentManager().getContents())
                .filter(content -> contentDisplayName.equals(content.getDisplayName())).findFirst();
    }

    public static void notifyErrorBalloon(final String toolWindowId, final String text, final Project project) {
        ToolWindowManager.getInstance(project)
                .notifyByBalloon(toolWindowId, MessageType.ERROR, StringUtil.notNullize(text, "internal error"));
    }


    private static void notifyInfoBalloon(final String toolWindowId, final String text, final Project project) {
        ToolWindowManager.getInstance(project)
                .notifyByBalloon(toolWindowId, MessageType.INFO, text);
    }

    static class ToolWindowAndId {
        ToolWindow toolWindow;
        String toolWindowId;

        ToolWindowAndId(ToolWindow toolWindow, String toolWindowId) {
            this.toolWindow = toolWindow;
            this.toolWindowId = toolWindowId;
        }
    }
}
