package com.reedelk.plugin.action.openapi.handler;

import com.reedelk.openapi.v3.model.*;
import com.reedelk.plugin.action.openapi.OpenApiExampleFormat;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.plugin.template.AssetProperties;
import com.reedelk.plugin.template.OpenAPIRESTListenerWithPayloadSet;
import com.reedelk.plugin.template.OpenAPIRESTListenerWithResource;
import com.reedelk.runtime.api.commons.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractHandlerTest {

    @Captor
    private ArgumentCaptor<OpenAPIRESTListenerWithResource> restListenerWithResource;
    @Captor
    private ArgumentCaptor<OpenAPIRESTListenerWithPayloadSet> restListenerWithPayload;

    @Mock
    private OpenApiImporterContext context;

    private Handler handler = new GETHandler();

    @Test
    void shouldCorrectlyCreateRestListenerFlowWithPayload() {
        // Given
        doReturn("aa-bb-cc").when(context).getRestListenerConfigId();

        OperationObject operationObject = new OperationObject();

        Map<RestMethod, OperationObject> operationObjectMap =
                ImmutableMap.of(RestMethod.GET, operationObject);

        // When
        handler.accept(context, "/order/{id}", operationObjectMap);

        // Then
        verify(context)
                .createRestListenerFlow(anyString(), restListenerWithPayload.capture());
    }

    @Test
    void shouldCorrectlyCreateRestListenerFlowWithResourceExample() {
        // Given
        // context.createAsset
        String assetPath = "assets/test-example.example.json";
        doReturn(assetPath)
                .when(context)
                .createAsset(anyString(), any(AssetProperties.class));

        String exampleJson = "{ \"name\": \"John\" }";
        doReturn(OpenApiExampleFormat.JSON).when(context).exampleFormatOf(exampleJson);
        doReturn("aa-bb-cc").when(context).getRestListenerConfigId();

        MediaTypeObject mediaTypeObject = new MediaTypeObject();
        mediaTypeObject.setExample(new Example(exampleJson));

        ResponseObject responseObject = new ResponseObject();
        responseObject.setContent(ImmutableMap.of("application/json", mediaTypeObject));

        Map<String, ResponseObject> responseObjects = new HashMap<>();
        responseObjects.put("200", responseObject);

        OperationObject operationObject = new OperationObject();
        operationObject.setResponses(responseObjects);

        Map<RestMethod, OperationObject> operationObjectMap =
                ImmutableMap.of(RestMethod.GET, operationObject);

        // When
        handler.accept(context, "/order/{id}", operationObjectMap);

        // Then
        verify(context)
                .createRestListenerFlowWithExample(anyString(), restListenerWithResource.capture());
        verify(context, never())
                .createRestListenerFlow(anyString(), any(OpenAPIRESTListenerWithPayloadSet.class));
    }
}
