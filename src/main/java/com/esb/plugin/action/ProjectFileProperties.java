package com.esb.plugin.action;

import com.esb.plugin.commons.DefaultFlowOrSubflowDescription;
import com.esb.plugin.commons.DefaultFlowOrSubflowTitle;

import java.util.Properties;
import java.util.UUID;

class ProjectFileProperties extends Properties {
    ProjectFileProperties(String fileName, String templateName) {
        String defaultTitle = DefaultFlowOrSubflowTitle.from(fileName);
        String defaultDescription = DefaultFlowOrSubflowDescription.from(fileName, templateName);
        put("id", UUID.randomUUID().toString());
        put("title", defaultTitle);
        put("description", defaultDescription);
    }
}
