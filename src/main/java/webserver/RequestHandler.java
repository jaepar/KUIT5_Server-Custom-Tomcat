package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.constant.HttpHeader.*;
import static http.constant.HttpMethod.*;
import static model.constant.UserQueryKey.*;
import static http.constant.Url.*;
import static http.util.HttpRequestUtils.parseQueryParameter;

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

            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = HttpResponse.from(dos);

            // 요구사항 1
            if (httpRequest.getUrl().equals(ROOT.getValue())) {
                httpResponse.forward(INDEX_HTML.getValue());
            }

            if (httpRequest.getMethod().equals(GET.getValue()) && httpRequest.getUrl().endsWith(HTML_EXTENSION.getValue())) {
                httpResponse.forward(httpRequest.getUrl());
            }

            // 요구사항 2
            if (httpRequest.getUrl().contains(USER_SIGNUP.getValue()) && httpRequest.getMethod().equals(GET.getValue())) {
                User user = new User(
                        httpRequest.getQueryValue(USER_ID.getValue()),
                        httpRequest.getQueryValue(PASSWORD.getValue()),
                        httpRequest.getQueryValue(NAME.getValue()),
                        httpRequest.getQueryValue(EMAIL.getValue()));

                repository.addUser(user);

                httpResponse.redirect(INDEX_HTML.getValue());
                return;
            }

            // 요구사항 3
            if (httpRequest.getUrl().equals(USER_SIGNUP.getValue()) && httpRequest.getMethod().equals(POST.getValue())) {
                String requestBody = httpRequest.getRequestBody();
                Map<String, String> queryParameter = parseQueryParameter(requestBody);
                User user = new User(
                        queryParameter.get(USER_ID.getValue()),
                        queryParameter.get(PASSWORD.getValue()),
                        queryParameter.get(NAME.getValue()),
                        queryParameter.get(EMAIL.getValue()));
                repository.addUser(user);

                httpResponse.redirect(INDEX_HTML.getValue());
                return;
            }

            // 요구사항 5
            if (httpRequest.getUrl().equals(USER_LOGIN.getValue())) {
                String requestBody = httpRequest.getRequestBody();
                Map<String, String> queryParameter = parseQueryParameter(requestBody);
                User user = repository.findUserById(queryParameter.get(USER_ID.getValue()));

                if (user != null && user.getPassword().equals(queryParameter.get(PASSWORD.getValue()))) {
                    httpResponse.putHeader(SET_COOKIE.getValue(), "logined=true");
                    httpResponse.redirect(INDEX_HTML.getValue());
                    return;
                }
                httpResponse.redirect(USER_LOGIN_FAILED_HTML.getValue());
            }

            // 요구사항 6
            if (httpRequest.getUrl().equals(USER_USER_LIST.getValue())) {
                if (!httpRequest.isLogin()) {
                    httpResponse.redirect(USER_LOGIN_HTML.getValue());
                    return;
                }
                httpResponse.forward(USER_LIST_HTML.getValue());
            }

            // 요구사항 7
            if (httpRequest.getUrl().endsWith(CSS_EXTENSION.getValue())) {
                httpResponse.forward(httpRequest.getUrl());
            }
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

}
