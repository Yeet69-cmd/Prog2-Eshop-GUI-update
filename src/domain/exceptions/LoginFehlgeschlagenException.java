package domain.exceptions;

public class LoginFehlgeschlagenException extends Exception {

    public LoginFehlgeschlagenException() {
        super("Login fehlgeschlagen.");
    }
}