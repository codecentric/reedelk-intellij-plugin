package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.metadata.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.lang.String.format;

class PlatformComponentMetadataService implements PlatformModuleService {

    private static final Logger LOG = Logger.getInstance(PlatformComponentMetadataService.class);

    private final Module module;
    private final TypeAndTries typeAndTries;
    private final SuggestionFinder suggestionFinder;
    private final PlatformModuleService moduleService;
    private final OnComponentMetadataEvent onComponentMetadataEvent;
    private final MetadataExpectedInputDTOBuilder metadataExpectedInputDTOBuilder;

    public PlatformComponentMetadataService(@NotNull Module module,
                                            @NotNull PlatformModuleService moduleService,
                                            @NotNull SuggestionFinder suggestionFinder,
                                            @NotNull TypeAndTries typeAndTries) {
        this.module = module;
        this.typeAndTries = typeAndTries;
        this.moduleService = moduleService;
        this.suggestionFinder = suggestionFinder;
        this.metadataExpectedInputDTOBuilder = new MetadataExpectedInputDTOBuilder(moduleService, this.typeAndTries);
        this.onComponentMetadataEvent = module.getProject().getMessageBus().syncPublisher(Topics.ON_COMPONENT_IO);
    }

    PreviousComponentOutput componentOutputOf(ComponentContext context) {
        return DiscoveryStrategyFactory
                .get(module, moduleService, typeAndTries, context, context.node())
                .orElse(null); // TODO: Should it be default?
    }

    @Override
    public void componentMetadataOf(@NotNull ComponentContext context) {
        PluginExecutors.run(module, message("component.io.ticker.text"), indicator -> {
            try {
                Optional<PreviousComponentOutput> componentOutput =
                        DiscoveryStrategyFactory.get(module, moduleService, typeAndTries, context, context.node());

                // TODO: Get might fail, consider to return default output! This might
                //  happen for instance when we want to find the previous component of flow
                //  reference but we have not selected any subflow in the flow reference.
                PreviousComponentOutput previousComponentOutput = componentOutput.get();
                List<MetadataTypeDTO> payload = previousComponentOutput.mapPayload(suggestionFinder, typeAndTries);
                MetadataTypeDTO attributes = previousComponentOutput.mapAttributes(suggestionFinder, typeAndTries);
                String description = previousComponentOutput.description();

                MetadataActualInputDTO actualInput = new MetadataActualInputDTO(attributes, payload, description);
                        MetadataExpectedInputDTO expectedInput = metadataExpectedInputDTOBuilder.build(context);
                MetadataDTO componentMetadata = new MetadataDTO(actualInput, expectedInput);
                onComponentMetadataEvent.onComponentMetadataUpdated(componentMetadata);

            } catch (Exception exception) {
                LOG.warn(exception);

                String componentFullyQualifiedName = context.node().componentData().getFullyQualifiedName();
                String errorMessage =
                        format("Component metadata could not be found for component=[%s], cause=[%s]", componentFullyQualifiedName, exception);
                PluginException wrapped = new PluginException(errorMessage, exception);
                onComponentMetadataEvent.onComponentMetadataError(wrapped);
            }
        });
    }
}
