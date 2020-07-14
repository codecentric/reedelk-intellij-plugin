package com.reedelk.plugin.action.importopenapi;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.commons.PluginModuleUtils;
import com.reedelk.plugin.template.Template;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

public class ImporterOpenAPIContext {

    private final Project project;

    public ImporterOpenAPIContext(@NotNull Project project) {
        this.project = project;
    }

    public void createTemplate(Template.Buildable buildableTemplate, String fileName, Properties properties) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        Module module = modules[0];

        Optional<String> flowsFolder = PluginModuleUtils.getFlowsFolder(module);
        flowsFolder.ifPresent(flowsFolder1 -> WriteCommandAction.runWriteCommandAction(project, () -> {
            VirtualFile flowsFolderVf = VfsUtil.findFile(Paths.get(flowsFolder1), true);
            buildableTemplate.create(project, properties, flowsFolderVf, fileName);
        }));
    }
}
