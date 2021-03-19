package de.codecentric.reedelk.plugin.editor.properties.renderer.typefloat;

import de.codecentric.reedelk.plugin.converter.ValueConverter;
import de.codecentric.reedelk.plugin.converter.ValueConverterProvider;
import de.codecentric.reedelk.plugin.editor.properties.commons.NumericDocumentFilter;
import de.codecentric.reedelk.plugin.editor.properties.commons.NumericInputField;

import javax.swing.text.DocumentFilter;

public class FloatInputField extends NumericInputField<Float> {

    public FloatInputField(String hint) {
        super(hint);
    }

    @Override
    protected DocumentFilter getInputFilter() {
        return new NumericDocumentFilter(value -> {
            try {
                Float.parseFloat(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    @Override
    protected ValueConverter<Float> getConverter() {
        return ValueConverterProvider.forType(Float.class);
    }
}
