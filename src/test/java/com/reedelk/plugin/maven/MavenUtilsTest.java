package com.reedelk.plugin.maven;

import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class MavenUtilsTest {

    @Mock
    private MavenProject mavenProject;

    @Test
    void shouldReturnCorrectModuleJarFileWithPath() {
        // Given
        MavenId mavenId = new MavenId("com.test", "module-xyz", "1.4.0");
        doReturn(mavenId)
                .when(mavenProject)
                .getMavenId();

        String buildDirectory = File.separator + "projects" + File.separator + "myproject" + File.separator + "target";
        doReturn(buildDirectory)
                .when(mavenProject)
                .getBuildDirectory();

        // When
        String moduleJarFile = MavenUtils.getModuleJarFile(mavenProject).toString();

        // Then
        String expected = Paths.get(File.separator + "projects", "myproject", "target", "module-xyz-1.4.0.jar").toString();
        assertThat(moduleJarFile).isEqualTo(expected);
    }

    @Test
    void shouldReturnCorrectModuleJarFileURIWithPath() {
        // Given
        MavenId mavenId = new MavenId("com.test", "module-xyz", "1.4.0");
        doReturn(mavenId)
                .when(mavenProject)
                .getMavenId();

        String buildDirectory = File.separator + "projects" + File.separator + "myproject" + File.separator + "target";
        doReturn(buildDirectory)
                .when(mavenProject)
                .getBuildDirectory();

        // When
        String moduleJarFile = MavenUtils.getModuleJarFileURI(mavenProject);

        // Then
        URI uri = Paths.get(File.separator + "projects", "myproject", "target", "module-xyz-1.4.0.jar").toUri();
        String expected = uri.toString();
        assertThat(moduleJarFile).isEqualTo(expected);
    }
}