package de.codecentric.reedelk.plugin.template;

public class Template {

    private Template() {
    }

    public enum Maven implements TemplateWriter {

        PROJECT("MavenProject"),
        MODULE("MavenModule");

        private final String templateName;

        Maven(String templateName) {
            this.templateName = templateName;
        }

        @Override
        public String templateName() {
            return templateName;
        }
    }

    public enum ProjectFile implements TemplateWriter {

        FLOW("FlowFile"),
        SUBFLOW("SubflowFile");

        private final String templateName;

        ProjectFile(String templateName) {
            this.templateName = templateName;
        }

        @Override
        public String templateName() {
            return templateName;
        }
    }

    public enum HelloWorld implements TemplateWriter {

        GET_FLOW("GETHelloWorld.flow"),
        POST_FLOW("POSTHelloWorld.flow"),
        SCRIPT("helloWorld.groovy"),
        CONFIG("DefaultListener.fconfig"),
        GIT_IGNORE(".gitignore"),
        DOCKERFILE("Dockerfile.txt"),
        HEROKU_PROCFILE("Procfile.txt");

        private final String templateName;

        HelloWorld(String templateName) {
            this.templateName = templateName;
        }

        @Override
        public String templateName() {
            return templateName;
        }
    }
}
