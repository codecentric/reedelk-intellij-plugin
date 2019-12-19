package com.reedelk.plugin.commons;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Optional;

public class HotSwapUtils {

    private HotSwapUtils() {
    }

    public static boolean isInsideResourcesFolder(Project project, VirtualFile file) {
        Module moduleForFile = ModuleUtil.findModuleForFile(file, project);
        if (moduleForFile == null) return false;

        return PluginModuleUtils.getResourcesFolder(moduleForFile)
                .flatMap(resourcesFolderPath -> Optional.of(file.getPath().startsWith(resourcesFolderPath)))
                .orElse(false);
    }
}
