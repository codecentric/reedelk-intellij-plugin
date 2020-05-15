package com.reedelk.plugin.service.module.impl.component;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.executor.PluginExecutors;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.completion.CompletionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.metadata.*;
import org.jetbrains.annotations.NotNull;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.lang.String.format;

class PlatformComponentMetadataService implements PlatformModuleService {

    private final Module module;
    private final TypeAndTries typeAndAndTries;
    private final OnComponentMetadataEvent onComponentMetadataEvent;
    private final MetadataExpectedInputDTOBuilder metadataExpectedInputDTOBuilder;
    private final MetadataActualInputDTOBuilder metadataActualInputBuilder;
    private final PlatformModuleService moduleService;

    public PlatformComponentMetadataService(@NotNull Module module,
                                            @NotNull PlatformModuleService moduleService,
                                            @NotNull CompletionFinder completionFinder,
                                            @NotNull TypeAndTries typesMap) {
        this.module = module;
        this.moduleService = moduleService;
        this.typeAndAndTries = typesMap;
        this.metadataExpectedInputDTOBuilder = new MetadataExpectedInputDTOBuilder(moduleService, typeAndAndTries);
        this.metadataActualInputBuilder = new MetadataActualInputDTOBuilder(module, moduleService, completionFinder, typeAndAndTries);
        this.onComponentMetadataEvent = module.getProject().getMessageBus().syncPublisher(Topics.ON_COMPONENT_IO);
    }

    ComponentOutputDescriptor componentOutputOf(ComponentContext context) {
        return DiscoveryStrategyFactory
                .get(module, moduleService, typeAndAndTries, context, context.node())
                .orElse(null);
    }

    @Override
    public void componentMetadataOf(@NotNull ComponentContext context) {
        PluginExecutors.run(module, message("component.io.ticker.text"), indicator -> {
            try {
                MetadataActualInputDTO actualInput = metadataActualInputBuilder.build(context);
                MetadataExpectedInputDTO expectedInput = metadataExpectedInputDTOBuilder.build(context);
                MetadataDTO componentMetadata = new MetadataDTO(actualInput, expectedInput);
                onComponentMetadataEvent.onComponentMetadataUpdated(componentMetadata);

            } catch (Exception exception) {
                String componentFullyQualifiedName = context.node().componentData().getFullyQualifiedName();
                String errorMessage =
                        format("Component metadata could not be found for component=[%s], cause=[%s]", componentFullyQualifiedName, exception);
                PluginException wrapped = new PluginException(errorMessage, exception);
                onComponentMetadataEvent.onComponentMetadataError(wrapped);
            }
        });
    }
}
