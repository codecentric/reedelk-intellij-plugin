package de.codecentric.reedelk.plugin.action.openapi.template;

import de.codecentric.reedelk.plugin.template.TemplateWriter;
import de.codecentric.reedelk.plugin.template.WritingStrategy;

public enum TemplateOpenApi implements TemplateWriter {

    FLOW_WITH_REST_LISTENER_AND_PAYLOAD_SET("OpenAPIRESTListenerWithPayloadSet.flow"),
    FLOW_WITH_REST_LISTENER_AND_RESOURCE("OpenAPIRESTListenerWithResource.flow"),
    REST_LISTENER_CONFIG("OpenAPIRESTListenerConfig.fconfig"),
    EXAMPLE("OpenAPIExample.txt"),
    ASSET("OpenAPIAsset.txt");

    private final String templateName;

    TemplateOpenApi(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public String templateName() {
        return templateName;
    }

    @Override
    public WritingStrategy writingStrategy() {
        return new WritingStrategyOpenApi();
    }
}
