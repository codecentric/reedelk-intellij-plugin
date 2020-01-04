package com.reedelk.plugin.service.module.impl.checkstate;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.service.module.RuntimeApiService;
import com.reedelk.runtime.rest.api.module.v1.ModuleGETRes;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckStateServiceImplTest {

    private final String groupId = "com.test";
    private final String version = "0.8.1-SNAPSHOT";
    private final int runtimeHostPort = 7788;
    private final String runtimeHostAddress = "localhost";

    @Mock
    private Module module;
    @Mock
    private MavenProject mockMavenProject;
    @Mock
    private RuntimeApiService runtimeApiService;

    private CheckStateServiceImpl service;

    @BeforeEach
    void setUp() {
        service = spy(new CheckStateServiceImpl(module));
    }

    @Test
    void shouldInvokeNotifyFromStateWhenExistsModuleInstalledMatchingCurrentProjectModuleName() {
        // Given
        ModuleGETRes moduleRest = createModuleWith("module-rest");
        ModuleGETRes moduleFile = createModuleWith("module-file");

        doReturn(asList(moduleRest, moduleFile))
                .when(runtimeApiService)
                .installedModules(runtimeHostAddress, runtimeHostPort);

        MavenId mavenId = new MavenId(groupId, "module-file", version);
        doReturn(mavenId).when(mockMavenProject).getMavenId();
        doReturn(Optional.of(mockMavenProject)).when(service).moduleMavenProject();
        doReturn(runtimeApiService).when(service).runtimeApiService();

        // When
        service.internalCheckModuleState(runtimeHostAddress, runtimeHostPort);

        // Then
        verify(service).notifyFromStateIfNeeded(moduleFile, runtimeHostAddress, runtimeHostPort);
    }

    @Test
    void shouldNotInvokeNotifyFromStateWhenDoesNotExistsModuleInstalledMatchingCurrentProjectName() {
        // Given
        ModuleGETRes moduleRest = createModuleWith("module-rest");
        ModuleGETRes moduleFile = createModuleWith("module-file");

        doReturn(asList(moduleRest, moduleFile))
                .when(runtimeApiService)
                .installedModules(runtimeHostAddress, runtimeHostPort);

        MavenId mavenId = new MavenId(groupId, "my-unknown-module", version);
        doReturn(mavenId).when(mockMavenProject).getMavenId();
        doReturn(Optional.of(mockMavenProject)).when(service).moduleMavenProject();
        doReturn(runtimeApiService).when(service).runtimeApiService();

        // When
        service.internalCheckModuleState(runtimeHostAddress, runtimeHostPort);

        // Then
        verify(service, never()).notifyFromStateIfNeeded(any(ModuleGETRes.class), anyString(), anyInt());
    }

    @Test
    void shouldNotInvokeNotifyFromStateWhenMavenProjectCouldNotBeFound() {
        // Given
        doReturn(Optional.empty()).when(service).moduleMavenProject();

        // When
        service.internalCheckModuleState(runtimeHostAddress, runtimeHostPort);

        // Then
        verify(service, never()).notifyFromStateIfNeeded(any(ModuleGETRes.class), anyString(), anyInt());
    }

    @Test
    void shouldNotInvokeNotifyFromStateWhenMavenProjectArtifactIdCouldNotBeFound() {
        // Given
        MavenId mavenId = new MavenId(groupId, null, version);
        doReturn(mavenId).when(mockMavenProject).getMavenId();
        doReturn(Optional.of(mockMavenProject)).when(service).moduleMavenProject();

        // When
        service.internalCheckModuleState(runtimeHostAddress, runtimeHostPort);

        // Then
        verify(service, never()).notifyFromStateIfNeeded(any(ModuleGETRes.class), anyString(), anyInt());
    }

    private ModuleGETRes createModuleWith(String name) {
        ModuleGETRes newModule = new ModuleGETRes();
        newModule.setName(name);
        return newModule;
    }
}