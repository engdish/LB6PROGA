package server;

import server.dao.DaoException;
import server.dao.JdbcOrganizationDao;
import server.dao.JdbcProductDao;
import server.dao.JdbcUserDao;
import server.dao.OrganizationDao;
import server.dao.ProductDao;
import server.dao.UserDao;
import common.model.Coordinates;
import common.model.Organization;
import common.model.OrganizationType;
import common.model.Product;
import common.model.UnitOfMeasure;
import server.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

public class ProductDaoTest {

    public static void main(String[] args) {
        try {
            // 1) Регистрация пользователя и получение его ID
            UserDao userDao = new JdbcUserDao();
            String username = "testuser";
            String rawPassword = "pass";
            String passwordHash = sha1Hex(rawPassword);
            // Используем правильную сигнатуру: register(User)
            User createdUser = userDao.register(new User(username, passwordHash));
// вытаскиваем из него сгенерированный ID
            int userId = createdUser.getId();
            System.out.println("Registered user: id=" + userId + ", username=" + username);

            // 2) Создание организации и получение её объекта с id
            OrganizationDao orgDao = new JdbcOrganizationDao();
            Organization org = new Organization("TestOrg", "Test Organization Ltd.", OrganizationType.PUBLIC);
            Organization savedOrg = orgDao.save(org);
            System.out.println("Created organization: " + savedOrg);

            // 3) Вставка нового продукта
            ProductDao productDao = new JdbcProductDao();
            Product newProduct = new Product(
                    "TestProduct",
                    new Coordinates(5.5f, 7.7f),
                    150.0,
                    UnitOfMeasure.PCS,
                    savedOrg
            );
            Product savedProd = productDao.save(newProduct, userId);
            System.out.println("Saved product: " + savedProd);

            // 4) Чтение всех продуктов
            System.out.println("All products:");
            productDao.findAll().forEach(System.out::println);

            // 5) Поиск по ID
            Optional<Product> fetched = productDao.findById(savedProd.getId());
            System.out.println("Fetched by id=" + savedProd.getId() + ": " + fetched);

            // 6) Обновление
            Product toUpdate = new Product(
                    savedProd.getId(),
                    savedProd.getName(),
                    savedProd.getCoordinates(),
                    savedProd.getCreationDate(),
                    200.0,  // новая цена
                    savedProd.getUnitOfMeasure(),
                    savedProd.getManufacturer()
            );
            boolean updated = productDao.update(toUpdate, userId);
            System.out.println("Updated: " + updated + " → " + productDao.findById(toUpdate.getId()));

            // 7) Удаление
            boolean deleted = productDao.deleteById(savedProd.getId(), userId);
            System.out.println("Deleted: " + deleted + ", remaining count=" + productDao.findAll().size());

        } catch (DaoException e) {
            e.printStackTrace();
            System.err.println("DAO error: " + e.getMessage());
        }
    }

    // Хешируем SHA-1 и конвертируем в hex
    private static String sha1Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not available", e);
        }
    }
}
