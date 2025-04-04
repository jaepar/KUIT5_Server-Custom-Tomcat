package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

import static http.constant.Url.INDEX_HTML;

public class HomeController implements Controller{
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.forward(INDEX_HTML.getValue());
    }
}
