package com.reedelk.plugin.editor.properties.widget.input.script;

public enum Type {

    OBJECT {
        @Override
        public String displayName() {
            return "Object";
        }
    },
    MESSAGE {
        @Override
        public String displayName() {
            return "Message";
        }
    },
    CONTEXT {
        @Override
        public String displayName() {
            return "FlowContext";
        }
    };

    public abstract String displayName();

}
