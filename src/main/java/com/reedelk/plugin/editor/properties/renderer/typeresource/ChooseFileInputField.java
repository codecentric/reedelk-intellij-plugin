package com.reedelk.plugin.editor.properties.renderer.typeresource;

import com.intellij.ui.components.fields.ExtendableTextField;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.HintPainter;

import java.awt.*;

public class ChooseFileInputField extends ExtendableTextField {

    private final String hintText;

    ChooseFileInputField(String hintText, int columns) {
        super(columns);
        this.hintText = hintText;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        HintPainter.from(g, this, hintText, Colors.PROPERTIES_FILE_INPUT_FIELD_HINT);
    }
}
