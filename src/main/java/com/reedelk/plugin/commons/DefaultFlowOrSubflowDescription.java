package com.reedelk.plugin.commons;

import com.reedelk.plugin.template.Template;

public class DefaultFlowOrSubflowDescription {

    private DefaultFlowOrSubflowDescription() {
    }

    public static String from(String name, String templateName) {
        String description = SplitWords.from(name);
        if (Template.ProjectFile.FLOW.equals(templateName)) {
            description += " Flow";
        } else if (Template.ProjectFile.SUBFLOW.equals(templateName)) {
            description += " Subflow";
        }
        return description;
    }
}
