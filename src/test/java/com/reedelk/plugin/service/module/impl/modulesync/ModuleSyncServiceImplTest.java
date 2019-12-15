package com.reedelk.plugin.service.module.impl.modulesync;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.service.module.RuntimeApiService;
import com.reedelk.runtime.rest.api.module.v1.ModuleGETRes;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.model.MavenArtifactNode;
import org.jetbrains.idea.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Optional;

import static com.reedelk.plugin.service.module.RuntimeApiService.OperationCallback;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.jetbrains.idea.maven.model.MavenArtifactState.ADDED;
import static org.jetbrains.idea.maven.model.MavenConstants.SCOPE_COMPILE;
import static org.jetbrains.idea.maven.model.MavenConstants.SCOPE_PROVIDED;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModuleSyncServiceImplTest {

    private final String groupId = "com.test";
    private final String runtimeHostAddress = "localhost";
    private final int runtimeHostPort = 7788;

    private ModuleSyncServiceImpl service;

    @Mock
    private RuntimeApiService runtimeApiService;
    @Mock
    private Module module;
    @Mock
    private MavenProject mockMavenProject;

    @BeforeEach
    void setUp() {
        service = spy(new ModuleSyncServiceImpl(module));
        doReturn(Optional.of(mockMavenProject)).when(service).moduleMavenProject();
        doReturn(runtimeApiService).when(service).runtimeApiService();
    }

    @Test
    void shouldUpdateModuleWithGreaterVersionFromMavenProjectThanTheOneInstalled() {
        // Given
        ModuleGETRes moduleRest = new ModuleGETRes();
        moduleRest.setName("module-rest");
        moduleRest.setVersion("1.0.0");
        ModuleGETRes moduleFile = new ModuleGETRes();
        moduleFile.setName("module-file");
        moduleFile.setVersion("1.0.0");

        doReturn(true).when(service).isModule(any(File.class));
        doReturn(asList(moduleRest, moduleFile)).when(runtimeApiService).installedModules(runtimeHostAddress, runtimeHostPort);

        String moduleRestPath = "/test/module-rest.jar";
        String moduleFilePath = "/test/module-file.jar";
        doReturn(asList(
                createArtifactNodeWith(SCOPE_PROVIDED, "module-rest", "1.0.0", new File(moduleRestPath)),
                createArtifactNodeWith(SCOPE_PROVIDED, "module-file", "1.0.1", new File(moduleFilePath))))
                .when(mockMavenProject).getDependencyTree();

        // When
        service.internalSyncInstalledModules(runtimeHostAddress, runtimeHostPort);

        // Then
        verify(runtimeApiService)
                .install(eq(moduleFilePath), eq(runtimeHostAddress), eq(runtimeHostPort), any(OperationCallback.class));
    }

    @Test
    void shouldNotInstallAnythingWhenAllTheModulesAreUpToDate() {
        // Given
        ModuleGETRes moduleRest = new ModuleGETRes();
        moduleRest.setName("module-rest");
        moduleRest.setVersion("1.0.0");
        ModuleGETRes moduleFile = new ModuleGETRes();
        moduleFile.setName("module-file");
        moduleFile.setVersion("1.0.0");

        doReturn(asList(moduleRest, moduleFile)).when(runtimeApiService).installedModules(runtimeHostAddress, runtimeHostPort);

        String moduleRestPath = "/test/module-rest.jar";
        String moduleFilePath = "/test/module-file.jar";
        doReturn(asList(
                createArtifactNodeWith(SCOPE_PROVIDED, "module-rest", "1.0.0", new File(moduleRestPath)),
                createArtifactNodeWith(SCOPE_PROVIDED, "module-file", "1.0.0", new File(moduleFilePath))))
                .when(mockMavenProject).getDependencyTree();

        // When
        service.internalSyncInstalledModules(runtimeHostAddress, runtimeHostPort);

        // Then
        verify(runtimeApiService, never())
                .install(anyString(), anyString(), anyInt(), any(OperationCallback.class));
    }

    @Test
    void shouldInstallModuleWhenNotInstalledButPresentInMavenDependencies() {
        // Given
        ModuleGETRes moduleRest = new ModuleGETRes();
        moduleRest.setName("module-rest");
        moduleRest.setVersion("1.0.0");

        doReturn(true).when(service).isModule(any(File.class));
        doReturn(singletonList(moduleRest)).when(runtimeApiService).installedModules(runtimeHostAddress, runtimeHostPort);

        String moduleRestPath = "/test/module-rest.jar";
        String moduleFilePath = "/test/module-file.jar";
        doReturn(asList(
                createArtifactNodeWith(SCOPE_PROVIDED, "module-rest", "1.0.0", new File(moduleRestPath)),
                createArtifactNodeWith(SCOPE_PROVIDED, "module-file", "1.0.1", new File(moduleFilePath))))
                .when(mockMavenProject).getDependencyTree();

        // When
        service.internalSyncInstalledModules(runtimeHostAddress, runtimeHostPort);

        // Then
        verify(runtimeApiService)
                .install(eq(moduleFilePath), eq(runtimeHostAddress), eq(runtimeHostPort), any(OperationCallback.class));
    }

    @Test
    void shouldIgnoreDependenciesWithScopeNotProvided() {
        // Given
        ModuleGETRes moduleRest = new ModuleGETRes();
        moduleRest.setName("module-rest");
        moduleRest.setVersion("1.0.0");

        doReturn(singletonList(moduleRest)).when(runtimeApiService).installedModules(runtimeHostAddress, runtimeHostPort);

        String moduleRestPath = "/test/module-rest.jar";
        doReturn(
                singletonList(createArtifactNodeWith(SCOPE_COMPILE, "module-rest", "1.0.1", new File(moduleRestPath))))
                .when(mockMavenProject)
                .getDependencyTree();

        // When
        service.internalSyncInstalledModules(runtimeHostAddress, runtimeHostPort);

        // Then
        verify(runtimeApiService, never())
                .install(anyString(), anyString(), anyInt(), any(OperationCallback.class));
    }

    private MavenArtifactNode createArtifactNodeWith(String scope, String artifactId, String version, File artifactFile) {
        MavenArtifact mavenArtifact = new MavenArtifact(
                groupId, artifactId, version, null, null,
                null, null, false, null,
                artifactFile, null, false, false);
        return new MavenArtifactNode(null, mavenArtifact, ADDED,
                null, scope, null, null);
    }

}