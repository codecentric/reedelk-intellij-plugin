package de.codecentric.reedelk.plugin.editor.properties.renderer.typefloat;

import de.codecentric.reedelk.plugin.editor.properties.commons.InputField;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractNumericPropertyRenderer;

public class FloatPropertyRenderer extends AbstractNumericPropertyRenderer<Float> {
    @Override
    protected InputField<Float> getInputField(String hint) {
        return new FloatInputField(hint);
    }
}
