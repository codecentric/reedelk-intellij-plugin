package com.esb.plugin.fixture;

import com.esb.internal.commons.FileUtils;

import java.net.URL;

public class Json {

    private static final String FIXTURE_BASE_PATH = "/com/esb/plugin/fixture/";

    interface DataProvider {

        String path();

        default String json() {
            URL url = Json.class.getResource(FIXTURE_BASE_PATH + path());
            return FileUtils.readFrom(url);
        }
    }

    public enum CompleteFlow implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "complete_flow_sample.json";
            }
        },

        NestedChoice() {
            @Override
            public String path() {
                return "complete_flow_with_nested_choice.json";
            }
        },

        NodesBetweenScopes() {
            @Override
            public String path() {
                return "complete_flow_with_nodes_between_scopes.json";
            }
        }
    }

    public enum GenericComponent implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "generic_component_sample.json";
            }
        }
    }

    public enum Fork implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "fork_sample.json";
            }
        }
    }

    public enum Choice implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "choice_sample.json";
            }
        }
    }

    public enum FlowReference implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "flow_reference_sample.json";
            }
        }
    }
}
