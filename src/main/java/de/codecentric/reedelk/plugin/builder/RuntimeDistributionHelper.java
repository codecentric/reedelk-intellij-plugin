package de.codecentric.reedelk.plugin.builder;

import com.intellij.util.io.ZipUtil;
import de.codecentric.reedelk.plugin.commons.BuildVersion;
import de.codecentric.reedelk.plugin.commons.TmpRandomDirectory;
import okhttp3.*;
import okio.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static de.codecentric.reedelk.plugin.commons.DefaultConstants.NameConvention;
import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
import static java.lang.String.format;

class RuntimeDistributionHelper {

    private static final String DISTRIBUTION_ZIP_FILE_NAME = "distribution.zip";
    private Call call;
    private final String pluginVersion;

    public RuntimeDistributionHelper() {
        pluginVersion = BuildVersion.get();
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
            call = null;
        }
    }

    interface PushProgress {
        void onProgress(String message);
    }

    Path downloadAndUnzip(PushProgress pushProgress) throws IOException {

        final ProgressListener progressListener = (bytesRead, contentLength, done) -> {
            if (done) {
                pushProgress.onProgress(message("runtimeBuilder.downloading.distribution.complete"));
            } else {
                if (contentLength != -1) {
                    pushProgress.onProgress(format(
                            message("runtimeBuilder.downloading.distribution") + " %d%% (%d MB)",
                            (100 * bytesRead) / contentLength, bytesToMeg(contentLength)));
                }
            }
        };

        // The service returns the runtime distribution from the given plugin version.
        String downloadDistributionByPluginVersionURL =
                message("runtime.version.by.plugin.version.url", pluginVersion);
        Request request = new Request.Builder().url(downloadDistributionByPluginVersionURL).get().build();

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(chain -> {
                    Response originalResponse = chain.proceed(chain.request());
                    return originalResponse.newBuilder()
                            .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                            .build();
                })
                .build();

        this.call = client.newCall(request);

        try (Response response = call.execute()) {

            int responseCode = response.code();

            if (responseCode == 404) {
                String message = message("runtimeBuilder.downloading.distribution.error.not.found", pluginVersion);
                throw new IOException(message);
            }

            if (responseCode != 200) {
                String responseBody = response.body() != null ? response.body().string() : "";
                String message = message("runtimeBuilder.downloading.distribution.error", responseCode, responseBody);
                throw new IOException(message);
            }

            if (response.body() == null) {
                String message = message("runtimeBuilder.downloading.distribution.error.body.null");
                throw new IOException(message);
            }

            try (InputStream initialStream = response.body().byteStream()) {

                // Prepare directories
                Path tmpRandomDirectory = TmpRandomDirectory.get();
                Path distributionZipFilePath =
                        Paths.get(tmpRandomDirectory.toString(), DISTRIBUTION_ZIP_FILE_NAME);
                File distributionZipFile = distributionZipFilePath.toFile();
                FileUtils.copyInputStreamToFile(initialStream, distributionZipFile);

                // Unzip the distribution
                ZipUtil.extract(distributionZipFile, tmpRandomDirectory.toFile(), (dir, name) -> true);

                // Find distribution root folder
                String runtimeRootFolderName = findDirectoryWithPrefixInDestination(
                        tmpRandomDirectory.toFile(),
                        NameConvention.RUNTIME_DISTRIBUTION_ROOT_FOLDER_PREFIX)
                        .orElseThrow(() -> new IOException(
                                message("runtimeBuilder.downloading.distribution.error.root.folder",
                                        NameConvention.RUNTIME_DISTRIBUTION_ROOT_FOLDER_PREFIX,
                                        downloadDistributionByPluginVersionURL)));

                // The final path is the tmp random directory + the distribution folder name
                return Paths.get(tmpRandomDirectory.toString(), runtimeRootFolderName);
            }
        }
    }

    private static Optional<String> findDirectoryWithPrefixInDestination(File destination, String directoryPrefix) {
        String[] rootFolder = destination.list((dir, name) -> dir.isDirectory() && name.startsWith(directoryPrefix));
        return (rootFolder == null || rootFolder.length == 0) ?
                Optional.empty() :
                Optional.of(rootFolder[0]);
    }

    private static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;

        ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }

    interface ProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }

    private static final long  MEGABYTE = 1024L * 1024L;

    public static long bytesToMeg(long bytes) {
        return bytes / MEGABYTE ;
    }
}
