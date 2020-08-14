package com.reedelk.plugin.template;

public class OpenAPIRESTListenerConfig extends RESTListenerConfig {

    public void setBasePath(String basePath) {
        put("basePath", basePath);
    }

    public void setOpenApiObject(String openApiObject) {
        put("openApiObject", openApiObject);
    }
}
