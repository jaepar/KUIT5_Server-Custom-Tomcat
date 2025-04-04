package http.response;

import http.constant.StatusCode;

public class ResponseStartLine {
    private final String version = "HTTP/1.1";
    private StatusCode status;

    public ResponseStartLine() {
    }

    public String getVersion() {
        return version;
    }

    public String getStatus() {
        return status.getValue();
    }

    public void setStatus(StatusCode status) {
        this.status = status;
    }
}
