package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.model.*;
import com.reedelk.runtime.api.commons.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;


class ConfigOpenApiObjectSerializerTest extends AbstractSerializerTest {

    private ConfigOpenApiObjectSerializer serializer;

    @BeforeEach
    void setUp() {
        serializer = new ConfigOpenApiObjectSerializer(context);
    }

    @Test
    void shouldSerializeOpenApiConfigCorrectly() {
        // Given
        NavigationPath navigationPath = NavigationPath.create();

        LicenseObject licenseObject = new LicenseObject();
        licenseObject.setUrl("http://my-domain.com/LICENSE.txt");
        licenseObject.setName("Apache License");

        InfoObject infoObject = new InfoObject();
        infoObject.setTitle("My API");
        infoObject.setLicense(licenseObject);
        infoObject.setVersion("v2");
        infoObject.setDescription("API Description");

        ServerVariableObject hostVariable = new ServerVariableObject();
        hostVariable.setDefaultValue("localhost");
        hostVariable.setDescription("Host variable description");
        hostVariable.setEnumValues(asList("localhost", "my-local.domain.com"));

        ServerObject serverObject = new ServerObject();
        serverObject.setUrl("http://{host}");
        serverObject.setDescription("My server object");
        serverObject.setVariables(ImmutableMap.of("host", hostVariable));

        ComponentsObject componentsObject = new ComponentsObject();

        ConfigOpenApiObject configOpenApiObject =
                new ConfigOpenApiObject(infoObject, singletonList(serverObject), componentsObject);

        // When
        Map<String, Object> serialize =
                serializer.serialize(serializerContext, navigationPath, configOpenApiObject);

        // Then
        Map<String, Object> expectedInfoMap = new LinkedHashMap<>();
        expectedInfoMap.put("title", "My API");
        expectedInfoMap.put("description", "API Description");
        expectedInfoMap.put("license", ImmutableMap.of("url", "http://my-domain.com/LICENSE.txt", "name", "Apache License"));
        expectedInfoMap.put("version", "v2");

        Map<String, Object> variablesMap = new LinkedHashMap<>();
        variablesMap.put("host",
                ImmutableMap.of("default", "localhost",
                        "description","Host variable description",
                        "enum", asList("localhost", "my-local.domain.com")));

        Map<String, Object> expectedMap = ImmutableMap.of(
                "info", expectedInfoMap,
                "servers", singletonList(ImmutableMap.of(
                        "url", "http://{host}", "description", "My server object", "variables", variablesMap)),
                "components", ImmutableMap.of());

        assertThat(serialize).isEqualTo(expectedMap);
    }
}
