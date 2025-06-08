package server.dao;

import common.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductDao {
    /** Сохраняет новый продукт; возвращает его с сгенерированным id и датой создания */
    Product save(Product product, int ownerId) throws DaoException;

    /** Обновляет продукт (по id и ownerId); true, если запись была обновлена */
    boolean update(Product product, int ownerId) throws DaoException;

    /** Удаляет продукт по id и ownerId; true, если запись была удалена */
    boolean deleteById(int id, int ownerId) throws DaoException;

    /** Возвращает все продукты из БД */
    List<Product> findAll() throws DaoException;

    /** Ищет продукт по id */
    Optional<Product> findById(int id) throws DaoException;
}
