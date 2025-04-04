package constant;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type: "),
    CONTENT_LENGTH("Content-Length: "),
    LOCATION(":Location: "),
    SET_COOKIE(":Set-Cookie: "),
    COOKIE("Cookie: ");

    private final String value;

    HttpHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
