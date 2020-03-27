package com.reedelk.plugin.editor.properties.renderer.typedouble;

import com.reedelk.plugin.editor.properties.commons.DynamicValueInputFieldAdapter;
import com.reedelk.plugin.editor.properties.commons.DynamicValueInputFieldBaseAdapter;
import com.reedelk.plugin.editor.properties.renderer.AbstractDynamicPropertyTypeRenderer;

public class DynamicDoublePropertyRenderer extends AbstractDynamicPropertyTypeRenderer {

    @Override
    protected DynamicValueInputFieldAdapter inputFieldAdapter(String hint) {
        return new DynamicValueInputFieldBaseAdapter<>(new DoubleInputField(hint));
    }
}
