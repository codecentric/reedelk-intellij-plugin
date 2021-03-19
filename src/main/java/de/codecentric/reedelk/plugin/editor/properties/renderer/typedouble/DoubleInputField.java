package de.codecentric.reedelk.plugin.editor.properties.renderer.typedouble;

import de.codecentric.reedelk.plugin.converter.ValueConverter;
import de.codecentric.reedelk.plugin.converter.ValueConverterProvider;
import de.codecentric.reedelk.plugin.editor.properties.commons.NumericDocumentFilter;
import de.codecentric.reedelk.plugin.editor.properties.commons.NumericInputField;

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
    protected ValueConverter<Double> getConverter() {
        return ValueConverterProvider.forType(Double.class);
    }
}
