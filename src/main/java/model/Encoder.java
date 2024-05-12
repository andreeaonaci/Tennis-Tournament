package model;

public class Encoder {

    private Encoder() {}
    private static final Encoder INSTANCE = new Encoder();
    public static Encoder getInstance() {
        return INSTANCE;
    }
    public static String encodingPassword(String password) {
        StringBuilder encodedPassword = new StringBuilder();
        for (char c : password.toCharArray()) {
            encodedPassword.append((char) (c + 1));
        }
        return encodedPassword.toString();
    }

    public static String decodingPassword(String password) {
        StringBuilder encodedPassword = new StringBuilder();
        for (char c : password.toCharArray()) {
            encodedPassword.append((char) (c - 1));
        }
        return encodedPassword.toString();
    }
}
