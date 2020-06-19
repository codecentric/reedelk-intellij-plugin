package com.reedelk.plugin.service.module.impl.syncdeps;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Version;
import com.reedelk.plugin.commons.ExcludedArtifactsFromModuleSync;
import com.reedelk.plugin.commons.Versions;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.maven.MavenResolveGoal;
import com.reedelk.plugin.maven.MavenUtils;
import com.reedelk.plugin.service.module.CheckStateService;
import com.reedelk.plugin.service.module.DependenciesSyncService;
import com.reedelk.plugin.service.module.RuntimeApiService;
import com.reedelk.runtime.commons.ModuleUtils;
import com.reedelk.runtime.rest.api.module.v1.ModuleGETRes;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.model.MavenArtifactNode;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.project.MavenProject;

import java.io.File;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.service.module.RuntimeApiService.OperationCallback;

public class DependenciesSyncServiceImpl implements DependenciesSyncService {

    private static final Logger LOG = Logger.getInstance(DependenciesSyncServiceImpl.class);

    private final Module module;

    public DependenciesSyncServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public void syncInstalledModules(String runtimeHostAddress, int runtimeHostPort, Consumer<Void> onDone) {
        PluginExecutors.run(module,
                message("module.sync.task.title", module.getName()),
                indicator -> {

                    boolean moduleUnresolved = checkModuleStateService().isModuleUnresolved(runtimeHostAddress, runtimeHostPort);
                    if (moduleUnresolved) {
                        // We must make sure all artifacts from the pom file have been downloaded: otherwise we cannot
                        // install them into the runtime.
                        MavenResolveGoal resolveGoal = new MavenResolveGoal(module.getProject(), module.getName(), result -> {
                            // Sync modules from Maven pom and Runtime.
                            internalSyncInstalledModules(runtimeHostAddress, runtimeHostPort);

                            onDone.accept(null);

                            // Check if the current module in the Runtime was not started or resolved.
                            checkModuleStateService().checkModuleState(runtimeHostAddress, runtimeHostPort);

                        });
                        resolveGoal.execute();

                    } else {
                        onDone.accept(null);

                        // Check if the current module in the Runtime was not started or resolved.
                        checkModuleStateService().checkModuleState(runtimeHostAddress, runtimeHostPort);
                    }

                });
    }

    void internalSyncInstalledModules(String runtimeHostAddress, int runtimeHostPort) {
        moduleMavenProject().ifPresent(mavenProject -> {
            Collection<ModuleGETRes> runtimeModules =
                    runtimeApiService()
                            .installedModules(runtimeHostAddress, runtimeHostPort);
            // We get dependencies from the dependency tree because we only want the root dependencies,
            // i.e the ones defined in the pom file. If we would call mavenProject.getDependencies() we would
            // also get back the transitive dependencies. Moreover, we filter out some dependencies which are
            // not Reedelk Modules, e.g: org.osgi.
            mavenProject.getDependencyTree()
                    .stream()
                    .filter(artifact -> Objects.equals(artifact.getOriginalScope(), MavenConstants.SCOPE_PROVIDED))
                    .map(MavenArtifactNode::getArtifact)
                    .filter(ExcludedArtifactsFromModuleSync.predicate())
                    .forEach(artifact -> syncArtifact(runtimeHostAddress, runtimeHostPort, runtimeModules, artifact));
        });
    }

    Optional<MavenProject> moduleMavenProject() {
        return MavenUtils.getMavenProject(module);
    }

    RuntimeApiService runtimeApiService() {
        return RuntimeApiService.getInstance(module);
    }

    boolean isModule(File artifactFile) {
        return ModuleUtils.isModule(artifactFile);
    }

    private void syncArtifact(String runtimeHostAddress, int runtimeHostPort, Collection<ModuleGETRes> installed, MavenArtifact artifact) {
        String artifactId = artifact.getArtifactId();

        Optional<ModuleGETRes> found = findInstalledModuleByName(installed, artifactId);
        if (found.isPresent()) {

            ModuleGETRes moduleDTO = found.get();
            Version artifactVersion = Version.parseVersion(artifact.getVersion());
            Version moduleVersion = Version.parseVersion(moduleDTO.getVersion());

            // All good, they have the same version, no need to install the artifact.
            if (Versions.compare(moduleVersion, artifactVersion)) {
                return;
            }
        }

        // The Runtime does not have installed a module matching the expected artifact. Or
        // the version of the module in the artifact is different, therefore we must install it.
        if (isModule(artifact.getFile())) {
            installModuleArtifactIntoRuntime(runtimeHostAddress, runtimeHostPort, artifact);
        }
    }

    private Optional<ModuleGETRes> findInstalledModuleByName(Collection<ModuleGETRes> installedModules, String moduleName) {
        return installedModules
                .stream()
                .filter(moduleDTO -> moduleDTO.getName().equals(moduleName))
                .findFirst();
    }

    private CheckStateService checkModuleStateService() {
        return CheckStateService.getInstance(module);
    }

    private void installModuleArtifactIntoRuntime(String address, int port, final MavenArtifact artifact) {
        runtimeApiService().install(artifact.getFile().getPath(), address, port, new OperationCallback() {
            @Override
            public void onSuccess() {
                String artifactId = artifact.getArtifactId();
                String artifactVersion = artifact.getVersion();
                LOG.info(message("module.sync.module.deployed", artifactId, artifactVersion));
            }

            @Override
            public void onError(Exception exception) {
                String artifactId = artifact.getArtifactId();
                String artifactVersion = artifact.getVersion();
                LOG.warn(message("module.sync.module.deploy.error", artifactId, artifactVersion, exception.getMessage()));
            }
        });
    }
}
