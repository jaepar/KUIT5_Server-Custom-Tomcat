package http.request;

import http.HttpHeader;

import java.io.BufferedReader;
import java.io.IOException;

import static http.constant.HttpHeader.CONTENT_LENGTH;
import static http.util.IOUtils.readData;

public class RequestBody {
    private final String body;

    private RequestBody(String body) {
        this.body = body;
    }

    public static RequestBody of(BufferedReader br, HttpHeader requestHeader) throws IOException {
        if (requestHeader.getHeaderMap().containsKey(CONTENT_LENGTH.getValue())) {
            int requestContentLength = Integer.parseInt(requestHeader.getHeaderMap().get(CONTENT_LENGTH.getValue()));
            String body = readData(br, requestContentLength);
            return new RequestBody(body);
        }
        return new RequestBody("");
    }

    public String getBody() {
        return body;
    }
}
