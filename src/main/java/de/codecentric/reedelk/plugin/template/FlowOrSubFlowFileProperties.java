package de.codecentric.reedelk.plugin.template;

import de.codecentric.reedelk.plugin.commons.DefaultFlowOrSubflowDescription;
import de.codecentric.reedelk.plugin.commons.DefaultFlowOrSubflowTitle;

import java.util.Properties;
import java.util.UUID;

public class FlowOrSubFlowFileProperties extends Properties {

    public FlowOrSubFlowFileProperties(String id, String title, String description, String configId) {
        setProperty("id", id);
        setProperty("title", title);
        setProperty("configId", configId);
        setProperty("description", description);
    }

    public FlowOrSubFlowFileProperties(String fileName, String templateName) {
        this(UUID.randomUUID().toString(),
                DefaultFlowOrSubflowTitle.from(fileName),
                DefaultFlowOrSubflowDescription.from(fileName, templateName));
    }

    public FlowOrSubFlowFileProperties(String id, String title, String description) {
        setProperty("id", id);
        setProperty("title", title);
        setProperty("description", description);
    }
}
