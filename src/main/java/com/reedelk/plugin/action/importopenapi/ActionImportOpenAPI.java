package com.reedelk.plugin.action.importopenapi;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.action.importopenapi.handler.POSTHandler;
import io.swagger.v3.oas.models.PathItem;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public class ActionImportOpenAPI extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        // Enable / Disable action (this is always enabled)
        Project project = event.getProject();
        event.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        Project currentProject = event.getProject();

        DialogSelectOpenAPI dialog = new DialogSelectOpenAPI(currentProject);
        boolean result = dialog.showAndGet();
        if (result) {
            String openAPIFilePath = dialog.getOpenAPIFilePath();
            ParserOpenAPI parserOpenAPI = new ParserOpenAPI(openAPIFilePath);
            parserOpenAPI.parse(new BiConsumer<String, PathItem>() {
                @Override
                public void accept(String pathEntry, PathItem pathItem) {
                    new POSTHandler().accept(currentProject, pathEntry, pathItem);
                }
            });
            System.out.println(openAPIFilePath);
        }
    }
}
