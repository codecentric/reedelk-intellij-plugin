package de.codecentric.reedelk.plugin.maven;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class MavenPackageGoal extends AbstractMavenGoal {

    public MavenPackageGoal(@NotNull Project project, @NotNull String moduleName, @NotNull Callback callback) {
        super("package -DskipTests=true", project, moduleName, callback);
    }
}
