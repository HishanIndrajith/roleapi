package com.haulmatic.api.repositories;

import com.haulmatic.api.entities.Role;
import com.haulmatic.api.entities.Role.RoleType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RoleRepository extends MongoRepository<Role, String> {
    List<Role> findByOrganizationAndRoleType(String organization, RoleType roleType);
}
