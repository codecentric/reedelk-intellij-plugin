package com.reedelk.plugin.action.openapi;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.model.InfoObject;
import com.reedelk.openapi.v3.model.OpenApiObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class OpenApiUtilsTest {

    @Mock
    private OpenApiImporterContext context;

    @BeforeEach
    void setUp() {
        lenient().doReturn(OpenApiSchemaFormat.JSON).when(context).getSchemaFormat();
    }

    @Test
    void shouldReturnCorrectConfigTitle() {
        // Given
        InfoObject infoObject = new InfoObject();
        infoObject.setTitle("Test API");
        OpenApiObject openApiObject = new OpenApiObject();
        openApiObject.setInfo(infoObject);

        // When
        String actual = OpenApiUtils.restListenerConfigTitleFrom(openApiObject);

        // Then
        String expected = "Test API REST Listener";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnDefaultConfigTitle() {
        // Given
        OpenApiObject openApiObject = new OpenApiObject();

        // When
        String actual = OpenApiUtils.restListenerConfigTitleFrom(openApiObject);

        // Then
        String expected = "My Api REST Listener";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnCorrectConfigFileName() {
        // Given
        InfoObject infoObject = new InfoObject();
        infoObject.setTitle("Test API");
        OpenApiObject openApiObject = new OpenApiObject();
        openApiObject.setInfo(infoObject);

        // When
        String actual = OpenApiUtils.restListenerConfigFileNameFrom(openApiObject);

        // Then
        String expected = "TestAPIRESTListener.fconfig";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnDefaultConfigFileName() {
        // Given
        OpenApiObject openApiObject = new OpenApiObject();

        // When
        String actual = OpenApiUtils.restListenerConfigFileNameFrom(openApiObject);

        // Then
        String expected = "MyApiRESTListener.fconfig";
        assertThat(actual).isEqualTo(expected);
    }

    // Schema
    @Test
    void shouldReturnCorrectSchemaFileNameWhenOperationIdPresent() {
        // Given
        NavigationPath navigationPath = NavigationPath.create()
                .with(NavigationPath.SegmentKey.OPERATION_ID, "getPetById")
                .with(NavigationPath.SegmentKey.METHOD, "GET")
                .with(NavigationPath.SegmentKey.PATH, "/order/{id}")
                .with(NavigationPath.SegmentKey.RESPONSES)
                .with(NavigationPath.SegmentKey.STATUS_CODE, "200")
                .with(NavigationPath.SegmentKey.CONTENT)
                .with(NavigationPath.SegmentKey.CONTENT_TYPE, "application/json");

        // When
        String actual = OpenApiUtils.schemaFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("getPetById_response_200_application_json.schema.json");
    }

    @Test
    void shouldReturnCorrectSchemaFileNameWhenOperationIdNotPresent() {
        // Given
        NavigationPath navigationPath = NavigationPath.create()
                .with(NavigationPath.SegmentKey.METHOD, "GET")
                .with(NavigationPath.SegmentKey.PATH, "/order/{id}")
                .with(NavigationPath.SegmentKey.RESPONSES)
                .with(NavigationPath.SegmentKey.STATUS_CODE, "200")
                .with(NavigationPath.SegmentKey.CONTENT)
                .with(NavigationPath.SegmentKey.CONTENT_TYPE, "application/json");

        // When
        String actual = OpenApiUtils.schemaFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("getOrderId_response_200_application_json.schema.json");
    }

    @Test
    void shouldReturnCorrectSchemaFileNameWhenOperationIdNotPresentAndRootPath() {
        // Given
        NavigationPath navigationPath = NavigationPath.create()
                .with(NavigationPath.SegmentKey.METHOD, "GET")
                .with(NavigationPath.SegmentKey.PATH, "/")
                .with(NavigationPath.SegmentKey.RESPONSES)
                .with(NavigationPath.SegmentKey.STATUS_CODE, "200")
                .with(NavigationPath.SegmentKey.CONTENT)
                .with(NavigationPath.SegmentKey.CONTENT_TYPE, "application/json");

        // When
        String actual = OpenApiUtils.schemaFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("get_response_200_application_json.schema.json");
    }

    // Example
    @Test
    void shouldReturnCorrectExampleFileNameWhenOperationIdPresent() {
        // Given
        NavigationPath navigationPath = NavigationPath.create()
                .with(NavigationPath.SegmentKey.OPERATION_ID, "getPetById")
                .with(NavigationPath.SegmentKey.METHOD, "GET")
                .with(NavigationPath.SegmentKey.PATH, "/order/{id}")
                .with(NavigationPath.SegmentKey.RESPONSES)
                .with(NavigationPath.SegmentKey.STATUS_CODE, "200")
                .with(NavigationPath.SegmentKey.CONTENT)
                .with(NavigationPath.SegmentKey.CONTENT_TYPE, "application/json");

        // When
        String actual = OpenApiUtils.exampleFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("getPetById_response_200_application_json.example.json");
    }

    @Test
    void shouldReturnCorrectExampleFileNameWhenOperationIdNotPresent() {
        // Given
        NavigationPath navigationPath = NavigationPath.create()
                .with(NavigationPath.SegmentKey.METHOD, "GET")
                .with(NavigationPath.SegmentKey.PATH, "/order/{id}")
                .with(NavigationPath.SegmentKey.RESPONSES)
                .with(NavigationPath.SegmentKey.STATUS_CODE, "200")
                .with(NavigationPath.SegmentKey.CONTENT)
                .with(NavigationPath.SegmentKey.CONTENT_TYPE, "application/json");

        // When
        String actual = OpenApiUtils.exampleFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("getOrderId_response_200_application_json.example.json");
    }

    @Test
    void shouldReturnCorrectExampleFileNameWhenOperationIdNotPresentAndRootPath() {
        // Given
        NavigationPath navigationPath = NavigationPath.create()
                .with(NavigationPath.SegmentKey.METHOD, "GET")
                .with(NavigationPath.SegmentKey.PATH, "/")
                .with(NavigationPath.SegmentKey.RESPONSES)
                .with(NavigationPath.SegmentKey.STATUS_CODE, "200")
                .with(NavigationPath.SegmentKey.CONTENT)
                .with(NavigationPath.SegmentKey.CONTENT_TYPE, "application/json");

        // When
        String actual = OpenApiUtils.exampleFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("get_response_200_application_json.example.json");
    }
}
