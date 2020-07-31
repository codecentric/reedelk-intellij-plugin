package com.reedelk.plugin.action.openapi.importer;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.commons.PluginModuleUtils;
import com.reedelk.plugin.template.ConfigProperties;
import com.reedelk.plugin.template.Template;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

public class OpenApiImporterContext {

    private final Project project;
    private final String configId = UUID.randomUUID().toString();

    public OpenApiImporterContext(@NotNull Project project) {
        this.project = project;
    }

    public String getConfigId() {
        return configId;
    }

    public void createTemplate(Template.Buildable buildableTemplate, String fileName, Properties properties) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        Module module = modules[0];

        Optional<String> flowsFolder = PluginModuleUtils.getFlowsFolder(module);
        flowsFolder.ifPresent(flowsFolder1 -> WriteCommandAction.runWriteCommandAction(project, () -> {
            VirtualFile flowsFolderVf = VfsUtil.findFile(Paths.get(flowsFolder1), true);
            buildableTemplate.create(project, properties, flowsFolderVf, fileName)
                    .ifPresent(virtualFile -> FileEditorManager.getInstance(project).openFile(virtualFile, true));
        }));
    }

    public void createConfig(String configFileName) {
        String title = "Open API Config";

        Module[] modules = ModuleManager.getInstance(project).getModules();
        Module module = modules[0];

        Optional<String> configsFolder = PluginModuleUtils.getConfigsFolder(module);
        configsFolder.ifPresent(configsFolder1 -> WriteCommandAction.runWriteCommandAction(project, () -> {
            VirtualFile configsDirectoryVf = VfsUtil.findFile(Paths.get(configsFolder1), true);
            ConfigProperties configProperties = new ConfigProperties(configId, title);
            Template.HelloWorld.CONFIG.create(project, configProperties, configsDirectoryVf, configFileName);
        }));
    }
}
