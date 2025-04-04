package model.constant;

public enum UserQueryKey {
    USER_ID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");

    private final String value;

    UserQueryKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
