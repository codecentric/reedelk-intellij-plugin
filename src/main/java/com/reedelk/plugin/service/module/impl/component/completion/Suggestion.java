package com.reedelk.plugin.service.module.impl.component.completion;

import com.intellij.icons.AllIcons;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;


public class Suggestion {

    public enum Type {
        FUNCTION,
        PROPERTY
    }

    private final String name;
    private final String typeText;
    private final String lookupString;
    private final String presentableType;
    private final String presentableText;

    private final Type type;
    private final Icon icon;
    private final int cursorOffset;

    private Suggestion(Type type, String name, String lookupString, String typeText, String presentableType, String presentableText, Icon icon, int cursorOffset) {
        this.presentableText = presentableText;
        this.lookupString = lookupString;
        this.cursorOffset = cursorOffset;
        this.presentableType = presentableType;
        this.typeText = typeText;
        this.name = name;
        this.type = type;
        this.icon = icon;
    }

    public Type getType() {
        return type;
    }

    public String name() {
        return name;
    }

    public String presentableText() {
        return presentableText;
    }

    public String lookupString() {
        return lookupString;
    }

    public int cursorOffset() {
        return cursorOffset;
    }

    public String typeText() {
        return typeText;
    }

    public String presentableType() {
        if (StringUtils.isNotBlank(presentableType)) {
            return presentableType;
        }
        return PresentableTypeUtils.from(typeText); // TODO : Fixme!
    }

    public String tailText() {
        return null;
    }

    public Icon icon() {
        return icon;
    }

    public static Builder create(Type type) {
        return new Builder(type);
    }

    public static class Builder {

        private final Type type;
        private final Icon icon;

        private int cursorOffset;

        private String name;
        private String typeText;
        private String lookupString;
        private String presentableText;
        private String presentableType;

        // TODO: It is pointless to use an icon here...
        public Builder(Type type) {
            this.type = type;
            this.icon = Type.FUNCTION.equals(type) ?
                    AllIcons.Nodes.Method : AllIcons.Nodes.Variable;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withType(String typeText) {
            this.typeText = typeText;
            return this;
        }

        public Builder withCursorOffset(int cursorOffset) {
            this.cursorOffset = cursorOffset;
            return this;
        }

        public Builder withLookupString(String lookupString) {
            this.lookupString = lookupString;
            return this;
        }

        public Builder withPresentableText(String presentableText) {
            this.presentableText = presentableText;
            return this;
        }

        public Builder withPresentableType(String presentableType) {
            this.presentableType = presentableType;
            return this;
        }

        public Suggestion build() {
            if (presentableText == null) {
                presentableText = lookupString;
            }
            if (name == null) {
                name = lookupString;
            }
            return new Suggestion(type, name, lookupString, typeText, presentableType, presentableText, icon, cursorOffset);
        }
    }
}
