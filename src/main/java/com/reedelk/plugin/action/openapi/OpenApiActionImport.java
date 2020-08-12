package com.reedelk.plugin.action.openapi;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class OpenApiActionImport extends AnAction {

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

        OpenApiDialogSelectFile dialog = new OpenApiDialogSelectFile(currentProject);
        if (dialog.showAndGet()) {
            doImport(currentProject, dialog);
        }
    }

    private void doImport(Project currentProject, OpenApiDialogSelectFile dialog) {
        String openAPIFilePath = dialog.getOpenAPIFilePath();
        String importModule = dialog.getImportModule();
        String targetDirectory = dialog.getTargetDirectory();
        OpenApiImporterContext context = new OpenApiImporterContext(currentProject, openAPIFilePath, importModule, targetDirectory);
        OpenApiImporter importer = new OpenApiImporter(context);
        try {
            importer.processImport();
        } catch (Exception exception) {
            ErrorDialogImport errorDialogImport = new ErrorDialogImport(currentProject);
            errorDialogImport.showAndGet();
        }
    }
}
