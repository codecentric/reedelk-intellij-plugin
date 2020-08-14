package com.reedelk.plugin.action.openapi.reader;

import com.intellij.openapi.components.ServiceManager;
import com.reedelk.plugin.action.openapi.OpenApiException;
import com.reedelk.plugin.commons.FileUtils;
import com.reedelk.plugin.service.module.impl.http.HttpResponse;
import com.reedelk.plugin.service.module.impl.http.HttpService;

import java.io.IOException;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class Readers {

    private Readers() {
    }

    public static class FileSystemFileReader implements FileReader {

        private final String openApiFile;

        public FileSystemFileReader(String openApiFile) {
            this.openApiFile = openApiFile;
        }

        @Override
        public String read() throws OpenApiException {
            try {
                return FileUtils.readFileToString(openApiFile);
            } catch (IOException exception) {
                String message = message("openapi.importer.fetch.file.error", openApiFile, exception.getMessage());
                throw new OpenApiException(message);
            }
        }
    }

    public static class RemoteFileReader implements FileReader {

        private final String openApiURL;

        public RemoteFileReader(String openApiURL) {
            this.openApiURL = openApiURL;
        }

        @Override
        public String read() throws OpenApiException {
            HttpService service = ServiceManager.getService(HttpService.class);
            HttpResponse httpResponse;

            try {
                httpResponse = service.get(openApiURL);
            } catch (IOException exception) {
                String message = message("openapi.importer.fetch.url.error", openApiURL, exception.getMessage());
                throw new OpenApiException(message);
            }

            if (httpResponse.isSuccessful()) {
                return httpResponse.getBody();
            } else {
                String message = message("openapi.importer.fetch.url.error", openApiURL, "response status code is " + httpResponse.getStatus());
                throw new OpenApiException(message);
            }
        }
    }
}
