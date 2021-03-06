package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.plugin.template.Template;

public class DefaultFlowOrSubflowDescription {

    private DefaultFlowOrSubflowDescription() {
    }

    public static String from(String name, String templateName) {
        String description = SplitWords.from(name);
        if (Template.ProjectFile.FLOW.templateName().equals(templateName)) {
            description += " Flow";
        } else if (Template.ProjectFile.SUBFLOW.templateName().equals(templateName)) {
            description += " Subflow";
        }
        return description;
    }
}
