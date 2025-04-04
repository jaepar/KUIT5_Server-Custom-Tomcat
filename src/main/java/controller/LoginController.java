package controller;

import db.Repository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

import static http.constant.HttpHeader.SET_COOKIE;
import static http.constant.Url.INDEX_HTML;
import static http.constant.Url.USER_LOGIN_FAILED_HTML;
import static http.util.HttpRequestUtils.parseQueryParameter;
import static model.constant.UserQueryKey.PASSWORD;
import static model.constant.UserQueryKey.USER_ID;

public class LoginController implements Controller{
    private final Repository repository;

    public LoginController(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
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
}
