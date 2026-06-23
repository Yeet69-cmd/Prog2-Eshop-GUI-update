package domain.exceptions;

public class LoginFehlgeschlagenException extends Exception {
    public LoginFehlgeschlagenException(String benutzername) {
        super("Login fehlgeschlagen: Benutzername oder Passwort ist falsch für '" + benutzername + "'.");
    }
}