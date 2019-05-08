package com.esb.plugin.fixture;

import com.esb.internal.commons.FileUtils;

import java.net.URL;

public class Json {

    interface DataProvider {
        String path();

        default String json() {
            URL url = Json.class.getResource(path());
            return FileUtils.readFrom(url);
        }
    }

    public enum GenericComponent implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "/com/esb/plugin/fixture/generic_component_sample.json";
            }
        }
    }

    public enum Fork implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "/com/esb/plugin/fixture/fork_sample.json";
            }
        }
    }

    public enum Choice implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "/com/esb/plugin/fixture/choice_sample.json";
            }
        }
    }

    public enum FlowReference implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "/com/esb/plugin/fixture/flow_reference_sample.json";
            }
        }
    }
}
