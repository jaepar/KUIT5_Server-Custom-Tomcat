package constant;

public enum QueryKey {
    USER_ID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");

    private final String value;

    QueryKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
