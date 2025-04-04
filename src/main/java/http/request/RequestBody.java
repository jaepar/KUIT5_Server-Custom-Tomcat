package http.request;

import java.io.BufferedReader;
import java.io.IOException;

import static constant.HttpHeader.CONTENT_LENGTH;
import static http.util.IOUtils.readData;

public class RequestBody {
    private final String body;

    private RequestBody(String body) {
        this.body = body;
    }

    public static RequestBody of(BufferedReader br, RequestHeader requestHeader) throws IOException {
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
