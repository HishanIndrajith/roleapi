package com.haulmatic.api.services;

import com.haulmatic.api.models.DetailedRole;
import com.haulmatic.api.models.Role;
import com.haulmatic.api.models.Role.RoleType;
import com.haulmatic.api.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public ResponseEntity<DetailedRole> getRoleByNic(String nic) {
        if (nic == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Optional<DetailedRole> roleData = roleRepository.findById(nic);
        return roleData.map(detailedRole -> new ResponseEntity<>(detailedRole, HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    public ResponseEntity<DetailedRole> createRole(DetailedRole detailedRole) {
        if (detailedRole.getNic() == null ||
                detailedRole.getOrganization() == null ||
                detailedRole.getFirstName() == null ||
                detailedRole.getLastName() == null ||
                detailedRole.getRoleType() == null ||
                detailedRole.getCreatedDate() != null ||
                detailedRole.getLastModifiedDate() != null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Optional<DetailedRole> roleData = roleRepository.findById(detailedRole.getNic());
        if (!roleData.isPresent()) {
            try {
                Date currentDate = new Date();
                detailedRole.setCreatedDate(currentDate);
                detailedRole.setLastModifiedDate(currentDate);
                DetailedRole detailedRoleSaved = roleRepository.save(detailedRole);
                return new ResponseEntity<>(detailedRoleSaved, HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }


    public ResponseEntity<DetailedRole> updateRole(String nic, DetailedRole detailedRole) {
        if (nic == null ||
                detailedRole.getOrganization() == null ||
                detailedRole.getFirstName() == null ||
                detailedRole.getLastName() == null ||
                detailedRole.getRoleType() == null ||
                detailedRole.getNic() != null ||
                detailedRole.getCreatedDate() != null ||
                detailedRole.getLastModifiedDate() != null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Optional<DetailedRole> roleData = roleRepository.findById(nic);
        if (roleData.isPresent()) {
            DetailedRole detailedRoleFromDb = roleData.get();
            detailedRoleFromDb.setOrganization(detailedRole.getOrganization());
            detailedRoleFromDb.setFirstName(detailedRole.getFirstName());
            detailedRoleFromDb.setLastName(detailedRole.getLastName());
            detailedRoleFromDb.setRoleType(detailedRole.getRoleType());
            detailedRoleFromDb.setLastModifiedDate(new Date());
            return new ResponseEntity<>(roleRepository.save(detailedRoleFromDb), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<HttpStatus> deleteRole(String nic) {
        if (nic == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        try {
            roleRepository.deleteById(nic);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> findByOrganization(String organization, String roleTypeStr) {
        if (organization == null || roleTypeStr == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        RoleType roleType;
        switch (roleTypeStr) {
            case "Assistant":
                roleType = RoleType.ASSISTANT;
                break;
            case "Driver":
                roleType = RoleType.DRIVER;
                break;
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            List<Role> roles = roleRepository.findByOrganizationAndRoleType(organization, roleType);

            if (roles.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(roles, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
