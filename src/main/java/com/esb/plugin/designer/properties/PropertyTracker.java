package com.esb.plugin.designer.properties;

public interface PropertyTracker {

    String getValueAsString();

    void onPropertyChange(Object newValue);

}