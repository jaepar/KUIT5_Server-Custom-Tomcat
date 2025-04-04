package http.request;

import http.util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestStartLine {
    private final String method;
    private final String url;
    private final String version;
    private final Map<String, String> queryMap;

    private RequestStartLine(String method, String url, String version, Map<String, String> queryMap) {
        this.method = method;
        this.url = url;
        this.version = version;
        this.queryMap = queryMap;
    }

    public static RequestStartLine from(BufferedReader br) throws IOException {
        String startLine = br.readLine();
        String[] startLineParts = startLine.split(" ");
        String method = startLineParts[0];
        String url = startLineParts[1];
        String version = startLineParts[2];
        Map<String, String> queryMap = new HashMap<>();

        if (url.contains("?")) {
            String queryString = url.substring(url.indexOf("?") + 1);
            queryMap = HttpRequestUtils.parseQueryParameter(queryString);
        }

        return new RequestStartLine(method, url, version, queryMap);
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getQueryMap() {
        return queryMap;
    }
}
