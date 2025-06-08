package server.dao;

import server.model.User;
import java.util.Optional;

public interface UserDao {
    /** Зарегистрировать нового пользователя; возвращает пользователя с присвоенным ID. */
    User register(User user) throws DaoException;

    /** Найти пользователя по имени. */
    Optional<User> findByUsername(String username) throws DaoException;

    /** Проверить, что имя+хэш пароля корректны. */
    boolean validate(String username, String passwordHash) throws DaoException;
    void delete(int userId) throws DaoException;
}
