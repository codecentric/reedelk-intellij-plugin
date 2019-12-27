package com.reedelk.plugin.editor.properties.renderer.commons;

import com.reedelk.plugin.commons.PluginValueConverterProvider;
import com.reedelk.runtime.converter.PluginValueConverter;

public class StringInputField extends InputField<String> {

    public StringInputField(String hint) {
        super(hint);
    }

    public StringInputField(String hint, int columns) {
        super(hint, columns);
    }

    @Override
    protected PluginValueConverter<String> getConverter() {
        return PluginValueConverterProvider.forType(String.class);
    }
}
