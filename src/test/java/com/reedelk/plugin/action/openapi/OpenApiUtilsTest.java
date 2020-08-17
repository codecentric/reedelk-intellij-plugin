package com.reedelk.plugin.action.openapi;

import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.model.InfoObject;
import com.reedelk.openapi.v3.model.OpenApiObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.reedelk.openapi.commons.NavigationPath.SegmentKey;
import static com.reedelk.openapi.commons.NavigationPath.create;
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

    // Flow File Name
    @Test
    void shouldReturnCorrectFlowFileName() {
        // Given
        NavigationPath navigationPath = NavigationPath.create()
                .with(SegmentKey.OPERATION_ID, "addPet")
                .with(SegmentKey.PATH, "/pet")
                .with(SegmentKey.METHOD, "POST");

        // When
        String actual = OpenApiUtils.flowFileNameFrom(navigationPath);

        // Then
        assertThat(actual).isEqualTo("addPet.flow");
    }

    @Test
    void shouldReturnCorrectFlowFileNameWhenOperationIdNotPresent() {
        // Given
        NavigationPath navigationPath = NavigationPath.create()
                .with(SegmentKey.PATH, "/pet")
                .with(SegmentKey.METHOD, "POST");

        // When
        String actual = OpenApiUtils.flowFileNameFrom(navigationPath);

        // Then
        assertThat(actual).isEqualTo("POSTPet.flow");
    }

    @Test
    void shouldReturnCorrectFlowFileNameWhenOperationIdNotPresentAndRoot() {
        // Given
        NavigationPath navigationPath = NavigationPath.create()
                .with(SegmentKey.PATH, "/")
                .with(SegmentKey.METHOD, "PUT");

        // When
        String actual = OpenApiUtils.flowFileNameFrom(navigationPath);

        // Then
        assertThat(actual).isEqualTo("PUT.flow");
    }

    // REST Listener Config Title
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

    // Schema Response
    @Test
    void shouldReturnCorrectSchemaResponseFileNameWhenOperationIdPresent() {
        // Given
        NavigationPath navigationPath = create()
                .with(SegmentKey.OPERATION_ID, "getPetById")
                .with(SegmentKey.METHOD, "GET")
                .with(SegmentKey.PATH, "/order/{id}")
                .with(SegmentKey.RESPONSES)
                .with(SegmentKey.STATUS_CODE, "200")
                .with(SegmentKey.CONTENT)
                .with(SegmentKey.CONTENT_TYPE, "application/json");

        // When
        String actual = OpenApiUtils.requestResponseSchemaFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("getPetById_response_200_application_json.schema.json");
    }

    @Test
    void shouldReturnCorrectSchemaResponseFileNameWhenOperationIdNotPresent() {
        // Given
        NavigationPath navigationPath = create()
                .with(SegmentKey.METHOD, "GET")
                .with(SegmentKey.PATH, "/order/{id}")
                .with(SegmentKey.RESPONSES)
                .with(SegmentKey.STATUS_CODE, "200")
                .with(SegmentKey.CONTENT)
                .with(SegmentKey.CONTENT_TYPE, "application/json");

        // When
        String actual = OpenApiUtils.requestResponseSchemaFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("GETOrderId_response_200_application_json.schema.json");
    }

    @Test
    void shouldReturnCorrectSchemaResponseFileNameWhenOperationIdNotPresentAndRootPath() {
        // Given
        NavigationPath navigationPath = create()
                .with(SegmentKey.METHOD, "GET")
                .with(SegmentKey.PATH, "/")
                .with(SegmentKey.RESPONSES)
                .with(SegmentKey.STATUS_CODE, "200")
                .with(SegmentKey.CONTENT)
                .with(SegmentKey.CONTENT_TYPE, "application/json");

        // When
        String actual = OpenApiUtils.requestResponseSchemaFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("GET_response_200_application_json.schema.json");
    }

    // Schema Request Body

    @Test
    void shouldReturnCorrectSchemaRequestBodyFileNameWhenOperationIdPresent() {
        // Given
        NavigationPath navigationPath = create()
                .with(SegmentKey.OPERATION_ID, "getPetById")
                .with(SegmentKey.METHOD, "GET")
                .with(SegmentKey.PATH, "/order/{id}")
                .with(SegmentKey.REQUEST_BODY)
                .with(SegmentKey.CONTENT)
                .with(SegmentKey.CONTENT_TYPE, "application/x-www-form-urlencoded");

        // When
        String actual = OpenApiUtils.requestResponseSchemaFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("getPetById_requestBody_application_x-www-form-urlencoded.schema.json");
    }

    @Test
    void shouldReturnCorrectSchemaRequestBodyFileNameWhenOperationIdNotPresent() {
        // Given
        NavigationPath navigationPath = create()
                .with(SegmentKey.METHOD, "GET")
                .with(SegmentKey.PATH, "/order/{id}")
                .with(SegmentKey.REQUEST_BODY)
                .with(SegmentKey.CONTENT)
                .with(SegmentKey.CONTENT_TYPE, "application/x-www-form-urlencoded");

        // When
        String actual = OpenApiUtils.requestResponseSchemaFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("GETOrderId_requestBody_application_x-www-form-urlencoded.schema.json");
    }

    @Test
    void shouldReturnCorrectSchemaRequestBodyFileNameWhenOperationIdNotPresentAndRootPath() {
        // Given
        NavigationPath navigationPath = create()
                .with(SegmentKey.METHOD, "GET")
                .with(SegmentKey.PATH, "/")
                .with(SegmentKey.REQUEST_BODY)
                .with(SegmentKey.CONTENT)
                .with(SegmentKey.CONTENT_TYPE, "application/x-www-form-urlencoded");

        // When
        String actual = OpenApiUtils.requestResponseSchemaFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("GET_requestBody_application_x-www-form-urlencoded.schema.json");
    }

    // Schema Parameter
    @Test
    void shouldReturnCorrectSchemaParameterFileNameWhenOperationIdPresent() {
        // Given
        NavigationPath navigationPath = create()
                .with(SegmentKey.OPERATION_ID, "getPetById")
                .with(SegmentKey.METHOD, "GET")
                .with(SegmentKey.PATH, "/order/findByStatus")
                .with(SegmentKey.PARAMETERS)
                .with(SegmentKey.PARAMETER_NAME, "status");

        // When
        String actual = OpenApiUtils.parameterSchemaFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("getPetById_parameter_status.schema.json");
    }

    @Test
    void shouldReturnCorrectSchemaParameterFileNameWhenOperationIdNotPresent() {
        // Given
        NavigationPath navigationPath = create()
                .with(SegmentKey.METHOD, "GET")
                .with(SegmentKey.PATH, "/order/findByStatus")
                .with(SegmentKey.PARAMETERS)
                .with(SegmentKey.PARAMETER_NAME, "status");

        // When
        String actual = OpenApiUtils.parameterSchemaFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("GETOrderFindByStatus_parameter_status.schema.json");
    }

    @Test
    void shouldReturnCorrectSchemaParameterFileNameWhenOperationIdNotPresentAndRootPath() {
        // Given
        NavigationPath navigationPath = create()
                .with(SegmentKey.METHOD, "POST")
                .with(SegmentKey.PATH, "/")
                .with(SegmentKey.PARAMETERS)
                .with(SegmentKey.PARAMETER_NAME, "status");

        // When
        String actual = OpenApiUtils.parameterSchemaFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("POST_parameter_status.schema.json");
    }

    // Schema Header
    @Test
    void shouldReturnCorrectSchemaHeaderFileNameWhenOperationIdPresent() {
        // Given
        NavigationPath navigationPath = create()
                .with(SegmentKey.OPERATION_ID, "getPetById")
                .with(SegmentKey.METHOD, "GET")
                .with(SegmentKey.PATH, "/order/findByStatus")
                .with(SegmentKey.RESPONSES)
                .with(SegmentKey.STATUS_CODE, "200")
                .with(SegmentKey.HEADERS)
                .with(SegmentKey.HEADER_NAME, "X-Rate-Limit");

        // When
        String actual = OpenApiUtils.headerSchemaFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("getPetById_response_200_header_X-Rate-Limit.schema.json");
    }

    @Test
    void shouldReturnCorrectSchemaHeaderFileNameWhenOperationIdNotPresent() {
        // Given
        NavigationPath navigationPath = create()
                .with(SegmentKey.METHOD, "DELETE")
                .with(SegmentKey.PATH, "/order/findByStatus")
                .with(SegmentKey.RESPONSES)
                .with(SegmentKey.STATUS_CODE, "200")
                .with(SegmentKey.HEADERS)
                .with(SegmentKey.HEADER_NAME, "X-Rate-Limit");

        // When
        String actual = OpenApiUtils.headerSchemaFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("DELETEOrderFindByStatus_response_200_header_X-Rate-Limit.schema.json");
    }

    @Test
    void shouldReturnCorrectSchemaHeaderFileNameWhenOperationIdNotPresentAndRootPath() {
        // Given
        NavigationPath navigationPath = create()
                .with(SegmentKey.METHOD, "DELETE")
                .with(SegmentKey.PATH, "/")
                .with(SegmentKey.RESPONSES)
                .with(SegmentKey.STATUS_CODE, "200")
                .with(SegmentKey.HEADERS)
                .with(SegmentKey.HEADER_NAME, "X-Rate-Limit");

        // When
        String actual = OpenApiUtils.headerSchemaFileNameFrom(navigationPath, context);

        // Then
        assertThat(actual).isEqualTo("DELETE_response_200_header_X-Rate-Limit.schema.json");
    }

    // Example
    @Test
    void shouldReturnCorrectExampleFileNameWhenOperationIdPresent() {
        // Given
        NavigationPath navigationPath = create()
                .with(SegmentKey.OPERATION_ID, "getPetById")
                .with(SegmentKey.METHOD, "GET")
                .with(SegmentKey.PATH, "/order/{id}")
                .with(SegmentKey.RESPONSES)
                .with(SegmentKey.STATUS_CODE, "200")
                .with(SegmentKey.CONTENT)
                .with(SegmentKey.CONTENT_TYPE, "application/json");

        // When
        String actual = OpenApiUtils.exampleFileNameFrom(navigationPath, OpenApiExampleFormat.JSON);

        // Then
        assertThat(actual).isEqualTo("getPetById_response_200_application_json.example.json");
    }

    @Test
    void shouldReturnCorrectExampleFileNameWhenOperationIdNotPresent() {
        // Given
        NavigationPath navigationPath = create()
                .with(SegmentKey.METHOD, "GET")
                .with(SegmentKey.PATH, "/order/{id}")
                .with(SegmentKey.RESPONSES)
                .with(SegmentKey.STATUS_CODE, "200")
                .with(SegmentKey.CONTENT)
                .with(SegmentKey.CONTENT_TYPE, "application/json");

        // When
        String actual = OpenApiUtils.exampleFileNameFrom(navigationPath, OpenApiExampleFormat.JSON);

        // Then
        assertThat(actual).isEqualTo("GETOrderId_response_200_application_json.example.json");
    }

    @Test
    void shouldReturnCorrectExampleFileNameWhenOperationIdNotPresentAndRootPath() {
        // Given
        NavigationPath navigationPath = create()
                .with(SegmentKey.METHOD, "GET")
                .with(SegmentKey.PATH, "/")
                .with(SegmentKey.RESPONSES)
                .with(SegmentKey.STATUS_CODE, "200")
                .with(SegmentKey.CONTENT)
                .with(SegmentKey.CONTENT_TYPE, "application/json");

        // When
        String actual = OpenApiUtils.exampleFileNameFrom(navigationPath, OpenApiExampleFormat.JSON);

        // Then
        assertThat(actual).isEqualTo("GET_response_200_application_json.example.json");
    }

    @Test
    void shouldReturnCorrectExampleFileNameAsXmlWhenOperationIdPresent() {
        // Given
        NavigationPath navigationPath = create()
                .with(SegmentKey.OPERATION_ID, "getPetById")
                .with(SegmentKey.METHOD, "GET")
                .with(SegmentKey.PATH, "/order/{id}")
                .with(SegmentKey.RESPONSES)
                .with(SegmentKey.STATUS_CODE, "200")
                .with(SegmentKey.CONTENT)
                .with(SegmentKey.CONTENT_TYPE, "application/json");

        // When
        String actual = OpenApiUtils.exampleFileNameFrom(navigationPath, OpenApiExampleFormat.XML);

        // Then
        assertThat(actual).isEqualTo("getPetById_response_200_application_json.example.xml");
    }

    // Get API title
    @Test
    void shouldReturnCorrectApiTitle() {
        // Given
        InfoObject infoObject = new InfoObject();
        infoObject.setTitle("My Awesome API");
        OpenApiObject openApiObject = new OpenApiObject();
        openApiObject.setInfo(infoObject);

        // When
        String actual = OpenApiUtils.getApiTitle(openApiObject);

        // Then
        assertThat(actual).isEqualTo("My Awesome API");
    }

    @Test
    void shouldReturnDefaultApiTitle() {
        // Given
        OpenApiObject openApiObject = new OpenApiObject();

        // When
        String actual = OpenApiUtils.getApiTitle(openApiObject);

        // Then
        assertThat(actual).isEqualTo("My Api");
    }
}
