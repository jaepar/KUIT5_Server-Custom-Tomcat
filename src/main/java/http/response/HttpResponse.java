package http.response;

import http.HttpHeader;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static http.constant.HttpHeader.*;
import static http.constant.StatusCode.OK;
import static http.constant.StatusCode.REDIRECT;
import static http.constant.Url.WEBAPP;

public class HttpResponse {
    private final ResponseStartLine responseStartLine = new ResponseStartLine();
    private final HttpHeader httpHeader;
    private byte[] body;
    private final DataOutputStream dos;

    private HttpResponse(HttpHeader httpHeader, byte[] body, OutputStream os) {
        this.httpHeader = httpHeader;
        this.body = body;
        this.dos = new DataOutputStream(os);
    }

    public static HttpResponse from(OutputStream os) {
        HttpHeader httpHeader = new HttpHeader(new HashMap<>());
        byte[] body = new byte[0];

        return new HttpResponse(httpHeader, body, os);
    }

    public void forward(String url) throws IOException {
        // startLine
        responseStartLine.setStatus(OK);

        // header
        String type = "text/html";
        if (url.contains("css")) {
            type = "text/css";
        }
        putHeader(CONTENT_TYPE.getValue(), type + ";charset=utf-8");

        // body
        body = Files.readAllBytes(Paths.get(WEBAPP.getValue() + url));
        putHeader(CONTENT_LENGTH.getValue(), String.valueOf(body.length));

        write();
    }

    public void redirect(String url) throws IOException {
        responseStartLine.setStatus(REDIRECT);

        putHeader(LOCATION.getValue(), url);

        write();
    }

    public void putHeader(String key, String value) {
        httpHeader.putHeader(key, value);
    }

    private void write() throws IOException {
        // startline
        dos.writeBytes(responseStartLine.getVersion() + " " + responseStartLine.getStatus() + "\r\n");

        // header
        for (Map.Entry<String, String> entry : httpHeader.getHeaderMap().entrySet()) {
            dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
        dos.writeBytes("\r\n");

        // body
        dos.write(body, 0, body.length);

        dos.flush();
        dos.close();
    }


}
