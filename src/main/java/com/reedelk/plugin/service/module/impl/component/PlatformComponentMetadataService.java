package com.reedelk.plugin.service.module.impl.component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

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

    private final Cache<String, Optional<PreviousComponentOutput>> cache;

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

        // Initialize cache used to avoid computing every time the same
        // discovery strategy for the same component context. The key of
        // the cache is the component context's uuid.
        cache = CacheBuilder
                .newBuilder()
                .maximumSize(1)
                .expireAfterAccess(Duration.ofMinutes(5))
                .build();
    }

    @Nullable
    PreviousComponentOutput componentOutputOf(ComponentContext context) {
        return outputFrom(context).orElse(null);
    }

    @Override
    public void componentMetadataOf(@NotNull ComponentContext context) {
        PluginExecutors.run(module, message("component.io.ticker.text"), indicator -> {
            try {
                Optional<PreviousComponentOutput> componentOutput = outputFrom(context);

                MetadataActualInputDTO actualInput;
                if (componentOutput.isPresent()) {

                    PreviousComponentOutput previousComponentOutput = componentOutput.get();
                    if (previousComponentOutput instanceof PreviousComponentOutputMultipleMessages) {
                        actualInput = new MetadataActualInputDTO(null, null, null, true, false);

                    } else {
                        List<MetadataTypeDTO> payload = previousComponentOutput.mapPayload(suggestionFinder, typeAndTries);
                        MetadataTypeDTO attributes = previousComponentOutput.mapAttributes(suggestionFinder, typeAndTries);
                        String description = previousComponentOutput.description();
                        actualInput = new MetadataActualInputDTO(attributes, payload, description);
                    }

                } else {
                    actualInput = new MetadataActualInputDTO(true);
                }

                MetadataExpectedInputDTO expectedInput = metadataExpectedInputDTOBuilder.build(context);
                MetadataDTO componentMetadata = new MetadataDTO(actualInput, expectedInput);
                onComponentMetadataEvent.onComponentMetadata(componentMetadata);

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

    private Optional<PreviousComponentOutput> outputFrom(ComponentContext context) {
        try {
            return cache.get(
                    context.getUuid(),
                    () -> DiscoveryStrategyFactory.get(module, moduleService, typeAndTries, context, context.node()));
        } catch (ExecutionException e) {
            return Optional.empty();
        }
    }
}
