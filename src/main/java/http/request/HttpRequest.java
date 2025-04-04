package http.request;

import java.io.BufferedReader;
import java.io.IOException;

import static constant.HttpHeader.COOKIE;

public class HttpRequest {
    private final RequestStartLine requestStartLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    private HttpRequest(RequestStartLine requestStartLine, RequestHeader requestHeader, RequestBody requestBody) {
        this.requestStartLine = requestStartLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        RequestStartLine requestStartLine = RequestStartLine.from(br);
        RequestHeader requestHeader = RequestHeader.from(br);
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
