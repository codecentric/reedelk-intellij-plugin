package com.reedelk.plugin.commons;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.runtime.commons.FileExtension;

public class HotSwapUtils {

    private HotSwapUtils() {
    }

    public static boolean hasHotSwappableExtension(VirtualFile file) {
        String extension = file.getExtension();
        return extension != null &&
                (extension.equals(FileExtension.SCRIPT.value()) ||
                        extension.equals(FileExtension.FLOW.value()) ||
                        extension.equals(FileExtension.SUBFLOW.value()) ||
                        extension.equals(FileExtension.CONFIG.value()));
    }

    public static boolean isInsideHotSwappableFolder(Project project, VirtualFile file) {
        String extension = file.getExtension();
        Module moduleForFile = ModuleUtil.findModuleForFile(file, project);
        if (moduleForFile == null) {
            return false;
        } else if (FileExtension.CONFIG.value().equals(extension)) {
            return ModuleUtils.getConfigsFolder(moduleForFile).map(configsFolder ->
                    file.getPath().startsWith(configsFolder)).orElse(false);
        } else if (FileExtension.FLOW.value().equals(extension)) {
            return ModuleUtils.getFlowsFolder(moduleForFile).map(flowsFolder ->
                    file.getPath().startsWith(flowsFolder)).orElse(false);
        } else if (FileExtension.SUBFLOW.value().equals(extension)) {
            return ModuleUtils.getSubFlowsFolder(moduleForFile).map(subFlowsFolder ->
                    file.getPath().startsWith(subFlowsFolder)).orElse(false);
        } else if (FileExtension.SCRIPT.value().equals(extension)) {
            return ModuleUtils.getScriptsFolder(moduleForFile).map(scriptsFolder ->
                    file.getPath().startsWith(scriptsFolder)).orElse(false);
        } else {
            return false;
        }
    }
}
