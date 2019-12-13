package com.reedelk.plugin.runconfig.module.runprofile;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Version;
import com.reedelk.plugin.commons.ToolWindowUtils;
import com.reedelk.plugin.executor.PluginExecutor;
import com.reedelk.plugin.maven.MavenUtils;
import com.reedelk.plugin.service.module.RuntimeApi;
import com.reedelk.plugin.service.project.SourceChangeService;
import com.reedelk.runtime.commons.ModuleUtils;
import com.reedelk.runtime.rest.api.module.v1.ModuleGETRes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.model.MavenArtifactNode;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.project.MavenProject;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static com.reedelk.plugin.commons.Defaults.PROJECT_RESOURCES_FOLDER;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.service.module.RuntimeApi.OperationCallback;
import static java.util.stream.Collectors.toList;

public class DeployRunProfile extends AbstractRunProfile {

    public DeployRunProfile(Project project, String moduleName, String runtimeConfigName) {
        super(project, moduleName, runtimeConfigName);
    }

    @Override
    protected ExecutionResult execute(@NotNull MavenProject mavenProject, @NotNull String moduleFile) throws ExecutionException {
        Module module = Optional.ofNullable(ModuleManager.getInstance(project).findModuleByName(moduleName))
                .orElseThrow(() -> new ExecutionException(message("module.run.error.module.not.found", moduleName)));

        if (SourceChangeService.getInstance(project).isHotSwap(runtimeConfigName, moduleName)) {
            // If the module has changes only in Flows/Configs/Scripts and so on, then we can hot swap the module.
            hotSwap(mavenProject, moduleFile, module);
        } else {
            // Otherwise we re-deploy the module
            deploy(moduleFile, module);
        }

        return null;
    }

    private void deploy(@NotNull String moduleFile, Module module) {
        RuntimeApi.getInstance(module).deploy(moduleFile, address, port, new OperationCallback() {
            @Override
            public void onSuccess() {
                String message = message("module.run.deploy", module.getName());
                ToolWindowUtils.notifyInfo(module.getProject(), message, runtimeConfigName);
                SourceChangeService.getInstance(project).unchanged(runtimeConfigName, moduleName);

                // TODO: Run check on dependencies
                PluginExecutor.getInstance().submit(new SyncInstalledModules(module, address, port));
            }

            @Override
            public void onError(Exception exception) {
                String message = message("module.run.deploy.error", module.getName(), exception.getMessage());
                ToolWindowUtils.notifyError(module.getProject(), message, runtimeConfigName);
            }
        });
    }

    private void hotSwap(@NotNull MavenProject mavenProject, @NotNull String moduleFile, Module module) {
        String mavenDirectory = mavenProject.getDirectory();
        String resourcesRootDirectory = Paths.get(mavenDirectory, PROJECT_RESOURCES_FOLDER).toString();

        RuntimeApi.getInstance(module).hotSwap(moduleFile, resourcesRootDirectory, address, port, new OperationCallback() {
            @Override
            public void onSuccess() {
                String message = message("module.run.update", module.getName());
                ToolWindowUtils.notifyInfo(module.getProject(), message, runtimeConfigName);
            }

            @Override
            public void onError(Exception exception) {
                String message = message("module.run.update.error", module.getName(), exception.getMessage());
                ToolWindowUtils.notifyError(module.getProject(), message, runtimeConfigName);
            }
        });
    }

    private static final Logger LOG = Logger.getInstance(SyncInstalledModules.class);

    // TODO: I should create a service. And trigger this method whenever it is needed.
    class SyncInstalledModules implements Runnable {

        private final Module module;

        private final int port;
        private final String address;

        public SyncInstalledModules(Module module, String address, int port) {
            this.module = module;
            this.address = address;
            this.port = port;
        }

        @Override
        public void run() {
            MavenUtils.getMavenProject(module).ifPresent(mavenProject -> {
                Collection<ModuleGETRes> installed = RuntimeApi.getInstance(module).getInstalledModules(address, port);

                List<MavenArtifact> scopeProvidedDependencies = mavenProject.getDependencyTree()
                        .stream()
                        .filter(artifact -> Objects.equals(artifact.getOriginalScope(), MavenConstants.SCOPE_PROVIDED))
                        .map(MavenArtifactNode::getArtifact)
                        .collect(toList());

                // TODO: Optimize me.

                scopeProvidedDependencies.stream()
                        .filter(EXCLUSIONS)
                        .filter(artifact -> ModuleUtils.isModule(artifact.getFile()))
                        .forEach(artifact -> {

                            String artifactId = artifact.getArtifactId();
                            Version artifactVersion = Version.parseVersion(artifact.getVersion());

                            boolean found = installed.stream().anyMatch(moduleGETRes -> {
                                String moduleName = moduleGETRes.getName();
                                Version moduleVersion = Version.parseVersion(moduleGETRes.getVersion());
                                return artifactId.equals(moduleName) && artifactVersion.compareTo(moduleVersion) == 0;
                            });
                            if (!found) {
                                LOG.info("Could not find: " + artifactId + ", version: " + artifactVersion + " I am going to deploy it");
                                // Install me if and only if I am an ESB module.
                                RuntimeApi.getInstance(module).install(artifact.getFile().getPath(), address, port, new OperationCallback() {
                                    @Override
                                    public void onSuccess() {
                                        LOG.info(artifactId + ", version: " + artifactVersion + " deployed.");
                                    }

                                    @Override
                                    public void onError(Exception exception) {

                                    }
                                });
                            }
                        });

            });
        }
    }

    private static final Predicate<MavenArtifact> EXCLUDE_OSGI = artifact -> !artifact.getGroupId().equals("org.osgi");
    private static final Predicate<MavenArtifact> EXCLUDE_OPS4J = artifact -> !artifact.getGroupId().equals("org.ops4j.pax.logging");
    private static final Predicate<MavenArtifact> EXCLUDE_PROJECT_REACTOR = artifact -> !artifact.getGroupId().equals("io.projectreactor");

    private static final Predicate<MavenArtifact> EXCLUSIONS = EXCLUDE_OSGI.and(EXCLUDE_OPS4J).and(EXCLUDE_PROJECT_REACTOR);
}
