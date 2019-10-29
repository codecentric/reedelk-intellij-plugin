package com.reedelk.plugin.fixture;

import com.reedelk.runtime.commons.FileUtils;

import java.net.URL;

public enum JsonSchema {

    SIMPLE {
        @Override
        String path() {
            return "simple.schema.json";
        }
    },
    WITH_REFERENCE {
        @Override
        String path() {
            return "with_reference.schema.json";
        }
    },
    REFERENCED {
        @Override
        String path() {
            return "referenced.schema.json";
        }
    };

    abstract String path();

    public String json() {
        URL url = Json.class.getResource(FIXTURE_BASE_PATH + path());
        return FileUtils.ReadFromURL.asString(url);
    }

    private static final String FIXTURE_BASE_PATH = "/com/reedelk/plugin/fixture/schema/";
}
