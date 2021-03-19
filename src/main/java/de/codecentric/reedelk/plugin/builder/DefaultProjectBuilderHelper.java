package de.codecentric.reedelk.plugin.builder;

import de.codecentric.reedelk.plugin.commons.FileUtils;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import de.codecentric.reedelk.plugin.commons.DefaultConstants;
import de.codecentric.reedelk.plugin.commons.DisableInspectionFor;
import de.codecentric.reedelk.plugin.commons.ToolWindowUtils;
import de.codecentric.reedelk.plugin.message.ReedelkBundle;
import de.codecentric.reedelk.plugin.template.DockerfileProperties;
import de.codecentric.reedelk.plugin.template.FlowOrSubFlowFileProperties;
import de.codecentric.reedelk.plugin.template.RESTListenerConfigProperties;
import de.codecentric.reedelk.plugin.template.Template;
import de.codecentric.reedelk.plugin.template.Template.HelloWorld;

import java.util.UUID;

import static de.codecentric.reedelk.plugin.commons.DefaultConstants.PROJECT_RESOURCES_FOLDER;
import static de.codecentric.reedelk.plugin.commons.FileUtils.createDirectory;
import static de.codecentric.reedelk.runtime.commons.ModuleProperties.*;

class DefaultProjectBuilderHelper {

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
            FileUtils.createDirectory(root, PROJECT_RESOURCES_FOLDER + Script.RESOURCE_DIRECTORY)
                    .flatMap(scriptDirectory -> Template.HelloWorld.SCRIPT.create(project, scriptDirectory))
                    .ifPresent(virtualFile -> DisableInspectionFor.file(project, virtualFile));

            // Config
            FileUtils.createDirectory(root, PROJECT_RESOURCES_FOLDER + Config.RESOURCE_DIRECTORY).ifPresent(configDirectory -> {
                String title = ReedelkBundle.message("hello.world.sample.config.title");
                RESTListenerConfigProperties properties = new RESTListenerConfigProperties();
                properties.setId(configId);
                properties.setTitle(title);
                properties.setHost(DefaultConstants.Template.DEFAULT_HOST);
                properties.setPort(DefaultConstants.Template.DEFAULT_PORT);
                Template.HelloWorld.CONFIG.create(project, properties, configDirectory);
            });

            // Flows
            FileUtils.createDirectory(root, PROJECT_RESOURCES_FOLDER + Flow.RESOURCE_DIRECTORY).ifPresent(flowsDir -> {
                // GET Hello World Flow
                createGETHelloWorldFlow(configId, flowsDir);

                // POST Hello World Flow
                createPOSTHelloWorldFlow(configId, flowsDir);
            });

            // Assets
            FileUtils.createDirectory(root, PROJECT_RESOURCES_FOLDER + Assets.RESOURCE_DIRECTORY);

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
