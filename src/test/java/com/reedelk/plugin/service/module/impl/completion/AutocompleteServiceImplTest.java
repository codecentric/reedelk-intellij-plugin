package com.reedelk.plugin.service.module.impl.completion;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.service.module.ComponentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

// TODO: Fixme
@ExtendWith(MockitoExtension.class)
class AutocompleteServiceImplTest {

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

    private TestableAutocompleteService service;

    @BeforeEach
    void setUp() {
        doReturn(mockMessageBus).when(mockProject).getMessageBus();
        doReturn(mockMessageBusConnection).when(mockMessageBus).connect();
        service = spy(new TestableAutocompleteService(mockProject, mockModule));

        doNothing().when(service).fireCompletionsUpdatedEvent();
        doReturn(mockComponentService).when(service).componentService();

        //service.initializeAutocomplete();
    }
/**
    @Test
    void shouldReturnEmptyCompletionTokens() {
        // Given
        String searchToken = "s";

        // When
        List<Suggestion> suggestions = service.completionTokensOf(COMPONENT_QUALIFIED_NAME, searchToken);

        // Then
        PluginAssertion.assertThat(suggestions).isEmpty();
    }

    @Test
    void shouldReturnMessageCompletionToken() {
        // Given
        String searchToken = "mes";

        // When
        List<Suggestion> suggestions = service.completionTokensOf(COMPONENT_QUALIFIED_NAME, searchToken);

        // Then
        PluginAssertion.assertThat(suggestions)
                .contains("message", SuggestionType.VARIABLE, "Message")
                .hasSize(1);
    }

    @Test
    void shouldReturnSpecificComponentSuggestions() {
        // Given
        String fullyQualifiedName = "com.reedelk.components.MyCustomComponent";
        AutoCompleteContributorDescriptor autoCompleteContribution = new AutoCompleteContributorDescriptor();
        autoCompleteContribution.setContributions(
                asList("messageCustom1[VARIABLE:MyType1]",
                        "messageCustom2[VARIABLE:MyType2]",
                        "messageCustom3[VARIABLE:MyType3]"));

        PropertyDescriptor propertyDescriptor = PropertyDescriptor.builder()
                .name("myPropertyWithAutoComplete")
                .type(ObjectFactories.createTypeDynamicValueDescriptor(DynamicString.class))
                .autoCompleteContributor(autoCompleteContribution)
                .build();

        ModuleComponents moduleComponents = createModuleComponentsWith(fullyQualifiedName, propertyDescriptor);
        List<ModuleComponents> allComponents = Collections.singletonList(moduleComponents);

        service.updateComponentsSuggestions(allComponents);

        // When
        List<Suggestion> suggestions = service.completionTokensOf(fullyQualifiedName, "mes");

        // Then
        PluginAssertion.assertThat(suggestions)
                .contains("messageCustom1", SuggestionType.VARIABLE, "MyType1")
                .contains("messageCustom2", SuggestionType.VARIABLE, "MyType2")
                .contains("messageCustom3", SuggestionType.VARIABLE, "MyType3")
                .hasSize(3);
    }

    @Test
    void shouldReturnCustomFunctionsSuggestions() {
        // Given
        AutoCompleteContributorDescriptor utilsDefinitions = new AutoCompleteContributorDescriptor(Arrays.asList(
                "Util[VARIABLE:Util]",
                "Util.tmpdir()[FUNCTION:String]",
                "Util.uuid()[FUNCTION:String]"));

        AutoCompleteContributorDescriptor loggerDefinitions = new AutoCompleteContributorDescriptor(Arrays.asList(
                "Log[VARIABLE:Log]",
                "Log.info('')[FUNCTION:void]",
                "Log.debug('')[FUNCTION:void]",
                "Log.warn('')[FUNCTION:void]",
                "Log.error('')[FUNCTION:void]",
                "Log.trace('')[FUNCTION:void]"));
        List<AutoCompleteContributorDescriptor> definitions =
                Arrays.asList(loggerDefinitions, utilsDefinitions);

        doReturn(definitions).when(mockComponentService).getAutoCompleteItemDescriptors();

        service.updateComponentsSuggestions(Collections.emptyList());

        // When
        List<Suggestion> suggestions = service.completionTokensOf(COMPONENT_QUALIFIED_NAME, "Log.");

        // Then
        PluginAssertion.assertThat(suggestions)
                .contains("info('')", SuggestionType.FUNCTION, "void")
                .contains("debug('')", SuggestionType.FUNCTION, "void")
                .contains("warn('')", SuggestionType.FUNCTION, "void")
                .contains("error('')", SuggestionType.FUNCTION, "void")
                .contains("trace('')", SuggestionType.FUNCTION, "void")
                .hasSize(5);
    }

    @Test
    void shouldUpdateComponentsSuggestionFireCompletionsUpdatedEvent() {
        // When
        service.updateComponentsSuggestions(Collections.emptyList());

        // Then
        // One time on initialization, the second time due  to the call above.
        verify(service, times(2)).fireCompletionsUpdatedEvent();
    }

    private ModuleComponents createModuleComponentsWith(String componentFullyQualifiedName, PropertyDescriptor propertyDescriptor) {
        List<PropertyDescriptor> propertyDescriptors = Collections.singletonList(propertyDescriptor);
        ComponentDescriptor descriptor = ComponentDescriptor.create()
                .fullyQualifiedName(componentFullyQualifiedName)
                .propertyDescriptors(propertyDescriptors)
                .build();
        List<ComponentDescriptor> componentDescriptors = Collections.singletonList(descriptor);
        return new ModuleComponents("my-module", componentDescriptors);
    }
*/
    static class TestableAutocompleteService extends AutocompleteServiceImpl {

        TestableAutocompleteService(Project project, Module module) {
            super(project, module);
        }

    }
}
