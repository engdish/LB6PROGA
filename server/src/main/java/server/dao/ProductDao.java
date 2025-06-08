package server.dao;

import common.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductDao {

    Product save(Product product, int ownerId) throws DaoException;

    boolean update(Product product, int ownerId) throws DaoException;

    boolean deleteById(int id, int ownerId) throws DaoException;


    List<Product> findAll() throws DaoException;

    Optional<Product> findById(int id) throws DaoException;
}
