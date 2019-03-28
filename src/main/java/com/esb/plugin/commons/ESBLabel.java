package com.esb.plugin.commons;

import java.util.ResourceBundle;

public enum ESBLabel {

    DEFAULT_GROUP_ID("defaultGroupId"),
    DEFAULT_VERSION("defaultVersion"),
    DEFAULT_ARTIFACT_ID("defaultArtifactId"),
    MODULE_BUILDER_NAME("moduleBuilderName"),
    MODULE_BUILDER_DESCRIPTION("moduleBuilderDescription");


    String labelKey;

    ESBLabel(String labelKey) {
        this.labelKey = labelKey;
    }

    public String get() {
        ResourceBundle bundle = ResourceBundle.getBundle("com/esb/plugin/Labels");
        return bundle.getString(labelKey);
    }

}
