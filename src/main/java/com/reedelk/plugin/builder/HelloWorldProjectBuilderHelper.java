package com.reedelk.plugin.builder;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.commons.DisableInspectionFor;
import com.reedelk.plugin.commons.ToolWindowUtils;
import com.reedelk.plugin.message.ReedelkBundle;
import com.reedelk.plugin.template.ConfigProperties;
import com.reedelk.plugin.template.FlowOrSubFlowFileProperties;
import com.reedelk.plugin.template.Template.HelloWorld;

import java.util.UUID;

import static com.reedelk.plugin.commons.DefaultConstants.PROJECT_RESOURCES_FOLDER;
import static com.reedelk.runtime.commons.ModuleProperties.*;

class HelloWorldProjectBuilderHelper extends AbstractProjectBuilderHelper {

    private final Project project;
    private final VirtualFile root;

    HelloWorldProjectBuilderHelper(Project project, VirtualFile root) {
        this.project = project;
        this.root = root;
    }

    void configure() {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            String configId = UUID.randomUUID().toString();

            // Script
            createDirectory(root, PROJECT_RESOURCES_FOLDER + Script.RESOURCE_DIRECTORY)
                    .flatMap(scriptDirectory -> createFromTemplate(project, HelloWorld.SCRIPT, scriptDirectory))
                    .ifPresent(virtualFile -> DisableInspectionFor.file(project, virtualFile));

            // Config
            createDirectory(root, PROJECT_RESOURCES_FOLDER + Config.RESOURCE_DIRECTORY).ifPresent(configDirectory -> {
                String title = ReedelkBundle.message("hello.world.sample.config.title");
                ConfigProperties configProperties = new ConfigProperties(configId, title);
                createFromTemplate(project, HelloWorld.CONFIG, configProperties, configDirectory);
            });

            // Flow
            createDirectory(root, PROJECT_RESOURCES_FOLDER + Flow.RESOURCE_DIRECTORY).ifPresent(flowsDir -> {
                String flowId = UUID.randomUUID().toString();
                String title = ReedelkBundle.message("hello.world.sample.flow.title");
                String description = ReedelkBundle.message("hello.world.sample.flow.description");
                FlowOrSubFlowFileProperties propertiesValues =
                        new FlowOrSubFlowFileProperties(flowId, title, description, configId);
                createFromTemplate(project, HelloWorld.FLOW, propertiesValues, flowsDir)
                        .ifPresent(virtualFile -> FileEditorManager.getInstance(project).openFile(virtualFile, true));
            });

            // Assets
            createDirectory(root, PROJECT_RESOURCES_FOLDER + Assets.RESOURCE_DIRECTORY);

            ToolWindowUtils.showComponentsPaletteToolWindow(project);
        });
    }
}
