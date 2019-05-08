package com.esb.plugin.fixture;

import com.esb.internal.commons.FileUtils;
import com.esb.plugin.SampleJson;

import java.net.URL;

public class Json {

    public enum Choice {

        Sample() {
            @Override
            String path() {
                return "/com/esb/plugin/fixture/choice_sample.json";
            }
        };

        public String asJson() {
            URL url = SampleJson.class.getResource(path());
            return FileUtils.readFrom(url);
        }

        abstract String path();

    }
}
