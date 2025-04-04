package constant;

public enum Url {
    ROOT("/"),
    WEBAPP("./webapp"),
    INDEX_HTML("/index.html"),
    USER_SIGNUP("/user/signup"),
    USER_LOGIN("/user/login"),
    USER_LOGIN_FAILED_HTML("/user/login_failed.html"),
    USER_USER_LIST("/user/userList"),
    USER_LOGIN_HTML("/user/login.html"),
    USER_LIST_HTML("/user/list.html"),
    HTML_EXTENSION(".html"),
    CSS_EXTENSION(".css");

    private final String value;

    Url(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
