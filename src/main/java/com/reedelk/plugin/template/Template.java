package com.reedelk.plugin.template;

public class Template {

    private Template() {
    }

    public static class Maven {

        private Maven() {
        }

        public static final String PROJECT = "MavenProject";
        public static final String MODULE = "MavenModule";
    }

    public static class ProjectFile {

        private ProjectFile() {
        }

        public static final String FLOW = "FlowFile";
        public static final String SUBFLOW = "SubflowFile";
    }

    public static class HelloWorld {

        private HelloWorld() {
        }

        public static final String GET_FLOW = "GETHelloWorld.flow";
        public static final String POST_FLOW = "POSTHelloWorld.flow";
        public static final String SCRIPT = "helloWorld.groovy";
        public static final String CONFIG = "DefaultListener.fconfig";
        public static final String GIT_IGNORE = ".gitignore";

    }
}
