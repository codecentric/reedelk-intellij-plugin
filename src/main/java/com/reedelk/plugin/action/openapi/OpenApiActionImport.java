package com.reedelk.plugin.action.openapi;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.reedelk.plugin.action.openapi.dialog.DialogImport;
import com.reedelk.plugin.action.openapi.dialog.DialogImportError;
import com.reedelk.plugin.action.openapi.dialog.DialogImportSuccess;
import org.jetbrains.annotations.NotNull;

import static com.reedelk.plugin.message.ReedelkBundle.message;

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

        DialogImport dialog = new DialogImport(currentProject);
        if (dialog.showAndGet()) {
            doImport(currentProject, dialog);
        }
    }

    private void doImport(Project currentProject, DialogImport importDialog) {
        String targetDirectory = importDialog.getTargetDirectory();
        String openAPIFilePath = importDialog.getOpenApiFile();
        String importModule = importDialog.getImportModule();
        String openApiBasePath = importDialog.getBasePath();
        String openApiURL = importDialog.getOpenApiURL();
        Integer openApiPort = importDialog.getOpenApiPort();

        OpenApiImporterContext context = new OpenApiImporterContext(
                currentProject,
                openAPIFilePath,
                importModule,
                targetDirectory,
                openApiURL,
                openApiPort,
                openApiBasePath);
        OpenApiImporter importer = new OpenApiImporter(context);

        try {
            importer.processImport();
            String apiTitle = importer.getApiTitle();
            String host = importer.getHost();
            int port = importer.getPort();
            String basePath = importer.getBasePath();

            DialogImportSuccess successDialog =
                    new DialogImportSuccess(currentProject,
                            message("openapi.importer.dialog.success.message.details",
                                    apiTitle, host, String.valueOf(port), basePath));
            successDialog.show();

        } catch (Exception exception) {
            String cause = exception.getMessage();
            DialogImportError errorDialog = new DialogImportError(currentProject, cause);
            errorDialog.showAndGet();
        }
    }
}
