package de.codecentric.reedelk.plugin.fixture;

import de.codecentric.reedelk.runtime.api.commons.FileUtils;

import java.net.URL;

public class Json {

    private static final String FLOW_FIXTURE_BASE_PATH = "/de/codecentric/reedelk/plugin/fixture/";

    interface DataProvider {

        String path();

        default String json() {
            URL url = Json.class.getResource(FLOW_FIXTURE_BASE_PATH + path());
            return FileUtils.ReadFromURL.asString(url);
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
        Primitives() {
            @Override
            public String path() {
                return "flow/generic_component_primitives.json";
            }
        },

        PrimitivesNull() {
            @Override
            public String path() {
                return "flow/generic_component_primitives_null.json";
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

        WithTypeObjectEmpty {
            @Override
            public String path() {
                return "flow/generic_component_with_type_object_empty.json";
            }
        },

        WithTypeObjectReferenceNotPresent {
            @Override
            public String path() {
                return "flow/generic_component_with_type_object_reference_not_present.json";
            }
        },

        WithTypeObjectMissing {
            @Override
            public String path() {
                return "flow/generic_component_with_type_object_missing.json";
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
        },

        WithNotEmptyMapPropertyCustomValueType {
            @Override
            public String path() {
                return "flow/generic_component_with_not_empty_map_custom_value_type.json";
            }
        },

        WithEmptyMapPropertyCustomValueType {
            @Override
            public String path() {
                return "flow/generic_component_with_empty_map_custom_value_type.json";
            }
        },

        WithNotEmptyListProperty {
            @Override
            public String path() {
                return "flow/generic_component_with_not_empty_list.json";
            }
        },

        WithEmptyListProperty {
            @Override
            public String path() {
                return "flow/generic_component_with_empty_list.json";
            }
        },

        WithNotEmptyListPropertyCustomValueType {
            @Override
            public String path() {
                return "flow/generic_component_with_not_empty_list_custom_value_type.json";
            }
        },

        WithEmptyListPropertyCustomValueType {
            @Override
            public String path() {
                return "flow/generic_component_with_empty_list_custom_value_type.json";
            }
        },

        WithScriptProperty {
            @Override
            public String path() {
                return "flow/generic_component_with_script.json";
            }
        },

        WithComboProperty {
            @Override
            public String path() {
                return "flow/generic_component_with_combo.json";
            }
        },

        WithResourceProperty {
            @Override
            public String path() {
                return "flow/generic_component_with_resource.json";
            }
        },

        WithEnumProperty {
            @Override
            public String path() {
                return "flow/generic_component_with_enum.json";
            }
        },

        DynamicTypes {
            @Override
            public String path() {
                return "flow/generic_component_dynamic_types.json";
            }
        },

        DynamicTypesWithScript {
            @Override
            public String path() {
                return "flow/generic_component_dynamic_types_with_script.json";
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

    public enum ForEach implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "flow/foreach_sample.json";
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

    public enum TryCatch implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "flow/trycatch_sample.json";
            }
        }
    }

    public enum Unknown implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "flow/unknown_sample.json";
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

        SampleWithoutTitle() {
            @Override
            public String path() {
                return "configuration/configuration_sample_without_title.json";
            }
        },

        SampleWithEmptyConfig() {
            @Override
            public String path() {
                return "configuration/configuration_sample_empty_nested.json";
            }
        },

        SampleWithoutBoolean() {
            @Override
            public String path() {
                return "configuration/configuration_sample_without_boolean.json";
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
