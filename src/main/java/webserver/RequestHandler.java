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
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
                if (line.startsWith("Cookie")) {
                    cookie = line.split(": ")[1];
                }
            }

            // 요구사항 1
            if (url.equals("/") || url.equals("/index.html")) {
                body = Files.readAllBytes(Paths.get("./webapp/index.html"));
            }

            if (method.equals("GET") && url.endsWith(".html")) {
                body = Files.readAllBytes(Paths.get("./webapp" + url));
            }

            // 요구사항 2
            if (url.contains("/user/signup") && method.equals("GET")) {
                String queryString = url.substring(url.indexOf("?") + 1);
                Map<String, String> queryParameter = parseQueryParameter(queryString);
                User user = new User(queryParameter.get("userId"), queryParameter.get("password"), queryParameter.get("name"), queryParameter.get("email"));
                repository.addUser(user);
                response302Header(dos, "/index.html");
                return;
            }

            // 요구사항 3
            if (url.equals("/user/signup") && method.equals("POST")) {
                String requestBody = readData(br, requestContentLength);
                Map<String, String> queryParameter = parseQueryParameter(requestBody);
                User user = new User(queryParameter.get("userId"), queryParameter.get("password"), queryParameter.get("name"), queryParameter.get("email"));
                repository.addUser(user);
                response302Header(dos, "/index.html");
                return;
            }

            // 요구사항 5
            if (url.equals("/user/login")) {
                String requestBody = readData(br, requestContentLength);
                Map<String, String> queryParameter = parseQueryParameter(requestBody);
                User user = repository.findUserById(queryParameter.get("userId"));

                if (user != null && user.getPassword().equals(queryParameter.get("password"))) {
                    response302HeaderWithCookie(dos, "/index.html");
                    return;
                }
                response302Header(dos, "/user/login_failed.html");
            }

            if (url.equals("/user/userList")) {
                if (!cookie.equals("logined=true")) {
                    response302Header(dos, "/user/login.html");
                    return;
                }
                body = Files.readAllBytes(Paths.get("./webapp" + "/user/list.html"));
            }


            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
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
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + url + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + url + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
