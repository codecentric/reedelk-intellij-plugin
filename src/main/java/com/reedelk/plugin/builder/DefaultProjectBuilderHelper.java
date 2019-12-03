package com.reedelk.plugin.builder;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.message.ReedelkBundle;
import com.reedelk.plugin.template.ConfigProperties;
import com.reedelk.plugin.template.FlowOrSubFlowFileProperties;
import com.reedelk.plugin.template.Template.HelloWorld;

import java.util.UUID;

import static com.reedelk.plugin.commons.Defaults.BASE_RESOURCE_FOLDER;
import static com.reedelk.runtime.commons.ModuleProperties.*;

public class DefaultProjectBuilderHelper extends AbstractProjectBuilderHelper {

    private final Project project;
    private final VirtualFile root;

    DefaultProjectBuilderHelper(Project project, VirtualFile root) {
        this.project = project;
        this.root = root;
    }

    public void run() {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            String configId = UUID.randomUUID().toString();

            // Script
            createDirectory(root, BASE_RESOURCE_FOLDER + Script.RESOURCE_DIRECTORY).ifPresent(scriptDirectory ->
                    createFromTemplate(project, HelloWorld.SCRIPT, scriptDirectory));

            // Config
            createDirectory(root, BASE_RESOURCE_FOLDER + Config.RESOURCE_DIRECTORY).ifPresent(configDirectory -> {
                String title = ReedelkBundle.message("hello.world.sample.config.title");
                ConfigProperties configProperties = new ConfigProperties(configId, title);
                createFromTemplate(project, HelloWorld.CONFIG, configProperties, configDirectory);
            });

            // Flow
            createDirectory(root, BASE_RESOURCE_FOLDER + Flow.RESOURCE_DIRECTORY).ifPresent(flowsDir -> {
                String flowId = UUID.randomUUID().toString();
                String title = ReedelkBundle.message("hello.world.sample.flow.title");
                String description = ReedelkBundle.message("hello.world.sample.flow.description");
                FlowOrSubFlowFileProperties propertiesValues =
                        new FlowOrSubFlowFileProperties(flowId, title, description, configId);
                createFromTemplate(project, HelloWorld.FLOW, propertiesValues, flowsDir);
            });
        });
    }
}
