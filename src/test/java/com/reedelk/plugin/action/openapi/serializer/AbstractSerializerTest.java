package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.serializer.Serializers;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import com.reedelk.plugin.action.openapi.OpenApiSchemaFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
abstract class AbstractSerializerTest {

    @Mock
    protected OpenApiImporterContext context;
    protected SerializerContext serializerContext;

    @BeforeEach
    void setUp() {
        doReturn(OpenApiSchemaFormat.YAML).when(context).getSchemaFormat();
        Serializers serializers = new Serializers(Serializer.serializers(context));
        serializerContext = new SerializerContext(serializers);
    }
}
