package de.codecentric.reedelk.plugin.editor.properties.renderer.typedouble;

import de.codecentric.reedelk.plugin.editor.properties.commons.DynamicValueInputFieldAdapter;
import de.codecentric.reedelk.plugin.editor.properties.commons.DynamicValueInputFieldBaseAdapter;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractDynamicPropertyTypeRenderer;

public class DynamicDoublePropertyRenderer extends AbstractDynamicPropertyTypeRenderer {

    @Override
    protected DynamicValueInputFieldAdapter inputFieldAdapter(String hint) {
        return new DynamicValueInputFieldBaseAdapter<>(new DoubleInputField(hint));
    }
}
