package com.reedelk.plugin.fixture;

import com.reedelk.runtime.commons.FileUtils;

import java.net.URL;

public class Json {

    private static final String FLOW_FIXTURE_BASE_PATH = "/com/reedelk/plugin/fixture/";

    interface DataProvider {

        String path();

        default String json() {
            URL url = Json.class.getResource(FLOW_FIXTURE_BASE_PATH + path());
            return FileUtils.readFrom(url);
        }
    }

    public enum CompleteFlow implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "flow/complete_flow_sample.json";
            }
        },

        NestedRouter() {
            @Override
            public String path() {
                return "flow/complete_flow_with_nested_router.json";
            }
        },

        NestedFork() {
            @Override
            public String path() {
                return "flow/complete_flow_with_nested_fork.json";
            }
        },

        NestedEmptyFork() {
            @Override
            public String path() {
                return "flow/complete_flow_with_empty_fork.json";
            }
        },

        NodesBetweenScopes() {
            @Override
            public String path() {
                return "flow/complete_flow_with_nodes_between_scopes.json";
            }
        }
    }

    public enum GenericComponent implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "flow/generic_component_sample.json";
            }
        },

        WithTypeObject {
            @Override
            public String path() {
                return "flow/generic_component_with_type_object.json";
            }
        },

        WithTypeObjectReference {
            @Override
            public String path() {
                return "flow/generic_component_with_type_object_reference.json";
            }
        },

        WithTypeObjectReferenceMissing {
            @Override
            public String path() {
                return "flow/generic_component_with_type_object_reference_missing.json";
            }
        },

        WithNotEmptyMapProperty {
            @Override
            public String path() {
                return "flow/generic_component_with_not_empty_map.json";
            }
        },

        WithEmptyMapProperty {
            @Override
            public String path() {
                return "flow/generic_component_with_empty_map.json";
            }
        }
    }

    public enum Fork implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "flow/fork_sample.json";
            }
        },

        WithoutSuccessorInsideScope() {
            @Override
            public String path() {
                return "flow/fork_without_successor_inside_scope.json";
            }
        }
    }

    public enum Router implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "flow/router_sample.json";
            }
        }
    }

    public enum FlowReference implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "flow/flow_reference_sample.json";
            }
        }
    }

    public enum Configuration implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "configuration/configuration_sample.json";
            }
        },

        SampleWithEmptyConfig() {
            @Override
            public String path() {
                return "configuration/configuration_sample_empty_nested.json";
            }
        },

        NestedConfig() {
            @Override
            public String path() {
                return "configuration/configuration_nested.json";
            }
        },

        NestedConfigMissingObjectProperty() {
            @Override
            public String path() {
                return "configuration/configuration_nested_missing_object_property.json";
            }
        },

        NestedConfigNullObjectProperty() {
            @Override
            public String path() {
                return "configuration/configuration_nested_null_object_property.json";
            }
        }
    }
}
