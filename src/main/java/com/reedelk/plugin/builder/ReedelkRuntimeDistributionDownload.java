package com.reedelk.plugin.builder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class ReedelkRuntimeDistributionDownload {

    private static final String LATEST_ESB_RUNTIME_DISTRIBUTION_ZIP_FILE_NAME = "reedelk-esb-runtime-latest.zip";
    private static final String DOWNLOAD_LATEST_DISTRIBUTION =
            "http://reedelk.com/distributions/" + LATEST_ESB_RUNTIME_DISTRIBUTION_ZIP_FILE_NAME;

    private ReedelkRuntimeDistributionDownload() {
    }

    public static Path download() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(DOWNLOAD_LATEST_DISTRIBUTION).get().build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() == null) {
                throw new IOException("Body was null");
            }
            try (InputStream initialStream = response.body().byteStream()) {
                Path tmpFilePath = getTmpFile();
                FileUtils.copyInputStreamToFile(initialStream, tmpFilePath.toFile());
                return tmpFilePath;
            }
        }
    }

    private static Path getTmpFile() {
        String tmpDirectory = System.getProperty("java.io.tmpdir");
        String tmpFolder = UUID.randomUUID().toString();
        return Paths.get(tmpDirectory, tmpFolder, LATEST_ESB_RUNTIME_DISTRIBUTION_ZIP_FILE_NAME);
    }
}
