package webserver;

import controller.*;
import db.MemoryUserRepository;
import db.Repository;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static http.constant.HttpMethod.*;
import static http.constant.Url.*;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private final Repository repository;
    private Controller controller;

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
                controller = new HomeController();
            }

            if (httpRequest.getMethod().equals(GET.getValue()) && httpRequest.getUrl().endsWith(HTML_EXTENSION.getValue())
                    || httpRequest.getUrl().endsWith(CSS_EXTENSION.getValue())) {
                controller = new ForwardController();
            }

            // 요구사항 3
            if (httpRequest.getUrl().equals(USER_SIGNUP.getValue()) && httpRequest.getMethod().equals(POST.getValue())) {
                controller = new SignUpController(repository);
            }

            // 요구사항 5
            if (httpRequest.getUrl().equals(USER_LOGIN.getValue())) {
                controller = new LoginController(repository);
            }

            // 요구사항 6
            if (httpRequest.getUrl().equals(USER_USER_LIST.getValue())) {
                controller = new ListController();
            }

            controller.execute(httpRequest, httpResponse);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

}
