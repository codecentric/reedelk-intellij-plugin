package com.reedelk.plugin.template;

import java.util.Properties;
import java.util.UUID;

public class FlowWithRestListenerProperties extends Properties {

    public FlowWithRestListenerProperties(String configId, String flowTitle, String flowDescription, String restListenerDescription, String restPath, String restMethod, String openApiOperationObject) {
        put("id", UUID.randomUUID().toString());
        put("title", flowTitle);
        put("description", flowDescription);
        put("restDescription", restListenerDescription);
        put("restConfigId", configId);
        put("restPath", restPath);
        put("restMethod", restMethod);
        put("openApiOperationObject", openApiOperationObject);
    }
}
