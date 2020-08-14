package com.reedelk.plugin.template;

public class FlowWithRestListenerAndResourceProperties extends FlowWithRestListenerProperties {

    public FlowWithRestListenerAndResourceProperties(String configId,
                                                     String flowTitle,
                                                     String flowDescription,
                                                     String restListenerDescription,
                                                     String restPath,
                                                     String restMethod,
                                                     String openApiOperationObject,
                                                     String exampleResourceFile,
                                                     String exampleMimeType) {
        super(configId, flowTitle, flowDescription, restListenerDescription, restPath, restMethod, openApiOperationObject);
        put("exampleResourceFile", exampleResourceFile);
        put("exampleMimeType", exampleMimeType);
    }
}
