package com.reedelk.plugin.component.type.generic;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeDefault;
import com.reedelk.plugin.service.module.impl.component.metadata.*;
import com.reedelk.runtime.api.annotation.ComponentOutput;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.MessageAttributeType;
import static com.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.MyItemType;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GenericComponentDiscoveryTest extends AbstractComponentDiscoveryTest {

    private GenericComponentDiscovery discovery;

    @BeforeEach
    public void setUp() {
        super.setUp();
        discovery = spy(new GenericComponentDiscovery(module, moduleService, typeAndTries));
    }

    @Test
    void shouldReturnDefaultPreviousComponentOutputWhenComponentDescriptorOutputIsNull() {
        // Given
        ComponentContext componentContext = mockComponentContext(null);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, componentNode2);

        // Then
        assertThat(maybeActualOutput).contains(DiscoveryStrategy.DEFAULT);
    }

    @Test
    void shouldReturnCorrectCompoundComponentOutput() {
        // Given
        List<String> payload = singletonList(MyItemType.class.getName());
        List<String> attributes = singletonList(MessageAttributeType.class.getName());
        String description = "My test description";

        ComponentOutputDescriptor outputDescriptor = new ComponentOutputDescriptor();
        outputDescriptor.setDescription(description);
        outputDescriptor.setAttributes(attributes);
        outputDescriptor.setPayload(payload);

        ComponentContext componentContext = mockComponentContext(outputDescriptor);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, componentNode2);

        // Then
        PreviousComponentOutputDefault expectedPayloadOutput =
                new PreviousComponentOutputDefault(attributes, payload, description);
        PreviousComponentOutputDefault expectedAttributeOutput =
                new PreviousComponentOutputDefault(attributes, payload, description);
        PreviousComponentOutputCompound expectedPreviousOutput  =
                new PreviousComponentOutputCompound(expectedAttributeOutput, expectedPayloadOutput);
        assertThat(maybeActualOutput).contains(expectedPreviousOutput);
    }

    @Test
    void shouldReturnDefaultPayloadWhenPreviousComponentIsEmpty() {
        // Given
        List<String> payload = singletonList(ComponentOutput.PreviousComponent.class.getName());
        List<String> attributes = singletonList(MessageAttributeType.class.getName());
        String description = "My test description";

        ComponentOutputDescriptor outputDescriptor = new ComponentOutputDescriptor();
        outputDescriptor.setDescription(description);
        outputDescriptor.setAttributes(attributes);
        outputDescriptor.setPayload(payload);

        ComponentContext componentContext = mockComponentContext(outputDescriptor);

        doReturn(Optional.empty()).when(discovery)
                .discover(componentContext, componentNode2);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, componentNode2);

        // Then
        // Should use the default payload because the previous of the previous was empty
        PreviousComponentOutput expectedPayloadOutput =
                new PreviousComponentOutputDefault(
                        singletonList(TypeDefault.DEFAULT_ATTRIBUTES),
                        singletonList(TypeDefault.DEFAULT_PAYLOAD));

        // Attributes are taken from the previous component.
        PreviousComponentOutput expectedAttributeOutput =
                new PreviousComponentOutputDefault(attributes, payload, description);

        PreviousComponentOutputCompound expectedPreviousOutput  =
                new PreviousComponentOutputCompound(expectedAttributeOutput, expectedPayloadOutput);
        assertThat(maybeActualOutput).contains(expectedPreviousOutput);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    void shouldReturnPreviousComponentPayload() {
        // Given
        PreviousComponentOutput previousPreviousComponentOutput = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                asList(MyTestType.class.getName(), String.class.getName()),
                "Description of the previous");

        List<String> payload = singletonList(ComponentOutput.PreviousComponent.class.getName());
        List<String> attributes = singletonList(MessageAttributeType.class.getName());
        String description = "My test description";

        ComponentOutputDescriptor outputDescriptor = new ComponentOutputDescriptor();
        outputDescriptor.setDescription(description);
        outputDescriptor.setAttributes(attributes);
        outputDescriptor.setPayload(payload);

        ComponentContext componentContext = mockComponentContext(outputDescriptor);

        doReturn(Optional.of(previousPreviousComponentOutput))
                .when(discovery)
                .discover(componentContext, componentNode2);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, componentNode2);

        // Then
        // Should use the default payload because the previous of the previous was empty
        PreviousComponentOutput expectedPayloadOutput = previousPreviousComponentOutput;

        // Attributes are taken from the previous component.
        PreviousComponentOutput expectedAttributeOutput =
                new PreviousComponentOutputDefault(attributes, payload, description);

        PreviousComponentOutputCompound expectedPreviousOutput  =
                new PreviousComponentOutputCompound(expectedAttributeOutput, expectedPayloadOutput);
        assertThat(maybeActualOutput).contains(expectedPreviousOutput);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Test
    void shouldReturnPreviousComponentAttributes() {
        // Given
        PreviousComponentOutput previousPreviousComponentOutput = new PreviousComponentOutputDefault(
                singletonList(MyTestType.class.getName()),
                singletonList(String.class.getName()),
                "Description of the previous");

        List<String> payload = singletonList(Long.class.getName());
        List<String> attributes = singletonList(ComponentOutput.PreviousComponent.class.getName());
        String description = "My test description";

        ComponentOutputDescriptor outputDescriptor = new ComponentOutputDescriptor();
        outputDescriptor.setDescription(description);
        outputDescriptor.setAttributes(attributes);
        outputDescriptor.setPayload(payload);

        ComponentContext componentContext = mockComponentContext(outputDescriptor);

        doReturn(Optional.of(previousPreviousComponentOutput))
                .when(discovery)
                .discover(componentContext, componentNode2);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, componentNode2);

        // Then
        // Should use the default payload because the previous of the previous was empty
        PreviousComponentOutput expectedPayloadOutput =
                new PreviousComponentOutputDefault(attributes, payload, description);

        // Attributes are taken from the previous of the previous component.
        PreviousComponentOutput expectedAttributeOutput = previousPreviousComponentOutput;

        PreviousComponentOutputCompound expectedPreviousOutput  =
                new PreviousComponentOutputCompound(expectedAttributeOutput, expectedPayloadOutput);
        assertThat(maybeActualOutput).contains(expectedPreviousOutput);
    }

    @Test
    void shouldReturnInferFromDynamicExpressionComponentOutput() {
        // Given
        PreviousComponentOutput previousPreviousComponentOutput = new PreviousComponentOutputDefault(
                singletonList(MyTestType.class.getName()),
                singletonList(String.class.getName()),
                "Description of the previous");

        List<String> payload = singletonList(ComponentOutput.InferFromDynamicProperty.class.getName());
        List<String> attributes = singletonList(MessageAttributes.class.getName());
        String description = "My test description";

        ComponentOutputDescriptor outputDescriptor = new ComponentOutputDescriptor();
        outputDescriptor.setDescription(description);
        outputDescriptor.setAttributes(attributes);
        outputDescriptor.setDynamicPropertyName("myPayloadProperty");
        outputDescriptor.setPayload(payload);

        ComponentContext componentContext = mockComponentContext(outputDescriptor);

        String dynamicPropertyExpression = "#[message.payload().myMethod()]";
        ComponentData componentData = mock(ComponentData.class);
        doReturn(componentNode2.componentData().getFullyQualifiedName())
                .when(componentData)
                .getFullyQualifiedName();
        doReturn(dynamicPropertyExpression)
                .when(componentData)
                .get("myPayloadProperty");
        doReturn(true)
                .when(componentData)
                .has("myPayloadProperty");
        doReturn(componentData)
                .when(componentNode2)
                .componentData();
        doReturn(Optional.of(previousPreviousComponentOutput))
                .when(discovery)
                .discover(componentContext, componentNode2);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, componentNode2);

        // Then
        // Should use the default payload because the previous of the previous was empty
        PreviousComponentOutput expectedPayloadOutput =
                new PreviousComponentOutputInferFromDynamicExpression(previousPreviousComponentOutput, dynamicPropertyExpression);

        // Attributes are taken from the previous of the previous component.
        PreviousComponentOutput expectedAttributeOutput =
                new PreviousComponentOutputDefault(attributes, payload, description);

        PreviousComponentOutputCompound expectedPreviousOutput  =
                new PreviousComponentOutputCompound(expectedAttributeOutput, expectedPayloadOutput);
        assertThat(maybeActualOutput).contains(expectedPreviousOutput);
    }
}
