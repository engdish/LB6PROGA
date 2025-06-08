package server.dao;

import common.model.Organization;

import java.util.Optional;

public interface OrganizationDao {

    Organization save(Organization org) throws DaoException;

    Optional<Organization> findById(long id) throws DaoException;
}
