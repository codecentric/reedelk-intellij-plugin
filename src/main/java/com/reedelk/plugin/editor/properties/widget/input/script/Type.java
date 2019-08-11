package com.reedelk.plugin.editor.properties.widget.input.script;

public enum Type {

    OBJECT {
        @Override
        public String displayName() {
            return "Object";
        }
    },
    MAP {
        @Override
        public String displayName() {
            return "Map";
        }
    },
    MESSAGE {
        @Override
        public String displayName() {
            return "Message";
        }
    },
    ANY {
        @Override
        public String displayName() {
            return "Any";
        }
    };

    public abstract String displayName();

}
