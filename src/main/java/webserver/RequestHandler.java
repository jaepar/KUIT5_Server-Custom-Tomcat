package webserver;

import db.MemoryUserRepository;
import db.Repository;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static constant.HttpHeader.*;
import static constant.HttpMethod.*;
import static constant.QueryKey.*;
import static constant.StatusCode.*;
import static constant.Url.*;
import static http.util.HttpRequestUtils.parseQueryParameter;
import static http.util.IOUtils.readData;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    Repository repository;

    public RequestHandler(Socket connection) {
        this.connection = connection;
        this.repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String startLine = br.readLine();
            String[] startLineParts = startLine.split(" ");
            String method = startLineParts[0];
            String url = startLineParts[1];

            byte[] body = new byte[0];

            int requestContentLength = 0;
            String cookie = "";

            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                // header info
                if (line.startsWith(CONTENT_LENGTH.getValue())) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
                if (line.startsWith(COOKIE.getValue())) {
                    cookie = line.split(": ")[1];
                }
            }

            // 요구사항 1
            if (url.equals(ROOT.getValue())) {
                body = Files.readAllBytes(Paths.get(WEBAPP.getValue() + INDEX_HTML.getValue()));
            }

            if (method.equals(GET.getValue()) && url.endsWith(HTML_EXTENSION.getValue())) {
                body = Files.readAllBytes(Paths.get(WEBAPP.getValue() + url));
            }

            // 요구사항 2
            if (url.contains(USER_SIGNUP.getValue()) && method.equals(GET.getValue())) {
                String queryString = url.substring(url.indexOf("?") + 1);
                Map<String, String> queryParameter = parseQueryParameter(queryString);
                User user = new User(
                        queryParameter.get(USER_ID.getValue()),
                        queryParameter.get(PASSWORD.getValue()),
                        queryParameter.get(NAME.getValue()),
                        queryParameter.get(EMAIL.getValue()));

                repository.addUser(user);
                response302Header(dos, INDEX_HTML.getValue());
                return;
            }

            // 요구사항 3
            if (url.equals(USER_SIGNUP.getValue()) && method.equals(POST.getValue())) {
                String requestBody = readData(br, requestContentLength);
                Map<String, String> queryParameter = parseQueryParameter(requestBody);
                User user = new User(
                        queryParameter.get(USER_ID.getValue()),
                        queryParameter.get(PASSWORD.getValue()),
                        queryParameter.get(NAME.getValue()),
                        queryParameter.get(EMAIL.getValue()));
                repository.addUser(user);
                response302Header(dos, INDEX_HTML.getValue());
                return;
            }

            // 요구사항 5
            if (url.equals(USER_LOGIN.getValue())) {
                String requestBody = readData(br, requestContentLength);
                Map<String, String> queryParameter = parseQueryParameter(requestBody);
                User user = repository.findUserById(queryParameter.get(USER_ID.getValue()));

                if (user != null && user.getPassword().equals(queryParameter.get(PASSWORD.getValue()))) {
                    response302HeaderWithCookie(dos, INDEX_HTML.getValue());
                    return;
                }
                response302Header(dos, USER_LOGIN_FAILED_HTML.getValue());
            }

            // 요구사항 6
            if (url.equals(USER_USER_LIST.getValue())) {
                if (!cookie.equals("logined=true")) {
                    response302Header(dos, USER_LOGIN_HTML.getValue());
                    return;
                }
                body = Files.readAllBytes(Paths.get(WEBAPP.getValue() + USER_LIST_HTML.getValue()));
            }

            // 요구사항 7
            if (url.endsWith(CSS_EXTENSION.getValue())) {
                body = Files.readAllBytes(Paths.get(WEBAPP.getValue() + url));
                response200HeaderWithCss(dos, body.length);
                responseBody(dos, body);
                return;
            }

            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 " + OK.getValue() + " \r\n");
            dos.writeBytes( CONTENT_TYPE.getValue() + "text/html;charset=utf-8\r\n");
            dos.writeBytes(CONTENT_LENGTH.getValue() + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200HeaderWithCss(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 "+ OK.getValue() + " \r\n");
            dos.writeBytes(CONTENT_TYPE +"text/css;charset=utf-8\r\n");
            dos.writeBytes(CONTENT_LENGTH.getValue() + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 " + REDIRECT.getValue() + " \r\n");
            dos.writeBytes(LOCATION.getValue() + url + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 " + REDIRECT.getValue() + " \r\n");
            dos.writeBytes(LOCATION.getValue() + url + "\r\n");
            dos.writeBytes(SET_COOKIE.getValue() + "logined=true \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
