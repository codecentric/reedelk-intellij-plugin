package com.reedelk.plugin.service.module.impl.completion;

import com.intellij.icons.AllIcons;
import com.reedelk.plugin.commons.ToPresentableType;

import javax.swing.*;


public class Suggestion {

    private final String name;

    public enum Type {
        FUNCTION,
        PROPERTY
    }

    private final Type type;
    private final String presentableText;
    private final String lookupString;
    private final String typeText;
    private final int cursorOffset;
    private final Icon icon;

    private Suggestion(Type type, String name, String lookupString, String presentableText, String typeText, Icon icon, int cursorOffset) {
        this.name = name;
        this.type = type;
        this.presentableText = presentableText;
        this.lookupString = lookupString;
        this.cursorOffset = cursorOffset;
        this.typeText = typeText;
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
        return ToPresentableType.from(typeText);
    }

    public Icon icon() {
        return icon;
    }

    public static Builder create(Type type) {
        return new Builder(type);
    }

    static class Builder {

        private final Type type;
        private String presentableText;
        private String lookupString;
        private String typeText;
        private String name;
        private int cursorOffset;
        private final Icon icon;

        public Builder(Type type) {
            this.type = type;
            this.icon = Type.FUNCTION.equals(type) ?
                    AllIcons.Nodes.Method : AllIcons.Nodes.Variable;
        }

        public Builder withPresentableText(String presentableText) {
            this.presentableText = presentableText;
            return this;
        }

        public Builder withLookupString(String lookupString) {
            this.lookupString = lookupString;
            return this;
        }

        public Builder withType(String type) {
            this.typeText = type;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withCursorOffset(int cursorOffset) {
            this.cursorOffset = cursorOffset;
            return this;
        }

        public Suggestion build() {
            if (presentableText == null) {
                presentableText = lookupString;
            }
            if (name == null) {
                name = lookupString;
            }
            return new Suggestion(type, name, lookupString, presentableText, typeText, icon, cursorOffset);
        }
    }
}
