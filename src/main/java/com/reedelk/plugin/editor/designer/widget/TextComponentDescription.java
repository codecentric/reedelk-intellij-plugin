package com.reedelk.plugin.editor.designer.widget;

import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.Fonts;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.runtime.commons.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class TextComponentDescription extends AbstractText {

    private static final Pattern REGEX = Pattern.compile(".{1,18}(?:\\s|$)", Pattern.DOTALL);

    private ComponentData componentData;

    public TextComponentDescription(ComponentData componentData) {
        super(Fonts.Component.DESCRIPTION, HorizontalAlignment.CENTER, VerticalAlignment.BELOW);
        this.componentData = componentData;
    }

    @Override
    protected List<String> getText() {
        String description = componentData.get(Implementor.description());
        if (StringUtils.isBlank(description)) {
            return Collections.emptyList();
        }

        List<String> matchList = new ArrayList<>();
        Matcher regexMatcher = REGEX.matcher(description);
        while (regexMatcher.find()) {
            matchList.add(regexMatcher.group());
        }
        return matchList;
    }

    @Override
    protected Color getColor() {
        return Colors.TEXT_COMPONENT_DESCRIPTION;
    }

    @Override
    protected Color getSelectedColor() {
        return Colors.TEXT_COMPONENT_DESCRIPTION_SELECTED;
    }
}
