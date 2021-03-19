package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.plugin.commons.ExcludedArtifactsFromModuleSync;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ExcludedArtifactsFromModuleSyncTest {

    @Test
    void shouldExcludeRuntimeApiArtifact() {
        // Given
        MavenArtifact runtimeApiArtifact = mock(MavenArtifact.class);

        doReturn("com.reedelk")
                .when(runtimeApiArtifact)
                .getGroupId();
        doReturn("runtime-api")
                .when(runtimeApiArtifact)
                .getArtifactId();

        // When
        boolean isAllowed = ExcludedArtifactsFromModuleSync.predicate().test(runtimeApiArtifact);


        // Then
        assertThat(isAllowed).isFalse();
    }

    @Test
    void shouldIncludeReedelkModule() {
        // Given
        MavenArtifact runtimeApiArtifact = mock(MavenArtifact.class);

        doReturn("com.reedelk")
                .when(runtimeApiArtifact)
                .getGroupId();
        doReturn("module-mymodule")
                .when(runtimeApiArtifact)
                .getArtifactId();

        // When
        boolean isAllowed = ExcludedArtifactsFromModuleSync.predicate().test(runtimeApiArtifact);


        // Then
        assertThat(isAllowed).isTrue();
    }
}