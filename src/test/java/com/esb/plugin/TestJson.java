package com.esb.plugin;

import com.esb.internal.commons.FileUtils;

import java.net.URL;

public enum TestJson {

    CHOICE_WITH_NODE_OUTSIDE_SCOPE {
        @Override
        String path() {
            return "/com/esb/plugin/component/choice/choice_with_node_outside_scope.json";
        }
    },
    FLOW_WITH_CHOICE {
        @Override
        String path() {
            return "/com/esb/plugin/graph/flow_with_choice.flow";
        }
    },
    FLOW_WITH_FORK {
        @Override
        String path() {
            return "/com/esb/plugin/graph/flow_with_fork.flow";
        }
    };

    public String asJson() {
        URL url = TestJson.class.getResource(path());
        return FileUtils.readFrom(url);
    }

    abstract String path();

}