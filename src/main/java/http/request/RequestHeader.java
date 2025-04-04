package http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static constant.HttpHeader.CONTENT_LENGTH;
import static constant.HttpHeader.COOKIE;

public class RequestHeader {
    private final Map<String, String> headerMap;

    private RequestHeader(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public static RequestHeader from(BufferedReader br) throws IOException {
        Map<String, String> headerMap = new HashMap<>();

        while (true) {
            final String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            String key = line.split(": ")[0];
            String value = line.split(": ")[1];

            headerMap.put(key, value);
        }
        return new RequestHeader(headerMap);
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}
