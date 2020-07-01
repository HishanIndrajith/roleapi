package com.haulmatic.api.repositories;

import com.haulmatic.api.models.DetailedRole;
import com.haulmatic.api.models.Role;
import com.haulmatic.api.models.Role.RoleType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RoleRepository extends MongoRepository<DetailedRole, String> {
    List<Role> findByOrganizationAndRoleType(String organization, RoleType roleType);
}
