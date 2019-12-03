package com.reedelk.plugin.template;

public class Template {

    private Template() {
    }

    public class Maven {

        private Maven() {
        }

        public static final String PROJECT = "MavenProject";
        public static final String MODULE = "MavenModule";
    }

    public class ProjectFile {

        private ProjectFile() {
        }

        public static final String FLOW = "FlowFile";
        public static final String SUBFLOW = "SubflowFile";
    }

    public class HelloWorld {

        private HelloWorld() {
        }

        public static final String FLOW = "HelloWorld.flow";
        public static final String SCRIPT = "HelloWorld.js";
        public static final String CONFIG = "DefaultListener.fconfig";

    }
}
