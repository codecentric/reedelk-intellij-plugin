package com.reedelk.plugin.action.openapi;

import com.reedelk.openapi.v3.model.OpenApiObject;
import com.reedelk.openapi.v3.model.ServerObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Arrays.asList;
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
        assertThat(listenerPort).isEqualTo(8484);
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
        assertThat(listenerPort).isEqualTo(8484);
    }
}
