package com.reedelk.plugin.builder;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.commons.DisableInspectionFor;
import com.reedelk.plugin.commons.ToolWindowUtils;
import com.reedelk.plugin.message.ReedelkBundle;
import com.reedelk.plugin.template.ConfigProperties;
import com.reedelk.plugin.template.DockerfileProperties;
import com.reedelk.plugin.template.FlowOrSubFlowFileProperties;
import com.reedelk.plugin.template.Template;
import com.reedelk.plugin.template.Template.HelloWorld;

import java.util.UUID;

import static com.reedelk.plugin.commons.DefaultConstants.PROJECT_RESOURCES_FOLDER;
import static com.reedelk.runtime.commons.ModuleProperties.*;

class DefaultProjectBuilderHelper extends AbstractProjectBuilderHelper {

    private final boolean isDownloadedDistribution;
    private final String reedelkRuntimeVersion;
    private final VirtualFile root;
    private final Project project;

    DefaultProjectBuilderHelper(Project project, VirtualFile root, String reedelkRuntimeVersion, boolean isDownloadedDistribution) {
        this.isDownloadedDistribution = isDownloadedDistribution;
        this.reedelkRuntimeVersion = reedelkRuntimeVersion;
        this.project = project;
        this.root = root;
    }

    void build() {

        WriteCommandAction.runWriteCommandAction(project, () -> {

            String configId = UUID.randomUUID().toString();

            // Script
            createDirectory(root, PROJECT_RESOURCES_FOLDER + Script.RESOURCE_DIRECTORY)
                    .flatMap(scriptDirectory -> Template.HelloWorld.SCRIPT.create(project, scriptDirectory))
                    .ifPresent(virtualFile -> DisableInspectionFor.file(project, virtualFile));

            // Config
            createDirectory(root, PROJECT_RESOURCES_FOLDER + Config.RESOURCE_DIRECTORY).ifPresent(configDirectory -> {
                String title = ReedelkBundle.message("hello.world.sample.config.title");
                ConfigProperties configProperties = new ConfigProperties(configId, title);
                Template.HelloWorld.CONFIG.create(project, configProperties, configDirectory);
            });

            // Flows
            createDirectory(root, PROJECT_RESOURCES_FOLDER + Flow.RESOURCE_DIRECTORY).ifPresent(flowsDir -> {
                // GET Hello World Flow
                createGETHelloWorldFlow(configId, flowsDir);

                // POST Hello World Flow
                createPOSTHelloWorldFlow(configId, flowsDir);
            });

            // Assets
            createDirectory(root, PROJECT_RESOURCES_FOLDER + Assets.RESOURCE_DIRECTORY);

            // Add .gitignore
            Template.HelloWorld.GIT_IGNORE.create(project, root);

            // Add Dockerfile
            DockerfileProperties dockerfileProperties = new DockerfileProperties(reedelkRuntimeVersion);
            Template.HelloWorld.DOCKERFILE.create(project, dockerfileProperties, root, "Dockerfile");

            if (isDownloadedDistribution) {
                // Add Heroku Proc File
                Template.HelloWorld.HEROKU_PROCFILE.create(project, root, "Procfile");
            }

            ToolWindowUtils.showComponentsPaletteToolWindow(project);
        });
    }

    private void createPOSTHelloWorldFlow(String configId, VirtualFile flowsDir) {
        String flowId = UUID.randomUUID().toString();
        String title = ReedelkBundle.message("hello.world.post.sample.flow.title");
        String description = ReedelkBundle.message("hello.world.post.sample.flow.description");
        FlowOrSubFlowFileProperties propertiesValues =
                new FlowOrSubFlowFileProperties(flowId, title, description, configId);
        HelloWorld.POST_FLOW.create(project, propertiesValues, flowsDir)
                .ifPresent(virtualFile -> FileEditorManager.getInstance(project).openFile(virtualFile, true));
    }

    private void createGETHelloWorldFlow(String configId, VirtualFile flowsDir) {
        String flowId = UUID.randomUUID().toString();
        String title = ReedelkBundle.message("hello.world.get.sample.flow.title");
        String description = ReedelkBundle.message("hello.world.get.sample.flow.description");
        FlowOrSubFlowFileProperties propertiesValues =
                new FlowOrSubFlowFileProperties(flowId, title, description, configId);
        Template.HelloWorld.GET_FLOW.create(project, propertiesValues, flowsDir)
                .ifPresent(virtualFile -> FileEditorManager.getInstance(project).openFile(virtualFile, true));
    }
}
