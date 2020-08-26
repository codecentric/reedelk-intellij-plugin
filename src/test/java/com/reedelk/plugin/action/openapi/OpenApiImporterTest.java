package com.reedelk.plugin.action.openapi;

import com.reedelk.openapi.v3.model.OpenApiObject;
import com.reedelk.openapi.v3.model.ServerObject;
import com.reedelk.runtime.api.commons.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class OpenApiImporterTest {

    @Mock
    private OpenApiImporterContext context;

    @Test
    void shouldReturnCorrectListenerPortWhenContextHasPort() {
        // Given
        doReturn(9191).when(context).getOpenApiPort();

        ServerObject localhostServer = new ServerObject();
        localhostServer.setUrl("localhost:8787");

        ServerObject remoteServer = new ServerObject();
        remoteServer.setUrl("http://my.domain.com");

        OpenApiObject openApiObject = new OpenApiObject();
        openApiObject.setServers(asList(localhostServer, remoteServer));

        OpenApiImporter importer = new OpenApiImporter(context);

        // When
        int listenerPort = importer.findListenerPort(openApiObject);

        // Then
        assertThat(listenerPort).isEqualTo(9191);
    }

    @Test
    void shouldReturnCorrectListenerPortFromOpenApiServerObject() {
        // Given
        doReturn(null).when(context).getOpenApiPort();

        ServerObject localhostServer = new ServerObject();
        localhostServer.setUrl("localhost:8787");

        ServerObject remoteServer = new ServerObject();
        remoteServer.setUrl("http://my.domain.com");

        OpenApiObject openApiObject = new OpenApiObject();
        openApiObject.setServers(asList(localhostServer, remoteServer));

        OpenApiImporter importer = new OpenApiImporter(context);

        // When
        int listenerPort = importer.findListenerPort(openApiObject);

        // Then
        assertThat(listenerPort).isEqualTo(8787);
    }

    @Test
    void shouldReturnDefaultListenerPortFromOpenApiServerObject() {
        // Given
        doReturn(null).when(context).getOpenApiPort();

        ServerObject localhostServer = new ServerObject();
        localhostServer.setUrl("localhost");

        ServerObject remoteServer = new ServerObject();
        remoteServer.setUrl("http://my.domain.com");

        OpenApiObject openApiObject = new OpenApiObject();
        openApiObject.setServers(asList(localhostServer, remoteServer));

        OpenApiImporter importer = new OpenApiImporter(context);

        // When
        int listenerPort = importer.findListenerPort(openApiObject);

        // Then
        assertThat(listenerPort).isEqualTo(8080);
    }

    @Test
    void shouldReturnDefaultListenerPortWhenNoServerObjects() {
        // Given
        doReturn(null).when(context).getOpenApiPort();

        OpenApiObject openApiObject = new OpenApiObject();
        OpenApiImporter importer = new OpenApiImporter(context);

        // When
        int listenerPort = importer.findListenerPort(openApiObject);

        // Then
        assertThat(listenerPort).isEqualTo(8080);
    }

    @Test
    void shouldFindCorrectBasePathWhenDefinedInContext() {
        // Given
        doReturn("v35").when(context).getBasePath();

        OpenApiObject openApiObject = new OpenApiObject();
        OpenApiImporter importer = new OpenApiImporter(context);

        // When
        String basePath = importer.getBasePath(openApiObject);

        // Then
        assertThat(basePath).isEqualTo("v35");
    }

    @Test
    void shouldFindCorrectBasePathWhenDefinedInServerObject() {
        // Given
        ServerObject localhostServer = new ServerObject();
        localhostServer.setUrl("localhost/v54");

        ServerObject remoteServer = new ServerObject();
        remoteServer.setUrl("http://my.domain.com/v54");

        OpenApiObject openApiObject = new OpenApiObject();
        openApiObject.setServers(asList(localhostServer, remoteServer));

        OpenApiImporter importer = new OpenApiImporter(context);

        // When
        String basePath = importer.getBasePath(openApiObject);

        // Then
        assertThat(basePath).isEqualTo("/v54");
    }

    @Test
    void shouldFindCorrectBasePathWhenDefinedInServerObjectWithMultipleSegments() {
        // Given
        ServerObject remoteServer = new ServerObject();
        remoteServer.setUrl("http://my.domain.com/api/v54");

        OpenApiObject openApiObject = new OpenApiObject();
        openApiObject.setServers(singletonList(remoteServer));

        OpenApiImporter importer = new OpenApiImporter(context);

        // When
        String basePath = importer.getBasePath(openApiObject);

        // Then
        assertThat(basePath).isEqualTo("/api/v54");
    }

    @Test
    void shouldFindCorrectBasePathWhenDefinedInServerObjectWhenRoot() {
        // Given
        ServerObject remoteServer = new ServerObject();
        remoteServer.setUrl("http://my.domain.com");

        OpenApiObject openApiObject = new OpenApiObject();
        openApiObject.setServers(singletonList(remoteServer));

        OpenApiImporter importer = new OpenApiImporter(context);

        // When
        String basePath = importer.getBasePath(openApiObject);

        // Then
        assertThat(basePath).isEqualTo(StringUtils.EMPTY);
    }

    @Test
    void shouldReturnDefaultBasePath() {
        // Given
        OpenApiObject openApiObject = new OpenApiObject();
        OpenApiImporter importer = new OpenApiImporter(context);

        // When
        String basePath = importer.getBasePath(openApiObject);

        // Then
        assertThat(basePath).isEqualTo("/v1");
    }

    @Test
    void shouldReturnCorrectBasePathForLocalhost() {
        // Given
        ServerObject remoteServer = new ServerObject();
        remoteServer.setUrl("http://localhost");

        OpenApiObject openApiObject = new OpenApiObject();
        openApiObject.setServers(singletonList(remoteServer));

        OpenApiImporter importer = new OpenApiImporter(context);

        // When
        String basePath = importer.getBasePath(openApiObject);

        // Then
        assertThat(basePath).isEqualTo(StringUtils.EMPTY);
    }
}
