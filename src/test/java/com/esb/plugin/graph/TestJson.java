package com.esb.plugin.graph;

import java.net.URL;

public enum TestJson {

    FLOW_WITH_CHOICE {
        @Override
        String path() {
            return "/com/esb/plugin/graph/flow_with_choice.flow";
        }
    },

    LINEAR {
        @Override
        String path() {
            return "/com/esb/plugin/graph/linear.flow";
        }
    };

    public URL url() {
        return TestJson.class.getResource(path());
    }

    abstract String path();

}