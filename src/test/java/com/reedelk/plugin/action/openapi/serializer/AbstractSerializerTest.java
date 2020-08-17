package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.serializer.Serializers;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
abstract class AbstractSerializerTest {

    @Mock
    protected OpenApiImporterContext context;
    protected SerializerContext serializerContext;

    @BeforeEach
    void setUp() {
        Serializers serializers = new Serializers(Serializer.serializers(context));
        serializerContext = new SerializerContext(serializers);
    }
}
