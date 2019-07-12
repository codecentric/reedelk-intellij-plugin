package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.editor.properties.widget.input.InputChangeListener;

public interface DefaultPanelContext {

    void subscribe(String propertyName, InputChangeListener<?> inputChangeListener);

    <T> void notify(String propertyName, T newValue);

    <T> T getPropertyValue(String propertyName);
}
