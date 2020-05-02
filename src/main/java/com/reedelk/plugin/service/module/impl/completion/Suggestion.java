package com.reedelk.plugin.service.module.impl.completion;

import com.intellij.icons.AllIcons;

import javax.swing.*;


public class Suggestion {

    public enum Type {
        FUNCTION,
        PROPERTY
    }

    private final String presentableText;
    private final String lookupString;
    private final String typeText;
    private final int cursorOffset;
    private final Icon icon;

    private Suggestion(String lookupString, String presentableText, String typeText, Icon icon, int cursorOffset) {
        this.presentableText = presentableText;
        this.lookupString = lookupString;
        this.cursorOffset = cursorOffset;
        this.typeText = typeText;
        this.icon = icon;
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

    public Icon icon() {
        return icon;
    }

    public static Builder create(Type type) {
        return new Builder(type);
    }

    static class Builder {

        private String presentableText;
        private String lookupString;
        private String typeText;
        private int cursorOffset;
        private final Icon icon;

        public Builder(Type type) {
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

        public Builder withCursorOffset(int cursorOffset) {
            this.cursorOffset = cursorOffset;
            return this;
        }

        public Suggestion build() {
            return new Suggestion(lookupString, presentableText, typeText, icon, cursorOffset);
        }
    }
}
