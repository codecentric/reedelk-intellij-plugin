package com.reedelk.plugin.service.module.impl.modulesyncdeps;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Version;
import com.reedelk.plugin.commons.ExcludedArtifactsFromModuleSync;
import com.reedelk.plugin.commons.Versions;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.maven.MavenUtils;
import com.reedelk.plugin.service.module.ModuleCheckErrorsService;
import com.reedelk.plugin.service.module.ModuleDependenciesSyncService;
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

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.service.module.RuntimeApiService.OperationCallback;

public class ModuleDependenciesSyncServiceImpl implements ModuleDependenciesSyncService {

    private static final Logger LOG = Logger.getInstance(ModuleDependenciesSyncServiceImpl.class);

    private final Module module;

    public ModuleDependenciesSyncServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public void syncInstalledModules(String runtimeHostAddress, int runtimeHostPort) {

        PluginExecutors.run(module, indicator -> {
            // Sync modules from Maven pom and Runtime.
            internalSyncInstalledModules(runtimeHostAddress, runtimeHostPort);

            // Check if there are any Modules in the Runtime in 'UNRESOLVED' or 'ERROR' state.
            checkErrorsService().checkForErrors(runtimeHostAddress, runtimeHostPort);
        });
    }

    void internalSyncInstalledModules(String runtimeHostAddress, int runtimeHostPort) {
        moduleMavenProject().ifPresent(mavenProject -> {
            Collection<ModuleGETRes> runtimeModules =
                    runtimeApiService().installedModules(runtimeHostAddress, runtimeHostPort);
            // We get dependencies from the dependency tree because we only want the root dependencies,
            // i.e the ones defined in the pom file. If we would call mavenProject.getDependencies() we would
            // also get back the transitive dependencies. Moreover, we filter out some dependencies which are
            // not ESB Modules, e.g: org.osgi.
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

    ModuleCheckErrorsService checkErrorsService() {
        return ModuleCheckErrorsService.getInstance(module);
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