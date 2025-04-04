package http.request;

import http.HttpHeader;

import java.io.BufferedReader;
import java.io.IOException;

import static http.constant.HttpHeader.COOKIE;

public class HttpRequest {
    private final RequestStartLine requestStartLine;
    private final HttpHeader requestHeader;
    private final RequestBody requestBody;

    private HttpRequest(RequestStartLine requestStartLine, HttpHeader requestHeader, RequestBody requestBody) {
        this.requestStartLine = requestStartLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        RequestStartLine requestStartLine = RequestStartLine.from(br);
        HttpHeader requestHeader = HttpHeader.from(br);
        RequestBody requestBody = RequestBody.of(br, requestHeader);

        return new HttpRequest(requestStartLine, requestHeader, requestBody);
    }

    public String getUrl() {
        return requestStartLine.getUrl();
    }

    public String getMethod() {
        return requestStartLine.getMethod();
    }

    public String getQueryValue(String key) {
        return requestStartLine.getQueryMap().get(key);
    }

    public String getHeaderValue(String key) {
        return requestHeader.getHeaderMap().get(key);
    }

    public String getRequestBody() {
        return requestBody.getBody();
    }

    public boolean isLogin() {
        boolean status = false;

        if (getHeaderValue(COOKIE.getValue()) != null && getHeaderValue(COOKIE.getValue()).equals("logined=true")) {
            status = true;
        }
        return status;
    }

}
