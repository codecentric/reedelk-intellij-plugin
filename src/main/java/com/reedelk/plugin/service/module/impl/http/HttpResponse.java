package com.reedelk.plugin.service.module.impl.http;

public class HttpResponse {

    private String body;
    private int status;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Returns true if the code is in [200..300), which means the request was successfully received,
     * understood, and accepted.
     */
    public boolean isNotSuccessful() {
        return this.status < 200 || this.status >= 300;
    }

    public boolean isNotFound() {
        return this.status == 404;
    }

}
