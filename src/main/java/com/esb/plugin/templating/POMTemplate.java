package com.esb.plugin.templating;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.*;

public class POMTemplate {


    public void create(String groupId, String version, String artifactId, String javaVersion, String directory) throws IOException, TemplateException {
        checkArgument(groupId != null, "groupId");
        checkArgument(version != null, "version");
        checkArgument(artifactId != null, "artifactId");
        checkArgument(javaVersion != null, "javaVersion");
        checkArgument(directory != null, "directory");

        Map<String, Object> input = new HashMap<String, Object>();
        input.put("groupId", groupId);
        input.put("version", version);
        input.put("artifactId", artifactId);
        input.put("javaVersion", javaVersion);

        Template template = POMConfig.get().getTemplate("pom_template.ftl");

        Writer file = new FileWriter(Paths.get(directory, "pom.xml").toFile());
        template.process(input, file);

        file.flush();
        file.close();

    }
}
