package com.reedelk.plugin.editor.designer.misc;

import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.Fonts;
import com.reedelk.plugin.commons.SplitTextInLines;
import com.reedelk.plugin.editor.designer.text.AbstractText;

import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

public class FlowErrorText extends AbstractText {

    private static final Pattern REGEX = Pattern.compile(".{1,100}(?:\\s|$)", Pattern.DOTALL);
    private final String text;

    FlowErrorText(String text) {
        super(Fonts.Component.DESCRIPTION, HorizontalAlignment.CENTER, VerticalAlignment.BELOW);
        this.text = text;
    }

    @Override
    protected Color getColor() {
        return Colors.PROPERTIES_EMPTY_SELECTION_TEXT;
    }

    @Override
    protected List<String> getText() {
        return SplitTextInLines.from(text, REGEX);
    }
}
