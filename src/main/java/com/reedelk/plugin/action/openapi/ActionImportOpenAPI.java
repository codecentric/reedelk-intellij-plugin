package com.reedelk.plugin.action.openapi;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ActionImportOpenAPI extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        // The import open API action is visible only when the project is open.
        Project project = event.getProject();
        event.getPresentation().setEnabledAndVisible(project != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        Project currentProject = event.getProject();
        if (currentProject == null) return;


        DialogSelectOpenAPI dialog = new DialogSelectOpenAPI(currentProject);
        boolean result = dialog.showAndGet();

        if (result) {
            ImporterOpenAPIContext context = new ImporterOpenAPIContext(currentProject);
            String openAPIFilePath = dialog.getOpenAPIFilePath();
            ImporterOpenAPI importer = new ImporterOpenAPI(context, openAPIFilePath);
            importer.process();
        }
    }
}
