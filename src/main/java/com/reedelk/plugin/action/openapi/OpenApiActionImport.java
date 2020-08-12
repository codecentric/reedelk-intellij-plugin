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

        OpenApiImportDialog dialog = new OpenApiImportDialog(currentProject);
        if (dialog.showAndGet()) {
            doImport(currentProject, dialog);
        }
    }

    private void doImport(Project currentProject, OpenApiImportDialog dialog) {
        String openAPIFilePath = dialog.getOpenAPIFilePath();
        String importModule = dialog.getImportModule();
        String targetDirectory = dialog.getTargetDirectory();
        String urlField = dialog.getUrlField();
        OpenApiImporterContext context = new OpenApiImporterContext(currentProject, openAPIFilePath, importModule, targetDirectory, urlField);
        OpenApiImporter importer = new OpenApiImporter(context);
        try {
            importer.processImport();
        } catch (Exception exception) {
            exception.printStackTrace();
            ErrorDialogImport errorDialogImport = new ErrorDialogImport(currentProject);
            errorDialogImport.showAndGet();
        }
    }
}
