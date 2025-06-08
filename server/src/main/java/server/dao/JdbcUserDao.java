package server.dao;

import server.model.User;
import server.util.DBConfig;

import java.sql.*;
import java.util.Optional;

public class JdbcUserDao implements UserDao {

    private static final String INSERT_SQL =
            "INSERT INTO users(username, password_hash) VALUES (?, ?) RETURNING id";

    private static final String SELECT_BY_NAME =
            "SELECT id, username, password_hash FROM users WHERE username = ?";

    private static final String DELETE_SQL =
            "DELETE FROM users WHERE id = ?";

    @Override
    public User register(User user) throws DaoException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                return new User(id, user.getUsername(), user.getPasswordHash());
            } else {
                throw new DaoException("Не удалось получить ID при вставке пользователя");
            }
        } catch (SQLException e) {
            throw new DaoException("Ошибка при регистрации пользователя", e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) throws DaoException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_NAME)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash")
                    ));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Ошибка при поиске пользователя", e);
        }
    }

    @Override
    public boolean validate(String username, String passwordHash) throws DaoException {
        Optional<User> opt = findByUsername(username);
        return opt.map(u -> u.getPasswordHash().equals(passwordHash)).orElse(false);
    }

    @Override
    public void delete(int userId) throws DaoException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, userId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new DaoException("Пользователь не найден или не удален");
            }
        } catch (SQLException e) {
            throw new DaoException("Ошибка при удалении пользователя", e);
        }
    }
}
