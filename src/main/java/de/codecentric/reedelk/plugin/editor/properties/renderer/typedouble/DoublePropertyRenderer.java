package de.codecentric.reedelk.plugin.editor.properties.renderer.typedouble;

import de.codecentric.reedelk.plugin.editor.properties.commons.InputField;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractNumericPropertyRenderer;

public class DoublePropertyRenderer extends AbstractNumericPropertyRenderer<Double> {
    @Override
    protected InputField<Double> getInputField(String hint) {
        return new DoubleInputField(hint);
    }
}
