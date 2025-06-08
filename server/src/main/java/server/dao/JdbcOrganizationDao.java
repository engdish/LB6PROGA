package server.dao;

import common.model.Organization;
import common.model.OrganizationType;
import server.util.DBConfig;

import java.sql.*;
import java.util.Optional;


public class JdbcOrganizationDao implements OrganizationDao {

    private static final String INSERT_SQL =
            "INSERT INTO organizations(name, full_name, type) VALUES (?, ?, ?) RETURNING id";

    private static final String SELECT_BY_ID_SQL =
            "SELECT id, name, full_name, type FROM organizations WHERE id = ?";

    @Override
    public Organization save(Organization org) throws DaoException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setString(1, org.getName());
            ps.setString(2, org.getFullName());
            // поддержка null для type
            if (org.getType() != null) {
                ps.setString(3, org.getType().toString());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong("id");
                    return new Organization(id, org.getName(), org.getFullName(), org.getType());
                } else {
                    throw new DaoException("Не удалось создать организацию в базе");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Ошибка при сохранении организации", e);
        }
    }

    @Override
    public Optional<Organization> findById(long id) throws DaoException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String fullName = rs.getString("full_name");
                    String typeStr = rs.getString("type");
                    OrganizationType type = typeStr != null ? OrganizationType.valueOf(typeStr) : null;
                    return Optional.of(new Organization(
                            rs.getLong("id"),
                            name,
                            fullName,
                            type
                    ));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException("Ошибка при получении организации", e);
        }
    }
}
