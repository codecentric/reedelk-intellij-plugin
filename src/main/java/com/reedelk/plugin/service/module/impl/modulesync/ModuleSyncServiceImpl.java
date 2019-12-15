package com.reedelk.plugin.service.module.impl.modulesync;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Version;
import com.reedelk.plugin.commons.ExcludedArtifactsFromModuleSync;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.maven.MavenUtils;
import com.reedelk.plugin.service.module.ModuleSyncService;
import com.reedelk.plugin.service.module.RuntimeApiService;
import com.reedelk.runtime.commons.ModuleUtils;
import com.reedelk.runtime.rest.api.module.v1.ModuleGETRes;
import org.jetbrains.annotations.Nullable;
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
import static com.reedelk.runtime.commons.Preconditions.checkState;

public class ModuleSyncServiceImpl implements ModuleSyncService {

    private static final Logger LOG = Logger.getInstance(ModuleSyncServiceImpl.class);

    private final Module module;

    public ModuleSyncServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public void syncInstalledModules(String runtimeHostAddress, int runtimeHostPort) {
        PluginExecutors.sequential().submit(() ->
                internalSyncInstalledModules(runtimeHostAddress, runtimeHostPort));
    }

    void internalSyncInstalledModules(String runtimeHostAddress, int runtimeHostPort) {
        moduleMavenProject().ifPresent(mavenProject -> {

            Collection<ModuleGETRes> runtimeModules = runtimeApiService()
                    .installedModules(runtimeHostAddress, runtimeHostPort);

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
            if (sameVersion(moduleVersion, artifactVersion)) {
                return;
            }
        }

        // The Runtime does not have installed a module matching the expected artifact. Or
        // the version of the module in the artifact is different, therefore we must install it.
        if (isModule(artifact.getFile())) {
            installModuleArtifactIntoRuntime(runtimeHostAddress, runtimeHostPort, artifact);
        }
    }

    private boolean sameVersion(@Nullable Version moduleVersion, @Nullable Version artifactVersion) {
        checkState(moduleVersion != null, "module version");
        checkState(artifactVersion != null, "artifact version");
        return moduleVersion.compareTo(artifactVersion) == 0;
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
                LOG.info(message("module.sync.module.deployed",  artifactId, artifactVersion));
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
