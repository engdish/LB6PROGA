package server.dao;

import server.model.User;
import java.util.Optional;

public interface UserDao {
    User register(User user) throws DaoException;

    Optional<User> findByUsername(String username) throws DaoException;

    boolean validate(String username, String passwordHash) throws DaoException;
    void delete(int userId) throws DaoException;
}
