package com.esb.plugin.editor.designer.widget;

import com.esb.internal.commons.StringUtils;
import com.esb.plugin.commons.Colors;
import com.esb.plugin.commons.Fonts;
import com.esb.plugin.component.domain.ComponentData;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextComponentTitle extends AbstractText {

    private static final Pattern REGEX = Pattern.compile(".{1,18}(?:\\s|$)", Pattern.DOTALL);

    private final String title;

    public TextComponentTitle(ComponentData componentData) {
        super(Fonts.Component.TITLE, HorizontalAlignment.CENTER, VerticalAlignment.BELOW);
        this.title = componentData.getDisplayName();
    }

    @Override
    protected List<String> getText() {
        if (StringUtils.isBlank(title)) {
            return Collections.emptyList();
        }

        List<String> matchList = new ArrayList<>();
        Matcher regexMatcher = REGEX.matcher(title);
        while (regexMatcher.find()) {
            matchList.add(regexMatcher.group());
        }
        return matchList;
    }

    @Override
    protected Color getColor() {
        return Colors.TEXT_COMPONENT_TITLE;
    }

    @Override
    protected Color getSelectedColor() {
        return Colors.TEXT_COMPONENT_TITLE_SELECTED;
    }
}
