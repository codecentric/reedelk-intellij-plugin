package com.reedelk.plugin.editor.properties.renderer.typedouble;

import com.reedelk.plugin.commons.PluginValueConverterProvider;
import com.reedelk.plugin.editor.properties.renderer.commons.NumericDocumentFilter;
import com.reedelk.plugin.editor.properties.renderer.commons.NumericInputField;
import com.reedelk.runtime.converter.PluginValueConverter;

import javax.swing.text.DocumentFilter;

public class DoubleInputField extends NumericInputField<Double> {

    public DoubleInputField(String hint) {
        super(hint);
    }

    @Override
    protected DocumentFilter getInputFilter() {
        return new NumericDocumentFilter(value -> {
            try {
                Double.parseDouble(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    @Override
    protected PluginValueConverter<Double> getConverter() {
        return PluginValueConverterProvider.forType(Double.class);
    }
}
