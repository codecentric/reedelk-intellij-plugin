package com.reedelk.plugin.commons;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.util.DisposeAwareRunnable;

public class ReedelkPluginUtil {

    public static void runWhenInitialized(final Project project, final Runnable r) {
        if (project.isDisposed()) {
            // nothing to do.
        } else if (project.isInitialized()) {
            DumbService.getInstance(project).runWhenSmart(DisposeAwareRunnable.create(r, project));
        } else {
            StartupManager.getInstance(project).registerPostStartupActivity(DisposeAwareRunnable.create(r, project));
        }
    }
}
