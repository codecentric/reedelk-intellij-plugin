package com.reedelk.plugin.service.module.impl.completion;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.component.domain.*;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.service.module.impl.component.ModuleComponents;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
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
        doReturn(mockComponentService).when(service).getComponentService();
        doNothing().when(service).fireCompletionsUpdatedEvent();
        service.initializeSuggestions();
    }

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
        AutoCompleteContributorDefinition autoCompleteContribution =
                new AutoCompleteContributorDefinition(false, false, false, asList(
                        "messageCustom1[VARIABLE:MyType1]",
                        "messageCustom2[VARIABLE:MyType2]",
                        "messageCustom3[VARIABLE:MyType3]"));

        ComponentPropertyDescriptor propertyDescriptor = ComponentPropertyDescriptor.builder()
                .propertyName("myPropertyWithAutoComplete")
                .type(new TypeDynamicValueDescriptor<>(DynamicString.class))
                .autoComplete(autoCompleteContribution)
                .build();

        ModuleComponents moduleComponents = createModuleComponentsWith(fullyQualifiedName, propertyDescriptor);
        List<ModuleComponents> allComponents = Collections.singletonList(moduleComponents);
        doReturn(allComponents).when(mockComponentService).getModuleComponents();

        service.updateComponentsSuggestions();

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
        AutoCompleteContributorDefinition utilsDefinitions = new AutoCompleteContributorDefinition(Arrays.asList(
                "Util[VARIABLE:Util]",
                "Util.tmpdir()[FUNCTION:String]",
                "Util.uuid()[FUNCTION:String]"));

        AutoCompleteContributorDefinition loggerDefinitions = new AutoCompleteContributorDefinition(Arrays.asList(
                "Log[VARIABLE:Log]",
                "Log.info('')[FUNCTION:void]",
                "Log.debug('')[FUNCTION:void]",
                "Log.warn('')[FUNCTION:void]",
                "Log.error('')[FUNCTION:void]",
                "Log.trace('')[FUNCTION:void]"));
        List<AutoCompleteContributorDefinition> definitions =
                Arrays.asList(loggerDefinitions, utilsDefinitions);

        doReturn(Collections.emptyList()).when(mockComponentService).getModuleComponents();
        doReturn(definitions).when(mockComponentService).getAutoCompleteContributorDefinition();

        service.updateComponentsSuggestions();

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
        // Given
        doReturn(Collections.emptyList()).when(mockComponentService).getModuleComponents();

        // When
        service.updateComponentsSuggestions();

        // Then
        // One time on initialization, the second time due  to the call above.
        verify(service, times(2)).fireCompletionsUpdatedEvent();
    }

    private ModuleComponents createModuleComponentsWith(String componentFullyQualifiedName, ComponentPropertyDescriptor propertyDescriptor) {
        List<ComponentPropertyDescriptor> propertyDescriptors = Collections.singletonList(propertyDescriptor);
        ComponentDescriptor descriptor = ComponentDefaultDescriptor.create()
                .fullyQualifiedName(componentFullyQualifiedName)
                .propertyDescriptors(propertyDescriptors)
                .build();
        List<ComponentDescriptor> componentDescriptors = Collections.singletonList(descriptor);
        return new ModuleComponents("my-module", componentDescriptors);
    }

    class TestableCompletionService extends CompletionServiceImpl {

        TestableCompletionService(Project project, Module module) {
            super(project, module);
        }

        @Override
        void initializeAsync() {
            // do nothing
        }
    }
}