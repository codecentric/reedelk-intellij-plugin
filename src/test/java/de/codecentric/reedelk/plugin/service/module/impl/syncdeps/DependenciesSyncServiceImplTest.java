package de.codecentric.reedelk.plugin.service.module.impl.syncdeps;

import de.codecentric.reedelk.plugin.service.module.impl.syncdeps.DependenciesSyncServiceImpl;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.plugin.service.module.RuntimeApiService;
import de.codecentric.reedelk.runtime.rest.api.module.v1.ModuleGETRes;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.jetbrains.idea.maven.model.MavenArtifactNode;
import org.jetbrains.idea.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

import static de.codecentric.reedelk.plugin.service.module.RuntimeApiService.OperationCallback;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.jetbrains.idea.maven.model.MavenArtifactState.ADDED;
import static org.jetbrains.idea.maven.model.MavenConstants.SCOPE_COMPILE;
import static org.jetbrains.idea.maven.model.MavenConstants.SCOPE_PROVIDED;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DependenciesSyncServiceImplTest {

    private final String version100 = "1.0.0";
    private final String groupId = "com.test";
    private final int runtimeHostPort = 7788;
    private final String runtimeHostAddress = "localhost";

    @Mock
    private Module module;
    @Mock
    private MavenProject mockMavenProject;
    @Mock
    private RuntimeApiService runtimeApiService;

    private DependenciesSyncServiceImpl service;

    @BeforeEach
    void setUp() {
        service = spy(new DependenciesSyncServiceImpl(module));
        doReturn(Optional.of(mockMavenProject)).when(service).moduleMavenProject();
        doReturn(runtimeApiService).when(service).runtimeApiService();
    }

    @Test
    void shouldUpdateModuleWithGreaterVersionFromMavenProjectThanTheOneInstalled() {
        // Given
        ModuleGETRes moduleRest = createModuleWith("module-rest", version100);
        ModuleGETRes moduleFile = createModuleWith("module-file", version100);

        doReturn(true)
                .when(service)
                .isModule(any(File.class));
        doReturn(asList(moduleRest, moduleFile))
                .when(runtimeApiService)
                .installedModules(runtimeHostAddress, runtimeHostPort);

        String moduleRestPath = Paths.get("test", "module-rest.jar").toString();
        String moduleFilePath = Paths.get("test", "module-file.jar").toString();
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
        ModuleGETRes moduleRest = createModuleWith("module-rest", version100);
        ModuleGETRes moduleFile = createModuleWith("module-file", version100);

        doReturn(asList(moduleRest, moduleFile))
                .when(runtimeApiService)
                .installedModules(runtimeHostAddress, runtimeHostPort);

        String moduleRestPath = Paths.get("test", "module-rest.jar").toString();
        String moduleFilePath = Paths.get("test", "module-file.jar").toString();
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
        ModuleGETRes moduleRest = createModuleWith("module-rest", version100);

        doReturn(true)
                .when(service)
                .isModule(any(File.class));
        doReturn(singletonList(moduleRest))
                .when(runtimeApiService)
                .installedModules(runtimeHostAddress, runtimeHostPort);

        String moduleRestPath = Paths.get("test", "module-rest.jar").toString();
        String moduleFilePath = Paths.get("test","module-file.jar").toString();
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
        ModuleGETRes moduleRest = createModuleWith("module-rest", version100);

        doReturn(singletonList(moduleRest))
                .when(runtimeApiService)
                .installedModules(runtimeHostAddress, runtimeHostPort);

        String moduleRestPath = Paths.get("test","module-rest.jar").toString();
        doReturn(singletonList(
                createArtifactNodeWith(SCOPE_COMPILE, "module-rest", "1.0.1", new File(moduleRestPath))))
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

    private ModuleGETRes createModuleWith(String name, String version) {
        ModuleGETRes newModule = new ModuleGETRes();
        newModule.setName(name);
        newModule.setVersion(version);
        return newModule;
    }
}