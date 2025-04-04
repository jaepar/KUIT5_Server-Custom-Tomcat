package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeader {
    private final Map<String, String> headerMap;

    public HttpHeader(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public static HttpHeader from(BufferedReader br) throws IOException {
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
        return new HttpHeader(headerMap);
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void putHeader(String key, String value) {
        headerMap.put(key, value);
    }

}
