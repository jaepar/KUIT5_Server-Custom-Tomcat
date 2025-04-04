package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

import static http.constant.Url.USER_LIST_HTML;
import static http.constant.Url.USER_LOGIN_HTML;

public class ListController implements Controller{
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (!httpRequest.isLogin()) {
            httpResponse.redirect(USER_LOGIN_HTML.getValue());
            return;
        }
        httpResponse.forward(USER_LIST_HTML.getValue());
    }
}
