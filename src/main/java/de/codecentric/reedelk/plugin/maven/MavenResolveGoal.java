package de.codecentric.reedelk.plugin.maven;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class MavenResolveGoal extends AbstractMavenGoal {

    public MavenResolveGoal(@NotNull Project project, @NotNull String moduleName, @NotNull Callback callback) {
        super("dependency:resolve", project, moduleName, callback);
    }
}
