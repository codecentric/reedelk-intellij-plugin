package com.reedelk.plugin.template;

import com.reedelk.plugin.commons.DefaultFlowOrSubflowDescription;
import com.reedelk.plugin.commons.DefaultFlowOrSubflowTitle;

import java.util.Properties;
import java.util.UUID;

public class FlowOrSubFlowFileProperties extends Properties {

    public FlowOrSubFlowFileProperties(String id, String title, String description, String configId) {
        put("id", id);
        put("title", title);
        put("configId", configId);
        put("description", description);
    }

    public FlowOrSubFlowFileProperties(String fileName, String templateName) {
        this(UUID.randomUUID().toString(),
                DefaultFlowOrSubflowTitle.from(fileName),
                DefaultFlowOrSubflowDescription.from(fileName, templateName));
    }

    public FlowOrSubFlowFileProperties(String id, String title, String description) {
        put("id", id);
        put("title", title);
        put("description", description);
    }
}