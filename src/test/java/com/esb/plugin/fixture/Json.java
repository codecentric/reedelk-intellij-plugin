package com.esb.plugin.fixture;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.SampleJson;

import java.net.URL;

public class Json {

    interface DataProvider {
        String path();

        default String asJson() {
            URL url = SampleJson.class.getResource(path());
            return FileUtils.readFrom(url);
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

    public enum Fork implements DataProvider {
        Sample() {
            @Override
            public String path() {
                return "/com/esb/plugin/fixture/fork_sample.json";
            }
        }
    }

}
