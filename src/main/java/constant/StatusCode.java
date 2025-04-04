package constant;

public enum StatusCode {
    OK("200 OK"),
    REDIRECT("302 Redirect");

    private final String value;

    StatusCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
