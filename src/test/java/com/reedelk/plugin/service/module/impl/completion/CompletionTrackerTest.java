package com.reedelk.plugin.service.module.impl.completion;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.service.module.PlatformModuleService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompletionTrackerTest {

    // TODO: Fixthese tests
    private final String COMPONENT_QUALIFIED_NAME = "com.reedelk.components.TestComponent";

    @Mock
    private Project mockProject;
    @Mock
    private Module mockModule;
    @Mock
    private MessageBus mockMessageBus;
    @Mock
    private PlatformModuleService mockPlatformModuleService;
    @Mock
    private MessageBusConnection mockMessageBusConnection;

    /**
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
        String[] searchToken = new String[]{"s"};

        // When
        List<Suggestion> suggestions = service.autocompleteSuggestionOf(COMPONENT_QUALIFIED_NAME, searchToken);

        // Then
        PluginAssertion.assertThat(suggestions).isEmpty();
    }

    @Test
    void shouldReturnDefaultMessageCompletionToken() {
        // Given
        String[] searchToken = new String[] {"mes"};

        // When
        List<Suggestion> suggestions = service.autocompleteSuggestionOf(COMPONENT_QUALIFIED_NAME, searchToken);

        // Then
        PluginAssertion.assertThat(suggestions)
                .contains("message", AutocompleteItemType.VARIABLE)
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
        List<Suggestion> suggestions = service.autocompleteSuggestionOf(COMPONENT_QUALIFIED_NAME, new String[]{"va"});

        // Then
        PluginAssertion.assertThat(suggestions)
                .hasSize(2)
                .contains("var1", AutocompleteItemType.VARIABLE)
                .contains("var2", AutocompleteItemType.VARIABLE);
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
                .properties(propertyDescriptors)
                .build();
        List<ComponentDescriptor> componentDescriptors = Collections.singletonList(descriptor);
        ModuleDescriptor moduleDescriptor = new ModuleDescriptor();
        moduleDescriptor.setName("my-module");
        moduleDescriptor.setComponents(componentDescriptors);
        return moduleDescriptor;
    }

    static class TestableCompletionService extends CompletionTracker {
        TestableCompletionService(Project project, Module module) {
            super(project, module);
        }

        @Override
        void initialize() {
            // Do nothing
        }
    }*/
}