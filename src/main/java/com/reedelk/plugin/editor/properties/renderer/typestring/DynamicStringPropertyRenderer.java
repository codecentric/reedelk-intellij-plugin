package com.reedelk.plugin.editor.properties.renderer.typestring;

import com.reedelk.plugin.editor.properties.renderer.AbstractDynamicPropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.commons.DynamicValueInputFieldAdapter;
import com.reedelk.plugin.editor.properties.renderer.commons.DynamicValueInputFieldBaseAdapter;
import com.reedelk.plugin.editor.properties.renderer.commons.StringInputField;

public class DynamicStringPropertyRenderer extends AbstractDynamicPropertyTypeRenderer {

    @Override
    protected DynamicValueInputFieldAdapter inputFieldAdapter(String hint) {
        return new DynamicValueInputFieldBaseAdapter<>(new StringInputField(hint));
    }
}
