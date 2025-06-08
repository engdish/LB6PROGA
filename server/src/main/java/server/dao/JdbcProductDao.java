package server.dao;

import common.model.Product;
import common.model.Coordinates;
import common.model.Organization;
import common.model.UnitOfMeasure;
import server.util.DBConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class JdbcProductDao implements ProductDao {

    private static final String INSERT_SQL = """
        INSERT INTO products(name, coord_x, coord_y, price, unit, organization_id, owner_id)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        RETURNING id, creation_date
    """;

    private static final String UPDATE_SQL = """
        UPDATE products
           SET name = ?, coord_x = ?, coord_y = ?, price = ?, unit = ?, organization_id = ?
         WHERE id = ? AND owner_id = ?
    """;

    private static final String DELETE_SQL = "DELETE FROM products WHERE id = ? AND owner_id = ?";
    private static final String SELECT_ALL_SQL = "SELECT * FROM products";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM products WHERE id = ?";

    private final JdbcOrganizationDao orgDao = new JdbcOrganizationDao();

    @Override
    public Product save(Product p, int ownerId) throws DaoException {
        try (Connection conn = DBConfig.getConnection()) {
            Organization manufacturer = p.getManufacturer();
            if (manufacturer != null && manufacturer.getId() == null) {
                manufacturer = orgDao.save(manufacturer);
            }

            try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
                ps.setString(1, p.getName());
                ps.setFloat(2, p.getCoordinates().getX());
                ps.setFloat(3, p.getCoordinates().getY());
                ps.setDouble(4, p.getPrice());
                if (p.getUnitOfMeasure() != null) {
                    ps.setString(5, p.getUnitOfMeasure().name());
                } else {
                    ps.setNull(5, Types.VARCHAR);
                }
                ps.setLong(6, manufacturer != null ? manufacturer.getId() : 0L);
                ps.setInt(7, ownerId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int newId = rs.getInt("id");
                        LocalDateTime creationDate = rs.getTimestamp("creation_date").toLocalDateTime();
                        return new Product(
                                newId,
                                p.getName(),
                                p.getCoordinates(),
                                creationDate,
                                p.getPrice(),
                                p.getUnitOfMeasure(),
                                manufacturer
                        );
                    }
                }
            }
            throw new DaoException("Не удалось вставить продукт");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DaoException("Ошибка сохранения продукта", e);
        }
    }

    @Override
    public boolean update(Product p, int ownerId) throws DaoException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, p.getName());
            ps.setFloat(2, p.getCoordinates().getX());
            ps.setFloat(3, p.getCoordinates().getY());
            ps.setDouble(4, p.getPrice());
            ps.setString(5, p.getUnitOfMeasure().name());
            ps.setLong(6, p.getManufacturer().getId());
            ps.setInt(7, p.getId());
            ps.setInt(8, ownerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Ошибка обновления продукта", e);
        }
    }

    @Override
    public boolean deleteById(int id, int ownerId) throws DaoException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);
            ps.setInt(2, ownerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Ошибка удаления продукта", e);
        }
    }

    @Override
    public List<Product> findAll() throws DaoException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            List<Product> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DaoException("Ошибка чтения всех продуктов", e);
        }
    }

    @Override
    public Optional<Product> findById(int id) throws DaoException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Ошибка поиска продукта по id", e);
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException, DaoException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        Coordinates coords = new Coordinates(
                rs.getFloat("coord_x"),
                rs.getFloat("coord_y")
        );
        LocalDateTime creationDate = rs.getTimestamp("creation_date").toLocalDateTime();
        double price = rs.getDouble("price");
        String unitStr = rs.getString("unit");
        UnitOfMeasure unit = unitStr != null ? UnitOfMeasure.valueOf(unitStr) : null;


        Organization manufacturer = null;
        long orgId = rs.getLong("organization_id");
        if (!rs.wasNull()) {
            manufacturer = orgDao.findById(orgId)
                    .orElseThrow(() -> new DaoException("Организация не найдена: " + orgId));
        }

        return new Product(
                id,
                name,
                coords,
                creationDate,
                price,
                unit,
                manufacturer
        );
    }
}
