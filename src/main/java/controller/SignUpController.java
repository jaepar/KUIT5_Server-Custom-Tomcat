package controller;

import db.Repository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

import static http.constant.Url.INDEX_HTML;
import static http.util.HttpRequestUtils.parseQueryParameter;
import static model.constant.UserQueryKey.*;
import static model.constant.UserQueryKey.EMAIL;

public class SignUpController implements Controller{
    private final Repository repository;

    public SignUpController(Repository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        String requestBody = httpRequest.getRequestBody();
        Map<String, String> queryParameter = parseQueryParameter(requestBody);
        User user = new User(
                queryParameter.get(USER_ID.getValue()),
                queryParameter.get(PASSWORD.getValue()),
                queryParameter.get(NAME.getValue()),
                queryParameter.get(EMAIL.getValue()));
        repository.addUser(user);

        System.out.println("여기까지 옴");
        httpResponse.redirect(INDEX_HTML.getValue());
    }
}
