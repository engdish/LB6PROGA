package server.model;

import java.util.Objects;

public class User {
    private final int id;
    private final String username;
    private final String passwordHash;

    public User(int id, String username, String passwordHash) {
        this.id = id;
        this.username = Objects.requireNonNull(username);
        this.passwordHash = Objects.requireNonNull(passwordHash);
    }

    // Конструктор для регистрационных целей (ID ещё не известен)
    public User(String username, String passwordHash) {
        this(0, username, passwordHash);
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
}
