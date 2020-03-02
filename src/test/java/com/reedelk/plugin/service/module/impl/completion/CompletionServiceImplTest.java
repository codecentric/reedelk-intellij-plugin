package com.reedelk.plugin.service.module.impl.completion;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.module.descriptor.ModuleDescriptor;
import com.reedelk.module.descriptor.model.AutocompleteVariableDescriptor;
import com.reedelk.module.descriptor.model.ComponentDescriptor;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.testutils.ObjectFactories;
import com.reedelk.runtime.api.autocomplete.AutocompleteType;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompletionServiceImplTest {

    private final String COMPONENT_QUALIFIED_NAME = "com.reedelk.components.TestComponent";

    @Mock
    private Project mockProject;
    @Mock
    private Module mockModule;
    @Mock
    private MessageBus mockMessageBus;
    @Mock
    private ComponentService mockComponentService;
    @Mock
    private MessageBusConnection mockMessageBusConnection;

    private TestableCompletionService service;

    @BeforeEach
    void setUp() {
        doReturn(mockMessageBus).when(mockProject).getMessageBus();
        doReturn(mockMessageBusConnection).when(mockMessageBus).connect();
        service = spy(new TestableCompletionService(mockProject, mockModule));

        doNothing().when(service).fireCompletionsUpdatedEvent();
        lenient().doReturn(mockComponentService).when(service).componentService();

        service.updateAutocomplete(Collections.emptyList());
    }

    @Test
    void shouldReturnEmptyCompletionTokens() {
        // Given
        String searchToken = "s";

        // When
        List<Suggestion> suggestions = service.autocompleteSuggestionOf(COMPONENT_QUALIFIED_NAME, searchToken);

        // Then
        PluginAssertion.assertThat(suggestions).isEmpty();
    }

    @Test
    void shouldReturnDefaultMessageCompletionToken() {
        // Given
        String searchToken = "mes";

        // When
        List<Suggestion> suggestions = service.autocompleteSuggestionOf(COMPONENT_QUALIFIED_NAME, searchToken);

        // Then
        PluginAssertion.assertThat(suggestions)
                .contains("message", AutocompleteType.VARIABLE)
                .hasSize(1);
    }

    @Test
    void shouldReturnSpecificComponentSuggestions() {
        // Given
        AutocompleteVariableDescriptor var1 = new AutocompleteVariableDescriptor();
        var1.setType("String");
        var1.setName("var1");
        AutocompleteVariableDescriptor var2 = new AutocompleteVariableDescriptor();
        var2.setType("int");
        var2.setName("var2");

        PropertyDescriptor propertyDescriptor = PropertyDescriptor.builder()
                .name("myPropertyWithAutoComplete")
                .type(ObjectFactories.createTypeDynamicValueDescriptor(DynamicString.class))
                .autocompleteVariable(var1)
                .autocompleteVariable(var2)
                .build();

        ModuleDescriptor moduleDescriptor = createModuleDescriptor(COMPONENT_QUALIFIED_NAME, propertyDescriptor);

        service.updateAutocomplete(Collections.singletonList(moduleDescriptor));

        // When
        List<Suggestion> suggestions = service.autocompleteSuggestionOf(COMPONENT_QUALIFIED_NAME, "va");

        // Then
        PluginAssertion.assertThat(suggestions)
                .hasSize(2)
                .contains("var1", AutocompleteType.VARIABLE)
                .contains("var2", AutocompleteType.VARIABLE);
    }

    @Test
    void shouldUpdateComponentsSuggestionFireCompletionsUpdatedEvent() {
        // When
        service.updateAutocomplete(Collections.emptyList());

        // Then
        // One for init and one for the updateAutocomplete above.
        verify(service, times(2)).fireCompletionsUpdatedEvent();
    }

    private ModuleDescriptor createModuleDescriptor(String componentFullyQualifiedName, PropertyDescriptor propertyDescriptor) {
        List<PropertyDescriptor> propertyDescriptors = Collections.singletonList(propertyDescriptor);
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .fullyQualifiedName(componentFullyQualifiedName)
                .propertyDescriptors(propertyDescriptors)
                .build();
        List<ComponentDescriptor> componentDescriptors = Collections.singletonList(descriptor);
        ModuleDescriptor moduleDescriptor = new ModuleDescriptor();
        moduleDescriptor.setName("my-module");
        moduleDescriptor.setComponents(componentDescriptors);
        return moduleDescriptor;
    }

    static class TestableCompletionService extends CompletionServiceImpl {
        TestableCompletionService(Project project, Module module) {
            super(project, module);
        }

        @Override
        void initialize() {
            // Do nothing
        }
    }
}
