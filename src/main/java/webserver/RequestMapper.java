package webserver;

import controller.*;
import db.Repository;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static http.constant.Url.*;

public class RequestMapper {
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;
    private final Map<String, Controller> controllerMap;
    private final Repository repository;

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse, Repository repository) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.controllerMap = new HashMap<>();
        this.repository = repository;

        initControllerMap();
    }

    private void initControllerMap() {
        controllerMap.put(ROOT.getValue(), new HomeController());
        controllerMap.put(USER_SIGNUP.getValue(), new SignUpController(repository));
        controllerMap.put(USER_LOGIN.getValue(), new LoginController(repository));
        controllerMap.put(USER_USER_LIST.getValue(), new ListController());
    }

    public void proceed() throws IOException {
        Controller controller = controllerMap.get(httpRequest.getUrl());

        if (controller == null) {
            httpResponse.forward(httpRequest.getUrl()); // forwardController 역할
            return;
        }
        controller.execute(httpRequest, httpResponse);
    }

}
