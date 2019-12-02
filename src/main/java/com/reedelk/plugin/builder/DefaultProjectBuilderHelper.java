package com.reedelk.plugin.builder;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.template.ConfigProperties;
import com.reedelk.plugin.template.FlowOrSubFlowFileProperties;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.commons.ModuleProperties.*;

public class DefaultProjectBuilderHelper {

    private static final Logger LOG = Logger.getInstance(MavenProjectBuilderHelper.class);
    private static final String BASE_RESOURCE_FOLDER = "/src/main/resources";
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
                    createFromTemplate("HelloWorld.js", scriptDirectory));

            // Config
            createDirectory(root, BASE_RESOURCE_FOLDER + Config.RESOURCE_DIRECTORY).ifPresent(configDirectory -> {
                ConfigProperties configProperties = new ConfigProperties(configId, "Hello World Listener");
                createFromTemplate("DefaultListener.fconfig", configProperties, configDirectory);
            });

            // Flow
            createDirectory(root, BASE_RESOURCE_FOLDER + Flow.RESOURCE_DIRECTORY).ifPresent(flowsDir -> {
                String flowId = UUID.randomUUID().toString();
                String title = "GET Hello world flow";
                String description = "Returns 'Hello world' text from GET /api/message";
                FlowOrSubFlowFileProperties propertiesValues =
                        new FlowOrSubFlowFileProperties(flowId, title, description, configId);
                createFromTemplate("HelloWorldFlow.flow", propertiesValues, flowsDir);
            });
        });
    }

    private void createFromTemplate(String templateName, VirtualFile destinationDir) {
        Properties emptyProperties = new Properties();
        createFromTemplate(templateName, emptyProperties, destinationDir);
    }

    private void createFromTemplate(String templateName, Properties templateProperties, VirtualFile destinationDir) {
        FileTemplateManager manager = FileTemplateManager.getInstance(project);
        FileTemplate fileTemplate = manager.getInternalTemplate(templateName);
        try {
            String scriptText = fileTemplate.getText(templateProperties);
            VirtualFile scriptFile = destinationDir.findOrCreateChildData(this, templateName);
            VfsUtil.saveText(scriptFile, scriptText);
        } catch (IOException exception) {
            String message = message("moduleBuilder.hello.world.template.error", templateName, exception.getMessage());
            LOG.warn(message, exception);
        }
    }

    private static Optional<VirtualFile> createDirectory(VirtualFile root, String suffix) {
        try {
            return Optional.ofNullable(VfsUtil.createDirectories(root.getPath() + suffix));
        } catch (IOException exception) {
            String message = message("moduleBuilder.hello.world.create.dir.error", suffix, exception.getMessage());
            LOG.warn(message, exception);
            return Optional.empty();
        }
    }
}
