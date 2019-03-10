package com.esb.plugin.templating;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class POMTemplate {


    public void create(String groupId, String version, String artifactId, String javaVersion) throws IOException, TemplateException {
        Configuration cfg = new Configuration();
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("groupId", "com.testing.scenario");
        input.put("version", "1.0.0-SNAPSHOT");
        input.put("artifactId", "testsomething");
        input.put("javaVersion", "1.8");

        cfg.setClassForTemplateLoading(POMTemplate.class, "/");
        Template template = cfg.getTemplate("pom_template.ftl");

        // Write output to the console
        Writer consoleWriter = new OutputStreamWriter(System.out);
        template.process(input, consoleWriter);
    }
}
